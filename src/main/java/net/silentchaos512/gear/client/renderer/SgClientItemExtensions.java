package net.silentchaos512.gear.client.renderer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class SgClientItemExtensions implements IClientItemExtensions {
    private final SgBlockEntityWithoutLevelRenderer renderer = new SgBlockEntityWithoutLevelRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }
}