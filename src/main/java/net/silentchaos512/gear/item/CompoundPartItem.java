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
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.SynergyUtils;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompoundPartItem extends Item {
    private static final String NBT_MATERIALS = "Materials";

    private final PartType partType;
    private final int tintLayer;

    public CompoundPartItem(PartType partType, Properties properties) {
        this(partType, 0, properties);
    }

    public CompoundPartItem(PartType partType, int tintLayer, Properties properties) {
        super(properties);
        this.partType = partType;
        this.tintLayer = tintLayer;
    }

    public PartType getPartType() {
        return partType;
    }

    public GearType getGearType() {
        return GearType.PART;
    }

    public ItemStack createFromItems(Collection<ItemStack> materials) {
        // TODO: Ignores invalid items, is that the best thing to do?
        return create(materials.stream()
                .map(MaterialManager::from)
                .filter(Objects::nonNull)
                .map(MaterialInstance::of)
                .collect(Collectors.toList()));
    }

    public ItemStack create(IMaterialInstance material) {
        return create(Collections.singletonList(material));
    }

    public ItemStack create(List<? extends IMaterialInstance> materials) {
        ListNBT materialListNbt = new ListNBT();
        materials.forEach(m -> materialListNbt.add(m.write(new CompoundNBT())));

        CompoundNBT tag = new CompoundNBT();
        tag.put(NBT_MATERIALS, materialListNbt);

        ItemStack result = new ItemStack(this);
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
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + ":");

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
