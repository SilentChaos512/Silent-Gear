package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.List;

public class CustomMaterialItem extends Item implements IColoredMaterialItem {
    private static final String NBT_MATERIAL = "Material";

    public CustomMaterialItem(Properties properties) {
        super(properties);
    }

    public ItemStack create(IMaterialInstance material) {
        return create(material, 1);
    }

    public ItemStack create(IMaterialInstance material, int count) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString(NBT_MATERIAL, SilentGear.shortenId(material.getId()));

        ItemStack result = new ItemStack(this, count);
        result.setTag(tag);
        return result;
    }

    @Nullable
    public static MaterialInstance getMaterial(ItemStack stack) {
        String str = stack.getOrCreateTag().getString(NBT_MATERIAL);
        ResourceLocation id = SilentGear.getIdWithDefaultNamespace(str);
        if (id != null) {
            IMaterial material = MaterialManager.get(id);
            if (material != null) {
                return MaterialInstance.of(material);
            }
        }
        return null;
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        if (layer == 0) {
            MaterialInstance material = getMaterial(stack);
            if (material != null) {
                return material.getPrimaryColor(GearType.ALL, PartType.MAIN);
            }
        }
        return Color.VALUE_WHITE;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        MaterialInstance material = getMaterial(stack);
        if (material != null) {
            return new TranslationTextComponent(this.getTranslationKey(), material.getDisplayName(PartType.MAIN));
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextUtil.withColor(TextUtil.misc("wip"), TextFormatting.RED));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            items.add(create(LazyMaterialInstance.of(Const.Materials.EXAMPLE)));
        }
    }
}
