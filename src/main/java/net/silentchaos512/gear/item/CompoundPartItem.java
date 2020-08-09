package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.SynergyUtils;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public ItemStack create(List<? extends IMaterialInstance> materials) {
        return create(materials, -1);
    }

    public ItemStack create(IMaterialInstance material, int craftedCount) {
        return create(Collections.singletonList(material), craftedCount);
    }

    public ItemStack create(List<? extends IMaterialInstance> materials, int craftedCount) {
        ListNBT materialListNbt = new ListNBT();
        materials.forEach(m -> materialListNbt.add(m.write(new CompoundNBT())));

        CompoundNBT tag = new CompoundNBT();
        tag.put(NBT_MATERIALS, materialListNbt);
        if (craftedCount > 0) {
            tag.putInt(NBT_CRAFTED_COUNT, craftedCount);
        }

        ItemStack result = new ItemStack(this, craftedCount > 0 ? craftedCount : 1);
        result.setTag(tag);
        return result;
    }

    public static List<MaterialInstance> getMaterials(ItemStack stack) {
        ListNBT materialListNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, 10);
        return materialListNbt.stream()
                .filter(nbt -> nbt instanceof CompoundNBT)
                .map(nbt -> (CompoundNBT) nbt)
                .map(MaterialInstance::read)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

        for (MaterialInstance material : getMaterials(stack)) {
            s.append(SilentGear.shortenId(material.getMaterialId()));
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
        MaterialInstance material = getPrimaryMaterial(stack);
        if (material != null) {
            return new TranslationTextComponent(this.getTranslationKey() + ".nameProper", material.getDisplayName(partType));
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PartData part = PartData.from(stack);
        if (part != null) {
            float synergy = SynergyUtils.getSynergy(this.partType, getMaterials(stack), part.getTraits());
            tooltip.add(SynergyUtils.getDisplayText(synergy));
        }
        getMaterials(stack).stream()
                .map(mat -> {
                    ITextComponent gradeText = mat.getDisplayNameWithGrade(this.partType);
                    gradeText.getStyle().setFormatting(TextFormatting.ITALIC);
                    return new StringTextComponent("- ").func_230529_a_(gradeText);
                })
                .forEach(tooltip::add);
    }
}
