package net.silentchaos512.gear.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
import net.silentchaos512.gear.util.TextUtil;

public class CraftedMaterialItem extends Item implements IColoredMaterialItem {
    private static final String NBT_MATERIAL = "Material";

    public CraftedMaterialItem(Properties properties) {
        super(properties);
    }

    public static IMaterialInstance getMaterial(ItemStack stack) {
        String id = stack.getOrCreateTag().getString(NBT_MATERIAL);
        IMaterial mat = MaterialManager.get(SilentGear.getIdWithDefaultNamespace(id));
        if (mat != null) {
            return MaterialInstance.of(mat);
        }
        return LazyMaterialInstance.of(Const.Materials.EXAMPLE);
    }

    public ItemStack create(IMaterialInstance material, int count) {
        ItemStack result = new ItemStack(this, count);
        result.getOrCreateTag().putString(NBT_MATERIAL, SilentGear.shortenId(material.getId()));
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
        TranslationTextComponent nameText = new TranslationTextComponent(this.getTranslationKey(), material.getDisplayName(PartType.MAIN));
        int nameColor = material.getNameColor(PartType.MAIN, GearType.ALL);
        return TextUtil.withColor(nameText, nameColor);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            items.add(create(LazyMaterialInstance.of(Const.Materials.EXAMPLE), 1));
        }
    }
}
