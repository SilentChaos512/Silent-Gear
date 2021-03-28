package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.TextListBuilder;
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
        if (this == ModItems.BOWSTRING.get()) return 1;
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
        ListNBT materialListNbt = materials.serializeNbt();

        CompoundNBT tag = new CompoundNBT();
        tag.put(NBT_MATERIALS, materialListNbt);
        if (craftedCount > 0) {
            tag.putInt(NBT_CRAFTED_COUNT, craftedCount);
        }

        ItemStack result = new ItemStack(this, craftedCount > 0 ? craftedCount : 1);
        result.setTag(tag);
        return result;
    }

    public static MaterialList getMaterials(ItemStack stack) {
        ListNBT materialListNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_COMPOUND);
        return MaterialList.deserializeNbt(materialListNbt);
    }

    @Nullable
    public static MaterialInstance getPrimaryMaterial(ItemStack stack) {
        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, 10);
        if (!listNbt.isEmpty()) {
            INBT nbt = listNbt.get(0);
            if (nbt instanceof CompoundNBT) {
                return MaterialInstance.read((CompoundNBT) nbt);
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
    public ITextComponent getDisplayName(ItemStack stack) {
        PartData part = PartData.from(stack);
        MaterialInstance material = getPrimaryMaterial(stack);
        if (part != null && material != null) {
            TranslationTextComponent nameText = new TranslationTextComponent(this.getTranslationKey() + ".nameProper", material.getDisplayName(partType, ItemStack.EMPTY));
            int nameColor = Color.blend(part.getColor(ItemStack.EMPTY), Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
            return TextUtil.withColor(nameText, nameColor);
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PartData part = PartData.from(stack);
        if (part != null) {
            float synergy = SynergyUtils.getSynergy(this.partType, getMaterials(stack), part.getTraits());
            tooltip.add(SynergyUtils.getDisplayText(synergy));

            TextListBuilder matsBuilder = new TextListBuilder();
            getMaterials(stack).forEach(material -> {
                int nameColor = material.getNameColor(part.getType(), this.getGearType());
                matsBuilder.add(TextUtil.withColor(material.getDisplayNameWithGrade(part.getType(), ItemStack.EMPTY), nameColor));
            });
            tooltip.addAll(matsBuilder.build());
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!isInGroup(group) || this == ModItems.ARMOR_BODY.get() || this == ModItems.SHIELD_PLATE.get()) {
            return;
        }
        items.add(create(LazyMaterialInstance.of(Const.Materials.EXAMPLE)));
    }
}
