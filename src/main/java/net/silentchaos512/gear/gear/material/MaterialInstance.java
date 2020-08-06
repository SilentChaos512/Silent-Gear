package net.silentchaos512.gear.gear.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetMaterialStatsEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.utils.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class MaterialInstance implements IMaterialInstance {
    private static final Map<ResourceLocation, MaterialInstance> QUICK_CACHE = new HashMap<>();

    private final IMaterial material;
    private final MaterialGrade grade;
    private final ItemStack item;

    private MaterialInstance(IMaterial material) {
        this(material, MaterialGrade.NONE, material.getDisplayItem(PartType.MAIN, 0));
    }

    private MaterialInstance(IMaterial material, MaterialGrade grade) {
        this(material, grade, material.getDisplayItem(PartType.MAIN, 0));
    }

    private MaterialInstance(IMaterial material, ItemStack craftingItem) {
        this(material, MaterialGrade.NONE, craftingItem);
    }

    private MaterialInstance(IMaterial material, MaterialGrade grade, ItemStack craftingItem) {
        this.material = material;
        this.grade = grade;
        this.item = craftingItem.copy();
        this.item.setCount(1);
    }

    public static MaterialInstance of(IMaterial material) {
        return QUICK_CACHE.computeIfAbsent(material.getId(), id -> new MaterialInstance(material));
    }

    public static MaterialInstance of(IMaterial material, MaterialGrade grade) {
        return new MaterialInstance(material, grade);
    }

    public static MaterialInstance of(IMaterial material, ItemStack craftingItem) {
        return new MaterialInstance(material, MaterialGrade.fromStack(craftingItem), craftingItem);
    }

    public static MaterialInstance of(IMaterial material, MaterialGrade grade, ItemStack craftingItem) {
        return new MaterialInstance(material, grade, craftingItem);
    }

    @Nullable
    public static MaterialInstance from(ItemStack stack) {
        IMaterial material = MaterialManager.from(stack);
        if (material != null) {
            return of(material, stack);
        }
        return null;
    }

    @Override
    public ResourceLocation getMaterialId() {
        return material.getId();
    }

    @Nonnull
    @Override
    public IMaterial getMaterial() {
        return material;
    }

    @Override
    public MaterialGrade getGrade() {
        return grade;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public int getTier(PartType partType) {
        return material.getTier(partType);
    }

    public Collection<PartType> getPartTypes() {
        return material.getPartTypes();
    }

    public Collection<StatInstance> getStatModifiers(ItemStat stat, PartType partType) {
        return getStatModifiers(stat, partType, ItemStack.EMPTY);
    }

    public Collection<StatInstance> getStatModifiers(ItemStat stat, PartType partType, ItemStack gear) {
        Collection<StatInstance> mods = material.getStatModifiers(stat, partType, gear);
        if (stat.isAffectedByGrades() && grade != MaterialGrade.NONE) {
            // Apply grade bonus to all modifiers. Makes it easier to see the effect on rods and such.
            float bonus = 1f + grade.bonusPercent / 100f;
            mods = mods.stream().map(m -> new StatInstance(m.getValue() * bonus, m.getOp())).collect(Collectors.toList());
        }
        GetMaterialStatsEvent event = new GetMaterialStatsEvent(this, stat, partType, mods);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public float getStat(ItemStat stat, PartType partType, ItemStack gear) {
        return stat.compute(stat.getDefaultValue(), getStatModifiers(stat, partType, gear));
    }

    public boolean canRepair(ItemStack gear) {
        return material.allowedInPart(PartType.MAIN) && GearData.getTier(gear) <= this.getTier(PartType.MAIN);
    }

    public int getRepairValue(ItemStack gear) {
        if (this.canRepair(gear)) {
            float durability = getStat(GearHelper.getDurabilityStat(gear), PartType.MAIN);
            float repairEfficiency = getStat(ItemStats.REPAIR_EFFICIENCY, PartType.MAIN);
            float itemRepairModifier = GearHelper.getRepairModifier(gear);
            return Math.round(durability * repairEfficiency * itemRepairModifier) + 1;
        }
        return 0;
    }

    @Nullable
    public static MaterialInstance read(CompoundNBT nbt) {
        ResourceLocation id = ResourceLocation.tryCreate(nbt.getString("ID"));
        IMaterial material = MaterialManager.get(id);
        if (material == null) return null;

        ItemStack stack = readOrGetDefaultItem(material, nbt);
        return of(material, stack);
    }

    private static ItemStack readOrGetDefaultItem(IMaterial material, CompoundNBT nbt) {
        ItemStack stack = ItemStack.read(nbt.getCompound("Item"));
        if (stack.isEmpty()) {
            // Item is missing from NBT, so pick something from the ingredient
            ItemStack[] array = material.getIngredient().getMatchingStacks();
            if (array.length > 0) {
                return array[0].copy();
            }
        }
        return stack;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putString("ID", material.getId().toString());
        nbt.put("Item", item.write(new CompoundNBT()));
        return nbt;
    }

    @Deprecated
    @Override
    public int getColor(PartType partType, ItemStack gear) {
        return material.getPrimaryColor(gear, partType);
    }

    public int getPrimaryColor(GearType gearType, PartType partType) {
        IMaterialDisplay model = MaterialDisplayManager.get(this.material);
        if (model != null) {
            MaterialLayer layer = model.getLayers(gearType, partType).getFirstLayer();
            if (layer != null) {
                return layer.getColor();
            }
        }
        return Color.VALUE_WHITE;
    }

    @Override
    public IFormattableTextComponent getDisplayName(PartType partType, ItemStack gear) {
        return material.getDisplayName(partType, gear);
    }

    public ITextComponent getDisplayNameWithGrade(PartType partType, ItemStack gear) {
        IFormattableTextComponent text = getDisplayName(partType, gear).copyRaw();
        if (this.grade != MaterialGrade.NONE) {
            text.func_240702_b_(" (").func_230529_a_(this.grade.getDisplayName()).func_240702_b_(")");
        }
        return text;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.material.getId());
        buffer.writeEnumValue(this.grade);
    }

    @Nullable
    public static MaterialInstance readShorthand(String str) {
        if (str.contains("#")) {
            String[] parts = str.split("#");
            ResourceLocation id = SilentGear.getIdWithDefaultNamespace(parts[0]);
            IMaterial material = MaterialManager.get(id);
            if (material != null) {
                MaterialGrade grade = MaterialGrade.fromString(parts[1]);
                return new MaterialInstance(material, grade);
            }

            return null;
        }

        ResourceLocation id = SilentGear.getIdWithDefaultNamespace(str);
        IMaterial material = MaterialManager.get(id);
        if (material != null) {
            return new MaterialInstance(material);
        }

        return null;
    }

    public static String writeShorthand(MaterialInstance material) {
        if (material.grade != MaterialGrade.NONE) {
            return material.getMaterialId() + "#" + material.grade;
        }
        return material.getMaterialId().toString();
    }
}
