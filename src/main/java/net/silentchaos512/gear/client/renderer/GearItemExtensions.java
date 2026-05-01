package net.silentchaos512.gear.client.renderer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.silentchaos512.gear.item.gear.GearArmorItem;

@MethodsReturnNonnullByDefault
public class GearItemExtensions implements IClientItemExtensions {
    final static GearItemRenderer renderer = new GearItemRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }

    @Override
    public int getArmorLayerTintColor(ItemStack stack, LivingEntity entity, ArmorMaterial.Layer layer, int layerIdx, int fallbackColor) {
        if (layerIdx == 0) {
            return GearArmorItem.getArmorColor(stack);
        }
        return -1;
    }
}
