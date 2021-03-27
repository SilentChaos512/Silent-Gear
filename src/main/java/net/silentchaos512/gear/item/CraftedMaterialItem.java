package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.List;

public class CraftedMaterialItem extends Item implements IColoredMaterialItem {
    private static final String NBT_MATERIAL = "Material";

    public CraftedMaterialItem(Properties properties) {
        super(properties);
    }

    public static IMaterialInstance getMaterial(ItemStack stack) {
        INBT nbt = stack.getOrCreateTag().get(NBT_MATERIAL);

        if (nbt instanceof CompoundNBT) {
            // Read full material information
            MaterialInstance mat = MaterialInstance.read((CompoundNBT) nbt);
            if (mat != null) {
                return mat;
            }
        } else if (nbt != null) {
            // Remain compatible with pre-2.6.15 items
            String id = nbt.getString();
            IMaterial mat = MaterialManager.get(SilentGear.getIdWithDefaultNamespace(id));
            if (mat != null) {
                return MaterialInstance.of(mat);
            }
        }

        return LazyMaterialInstance.of(Const.Materials.EXAMPLE);
    }

    public ItemStack create(IMaterialInstance material, int count) {
        ItemStack result = new ItemStack(this, count);
        result.getOrCreateTag().put(NBT_MATERIAL, material.write(new CompoundNBT()));
        return result;
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        IMaterialInstance material = getMaterial(stack);
        IMaterialDisplay model = MaterialDisplayManager.get(material);
        return model.getLayerColor(GearType.ALL, PartType.MAIN, material, layer);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        IMaterialInstance material = getMaterial(stack);
        return new TranslationTextComponent(this.getTranslationKey(), material.getDisplayName(PartType.MAIN));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            items.add(create(LazyMaterialInstance.of(Const.Materials.EXAMPLE), 1));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getOrCreateTag().get(NBT_MATERIAL) instanceof StringNBT) {
            tooltip.add(new StringTextComponent("Has an older NBT format").mergeStyle(TextFormatting.RED));
            tooltip.add(new StringTextComponent("May not stack with newer items").mergeStyle(TextFormatting.RED));
        }
    }
}
