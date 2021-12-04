package net.silentchaos512.gear.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.SynergyUtils;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.NameUtils;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class CompoundPartItem extends Item {
    private static final String NBT_CRAFTED_COUNT = "CraftedCount";
    private static final String NBT_MATERIALS = "Materials";

    private final PartType partType;

    public CompoundPartItem(PartType partType, Properties properties) {
        super(properties);
        this.partType = partType;
    }

    public PartType getPartType() {
        return partType;
    }

    public GearType getGearType() {
        return GearType.PART;
    }

    public int getCraftedCount(ItemStack stack) {
        if (stack.getOrCreateTag().contains(NBT_CRAFTED_COUNT)) {
            return stack.getOrCreateTag().getInt(NBT_CRAFTED_COUNT);
        }

        // Crafted count not stored... Base the value on the default recipes.
        if (this == ModItems.BINDING.get()) return getMaterials(stack).size();
        if (this == ModItems.CORD.get()) return 1;
        if (this == ModItems.COATING.get()) return getMaterials(stack).size();
        if (this == ModItems.FLETCHING.get()) return getMaterials(stack).size();
        if (this == ModItems.GRIP.get()) return getMaterials(stack).size();
        if (this == ModItems.LONG_ROD.get()) return 2;
        if (this == ModItems.ROD.get()) return 4;
        if (this == ModItems.TIP.get()) return getMaterials(stack).size();

        SilentGear.LOGGER.error("Unknown part with no crafted count: {}", stack);
        return 1;
    }

    public ItemStack create(IMaterialInstance material) {
        return create(material, -1);
    }

    public ItemStack create(Collection<? extends IMaterialInstance> materials) {
        return create(MaterialList.of(materials), -1);
    }

    public ItemStack create(IMaterialInstance material, int craftedCount) {
        return create(MaterialList.of(material), craftedCount);
    }

    public ItemStack create(MaterialList materials) {
        return create(materials, -1);
    }

    public ItemStack create(MaterialList materials, int craftedCount) {
        ListTag materialListNbt = materials.serializeNbt();

        CompoundTag tag = new CompoundTag();
        tag.put(NBT_MATERIALS, materialListNbt);
        if (craftedCount > 0) {
            tag.putInt(NBT_CRAFTED_COUNT, craftedCount);
        }

        ItemStack result = new ItemStack(this, craftedCount > 0 ? craftedCount : 1);
        result.setTag(tag);
        return result;
    }

    public static MaterialList getMaterials(ItemStack stack) {
        ListTag materialListNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Tag.TAG_COMPOUND);
        return MaterialList.deserializeNbt(materialListNbt);
    }

    @Nullable
    public static MaterialInstance getPrimaryMaterial(ItemStack stack) {
        ListTag listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, 10);
        if (!listNbt.isEmpty()) {
            Tag nbt = listNbt.get(0);
            if (nbt instanceof CompoundTag) {
                return MaterialInstance.read((CompoundTag) nbt);
            }
        }
        return null;
    }

    public static String getModelKey(ItemStack stack) {
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + "#");

        if (!stack.hasTag()) {
            return s.append(Const.Materials.EXAMPLE.getId()).toString();
        }

        for (IMaterialInstance material : getMaterials(stack)) {
            s.append(SilentGear.shortenId(material.getId()));
        }

        return s.toString();
    }

    public int getColor(ItemStack stack, int layer) {
        return ColorUtils.getBlendedColor(this, getMaterials(stack), layer);
    }

    public int getColorWeight(int index, int totalCount) {
        return totalCount - index;
    }

    @Override
    public Component getName(ItemStack stack) {
        PartData part = PartData.from(stack);
        MaterialInstance material = getPrimaryMaterial(stack);
        if (part != null && material != null) {
            TranslatableComponent nameText = new TranslatableComponent(this.getDescriptionId() + ".nameProper", material.getDisplayName(partType, ItemStack.EMPTY));
            int nameColor = Color.blend(part.getColor(ItemStack.EMPTY), Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
            return TextUtil.withColor(nameText, nameColor);
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        PartData part = PartData.from(stack);

        if (part != null && Config.Client.showPartTooltips.get()) {
            float synergy = SynergyUtils.getSynergy(this.partType, getMaterials(stack), part.getTraits());
            tooltip.add(SynergyUtils.getDisplayText(synergy));

            TextListBuilder matsBuilder = new TextListBuilder();
            getMaterials(stack).forEach(material -> {
                int nameColor = material.getNameColor(part.getType(), this.getGearType());
                matsBuilder.add(TextUtil.withColor(material.getDisplayNameWithModifiers(part.getType(), ItemStack.EMPTY), nameColor));
            });
            tooltip.addAll(matsBuilder.build());
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (!allowdedIn(group) || this == ModItems.SHIELD_PLATE.get()) {
            return;
        }
        items.add(create(LazyMaterialInstance.of(Const.Materials.EXAMPLE)));
    }
}
