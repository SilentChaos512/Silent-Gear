package net.silentchaos512.gear.client.renderer.blockentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class StoneAnvilRenderState extends BlockEntityRenderState {
    public ItemStackRenderState item = new ItemStackRenderState();
    public Direction facing = Direction.NORTH;
}
