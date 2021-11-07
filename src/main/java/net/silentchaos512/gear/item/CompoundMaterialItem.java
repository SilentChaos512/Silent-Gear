package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
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
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class CompoundMaterialItem extends Item implements IColoredMaterialItem {
    public CompoundMaterialItem(Properties properties) {
        super(properties);
    }

    public static MaterialList getSubMaterials(ItemStack stack) {
        MaterialList ret = MaterialList.empty();

        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        if (!listNbt.isEmpty()) {
            for (INBT nbt : listNbt) {
                IMaterial mat = MaterialManager.get(SilentGear.getIdWithDefaultNamespace(nbt.getAsString()));
                if (mat != null) {
                    ret.add(MaterialInstance.of(mat));
                }
            }
        } else {
            ListNBT list = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_COMPOUND);
            return MaterialList.deserializeNbt(list);
        }

        return ret;
    }

    public int getCraftedCount(ItemStack stack) {
        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        return listNbt.size();
    }

    public ItemStack create(MaterialList materials) {
        return create(materials, materials.size());
    }

    public ItemStack create(MaterialList materials, int craftedCount) {
        ItemStack result = new ItemStack(this, craftedCount);
        result.getOrCreateTag().put(NBT_MATERIALS, materials.serializeNbt());
        return result;
    }

    @Nullable
    private static IMaterialInstance getPrimaryMaterial(ItemStack stack) {
        IMaterialInstance first = MaterialList.deserializeFirst(stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_COMPOUND));
        if (first != null) {
            return first;
        }

        // Read old style
        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        if (!listNbt.isEmpty()) {
            INBT nbt = listNbt.get(0);
            ResourceLocation id = ResourceLocation.tryParse(nbt.getAsString());
            if (id != null) {
                IMaterial material = MaterialManager.get(id);
                if (material != null) {
                    return MaterialInstance.of(material);
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public IMaterialInstance getPrimarySubMaterial(ItemStack stack) {
        return getPrimaryMaterial(stack);
    }

    public static String getModelKey(ItemStack stack) {
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + "#");

        if (!stack.hasTag()) {
            return s.append(Const.Materials.EXAMPLE.getId()).toString();
        }

        for (IMaterialInstance material : getSubMaterials(stack)) {
            s.append(SilentGear.shortenId(material.getId()));
        }

        return s.toString();
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        return ColorUtils.getBlendedColor(this, getSubMaterials(stack), layer);
    }

    public int getColorWeight(int index, int totalCount) {
        // TODO: Is this even needed? Should probably axe material order mattering.
        return 1;
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        IMaterialInstance material = getPrimaryMaterial(stack);
        ITextComponent text = material != null ? material.getDisplayName(PartType.MAIN) : TextUtil.misc("unknown");
        return new TranslationTextComponent(this.getDescriptionId(), text);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(Config.Client.showMaterialTooltips.get()) {
            TextUtil.addWipText(tooltip);

            Collection<IMaterialInstance> materials = getSubMaterials(stack);

            TextListBuilder statsBuilder = new TextListBuilder();
            for (IMaterialInstance material : materials) {
                int nameColor = material.getNameColor(PartType.MAIN, GearType.ALL);
                statsBuilder.add(TextUtil.withColor(material.getDisplayName(PartType.MAIN).copy(), nameColor));
            }
            tooltip.addAll(statsBuilder.build());
        }
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            items.add(create(MaterialList.of(LazyMaterialInstance.of(Const.Materials.EXAMPLE)), 1));
        }
    }
}
