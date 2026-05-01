package net.silentchaos512.gear.client.renderer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

@MethodsReturnNonnullByDefault
public class GearRangedItemExtensions implements IClientItemExtensions {
    final static GearRangedItemRenderer renderer = new GearRangedItemRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }
}
