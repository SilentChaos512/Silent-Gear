package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.NameUtils;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CompoundMaterialItem extends Item implements IColoredMaterialItem {
    private static final String NBT_MATERIALS = "Materials";

    public CompoundMaterialItem(Properties properties) {
        super(properties);
    }

    public static List<MaterialInstance> getSubMaterials(ItemStack stack) {
        List<MaterialInstance> ret = new ArrayList<>();
        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        for (INBT nbt : listNbt) {
            IMaterial mat = MaterialManager.get(SilentGear.getIdWithDefaultNamespace(nbt.getString()));
            if (mat != null) {
                ret.add(MaterialInstance.of(mat));
            }
        }
        return ret;
    }

    @Nullable
    public static MaterialInstance getPrimarySubMaterial(ItemStack stack) {
        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        for (INBT nbt : listNbt) {
            IMaterial mat = MaterialManager.get(SilentGear.getIdWithDefaultNamespace(nbt.getString()));
            if (mat != null) {
                return MaterialInstance.of(mat);
            }
        }
        return null;
    }

    public int getCraftedCount(ItemStack stack) {
        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        return listNbt.size();
    }

    public ItemStack create(List<? extends IMaterialInstance> materials) {
        return create(materials, materials.size());
    }

    public ItemStack create(List<? extends IMaterialInstance> materials, int craftedCount) {
        ListNBT materialListNbt = new ListNBT();
        for (IMaterialInstance mat : materials) {
            materialListNbt.add(StringNBT.valueOf(mat.getMaterialId().toString()));
        }

        CompoundNBT tag = new CompoundNBT();
        tag.put(NBT_MATERIALS, materialListNbt);

        ItemStack result = new ItemStack(this, materials.size());
        result.setTag(tag);
        return result;
    }

    @Nullable
    public static MaterialInstance getPrimaryMaterial(ItemStack stack) {
        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        if (!listNbt.isEmpty()) {
            INBT nbt = listNbt.get(0);
            ResourceLocation id = ResourceLocation.tryCreate(nbt.getString());
            if (id != null) {
                IMaterial material = MaterialManager.get(id);
                if (material != null) {
                    return MaterialInstance.of(material);
                }
            }
        }
        return null;
    }

    public static String getModelKey(ItemStack stack) {
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + "#");

        if (!stack.hasTag()) {
            return s.append(Const.Materials.EXAMPLE.getId()).toString();
        }

        for (MaterialInstance material : getSubMaterials(stack)) {
            s.append(SilentGear.shortenId(material.getMaterialId()));
        }

        return s.toString();
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        return ColorUtils.getBlendedColor(this, getSubMaterials(stack), layer);
    }

    public int getColorWeight(int index, int totalCount) {
        return totalCount - index;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        PartData part = PartData.from(stack);
        MaterialInstance material = getPrimaryMaterial(stack);
        if (part != null && material != null) {
            TranslationTextComponent nameText = new TranslationTextComponent(this.getTranslationKey() + ".nameProper", material.getDisplayName(PartType.MAIN));
            int nameColor = Color.blend(part.getColor(ItemStack.EMPTY), Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
            return TextUtil.withColor(nameText, nameColor);
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Collection<MaterialInstance> materials = getSubMaterials(stack);

        TextListBuilder statsBuilder = new TextListBuilder();
        for (MaterialInstance material : materials) {
            int nameColor = material.getMaterial().getNameColor(PartType.MAIN, GearType.ALL);
            statsBuilder.add(TextUtil.withColor(material.getDisplayName(PartType.MAIN), nameColor));
        }
        tooltip.addAll(statsBuilder.build());
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!isInGroup(group)) {
            return;
        }
        items.add(create(Collections.singletonList(LazyMaterialInstance.of(Const.Materials.EXAMPLE)), 1));
    }
}
