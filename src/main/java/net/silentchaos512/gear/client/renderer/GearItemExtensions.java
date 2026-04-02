package net.silentchaos512.gear.client.renderer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

@MethodsReturnNonnullByDefault
public class GearItemExtensions implements IClientItemExtensions {
    private final GearItemRenderer renderer = new GearItemRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }
}
