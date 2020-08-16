package net.silentchaos512.gear.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;

public class FragmentItem extends Item {
    private static final String NBT_MATERIAL = "Material";

    public FragmentItem(Properties properties) {
        super(properties);
    }

    public ItemStack create(IMaterial material, int count) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTag().putString(NBT_MATERIAL, material.getId().toString());
        return stack;
    }

    @Nullable
    public static IMaterial getMaterial(ItemStack stack) {
        ResourceLocation id = ResourceLocation.tryCreate(stack.getOrCreateTag().getString(NBT_MATERIAL));
        return MaterialManager.get(id);
    }

    public static String getModelKey(ItemStack stack) {
        return stack.getOrCreateTag().getString(NBT_MATERIAL);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        IMaterial material = getMaterial(stack);
        if (material == null) {
            return new TranslationTextComponent(this.getTranslationKey(stack) + ".invalid");
        }
        return new TranslationTextComponent(this.getTranslationKey(stack), material.getDisplayName(PartType.MAIN));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!this.isInGroup(group)) return;

        items.add(new ItemStack(this));

        if (SilentGear.isDevBuild()) {
            for (IMaterial material : MaterialManager.getValues()) {
                items.add(create(material, 1));
            }
        }
    }
}
