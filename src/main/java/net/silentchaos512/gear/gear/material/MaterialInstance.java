package net.silentchaos512.gear.gear.material;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.enchantment.IStatModifierEnchantment;
import net.silentchaos512.gear.api.event.GetMaterialStatsEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ChargedProperties;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.part.RepairContext;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.utils.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class MaterialInstance implements IMaterialInstance {
    private static final Map<ResourceLocation, MaterialInstance> QUICK_CACHE = new HashMap<>();

    private final IMaterial material;
    @Deprecated private final MaterialGrade grade;
    private final ItemStack item;
    private ImmutableList<IMaterialModifier> modifiers = ImmutableList.of(); // Start empty, build when needed

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

    public static IMaterialInstance of(DataResource<IMaterial> material, ItemStack craftingItem) {
        if (material.isPresent())
            return of(material.get(), craftingItem);
        return LazyMaterialInstance.of(material, MaterialGrade.fromStack(craftingItem));
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
    public ResourceLocation getId() {
        return material.getId();
    }

    @Nonnull
    @Override
    public IMaterial get() {
        return material;
    }

    @Override
    public MaterialGrade getGrade() {
        return grade;
    }

    @Override
    public Collection<IMaterialModifier> getModifiers() {
        if (modifiers.isEmpty()) {
            modifiers = ImmutableList.copyOf(MaterialModifiers.readFromMaterial(this));
        }
        return modifiers;
    }

    @Override
    public MaterialList getMaterials() {
        return material.getMaterials(this);
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public Collection<IMaterialCategory> getCategories() {
        return material.getCategories(this);
    }

    @Override
    public Ingredient getIngredient() {
        return material.getIngredient();
    }

    @Override
    public int getTier(PartType partType) {
        return this.material.getTier(this, partType);
    }

    @Override
    public Collection<StatInstance> getStatModifiers(PartType partType, StatGearKey key, ItemStack gear) {
        List<StatInstance> mods = new ArrayList<>(material.getStatModifiers(this, partType, key, gear));

        ItemStat stat = ItemStats.get(key.getStat());
        if (stat == null) {
            SilentGear.LOGGER.warn("Unknown item stat: {}", key.getStat().getStatId());
            SilentGear.LOGGER.catching(new NullPointerException());
            return mods;
        }

//        getEnchantmentModifiedStats(mods, key);

        // Material modifiers (grades, starcharged, etc.)
        for (IMaterialModifier materialModifier : getModifiers()) {
            mods = materialModifier.modifyStats(partType, key, mods);
        }

        GetMaterialStatsEvent event = new GetMaterialStatsEvent(this, stat, partType, mods);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Deprecated
    private void getEnchantmentModifiedStats(List<StatInstance> mods, StatGearKey key) {
        if (key.getStat() == ItemStats.CHARGING_VALUE) {
            return;
        }

        // Search for materials that stats
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(this.item).entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer level = entry.getValue();

            if (enchantment instanceof IStatModifierEnchantment) {
                IStatModifierEnchantment statModifierEnchantment = (IStatModifierEnchantment) enchantment;
                ChargedProperties charge = new ChargedProperties(level, getChargeability());

                // Replace modifiers with updated ones (if provided)
                for (int i = 0; i < mods.size(); i++) {
                    StatInstance mod = mods.get(i);
                    StatInstance newMod = statModifierEnchantment.modifyStat(key, mod, charge);
                    if (newMod != null) {
                        mods.remove(i);
                        mods.add(i, newMod);
                    }
                }
            }
        }
    }

    private float getChargeability() {
        return getStat(PartType.MAIN, ItemStats.CHARGING_VALUE);
    }

    @Override
    public float getStat(PartType partType, StatGearKey key, ItemStack gear) {
        ItemStat stat = ItemStats.get(key.getStat());
        if (stat == null) return key.getStat().getDefaultValue();

        return stat.compute(stat.getDefaultValue(), getStatModifiers(partType, key));
    }

    @Override
    public Collection<TraitInstance> getTraits(PartGearKey partKey, ItemStack gear) {
        return material.getTraits(this, partKey, gear);
    }

    public boolean canRepair(ItemStack gear) {
        return material.allowedInPart(this, PartType.MAIN) && GearData.getTier(gear) <= this.getTier(PartType.MAIN);
    }

    public int getRepairValue(ItemStack gear) {
        return this.getRepairValue(gear, RepairContext.Type.QUICK);
    }

    public int getRepairValue(ItemStack gear, RepairContext.Type type) {
        if (this.canRepair(gear)) {
            float durability = getStat(PartType.MAIN, GearHelper.getDurabilityStat(gear));
            float repairValueMulti = getStat(PartType.MAIN, ItemStats.REPAIR_VALUE);
            float itemRepairModifier = GearHelper.getRepairModifier(gear);
            float typeBonus = 1f + type.getBonusEfficiency();
            return Math.round(durability * repairValueMulti * itemRepairModifier * typeBonus) + 1;
        }
        return 0;
    }

    @Nullable
    public static MaterialInstance read(CompoundTag nbt) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("ID"));
        IMaterial material = MaterialManager.get(id);
        if (material == null) return null;

        ItemStack stack = readOrGetDefaultItem(material, nbt);
        return of(material, stack);
    }

    private static ItemStack readOrGetDefaultItem(IMaterial material, CompoundTag nbt) {
        ItemStack stack = ItemStack.of(nbt.getCompound("Item"));
        if (stack.isEmpty()) {
            // Item is missing from NBT, so pick something from the ingredient
            ItemStack[] array = material.getIngredient().getItems();
            if (array.length > 0) {
                return array[0].copy();
            }
        }
        return stack;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        nbt.putString("ID", material.getId().toString());
        nbt.put("Item", item.save(new CompoundTag()));
        return nbt;
    }

    public int getPrimaryColor(GearType gearType, PartType partType) {
        IMaterialDisplay model = getDisplayProperties();
        MaterialLayer layer = model.getLayerList(gearType, partType, this).getFirstLayer();
        if (layer != null) {
            return layer.getColor();
        }
        return Color.VALUE_WHITE;
    }

    @Override
    public Component getDisplayName(PartType partType, ItemStack gear) {
        return material.getDisplayName(this, partType, gear);
    }

    @Override
    public IMaterialDisplay getDisplayProperties() {
        return this.material.getDisplayProperties(this);
    }

    @Override
    public String getModelKey() {
        return this.material.getModelKey(this);
    }

    @Override
    public int getNameColor(PartType partType, GearType gearType) {
        return material.getNameColor(this, partType, gearType);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.material.getId());
        buffer.writeEnum(this.grade);
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

    @Deprecated
    public static String writeShorthand(IMaterialInstance material) {
        String id = SilentGear.shortenId(material.getId());
        if (material.getGrade() != MaterialGrade.NONE) {
            return id + "#" + material.getGrade();
        }
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialInstance that = (MaterialInstance) o;
        return material.equals(that.material) &&
                grade == that.grade &&
                InventoryUtils.canItemsStack(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, grade, item);
    }
}
