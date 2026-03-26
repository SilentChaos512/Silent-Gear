package net.silentchaos512.gear.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.silentchaos512.gear.block.stoneanvil.StoneAnvilBlock;
import net.silentchaos512.gear.block.stoneanvil.StoneAnvilBlockEntity;
import net.silentchaos512.gear.client.renderer.blockentity.state.StoneAnvilRenderState;

public class StoneAnvilRenderer implements BlockEntityRenderer<StoneAnvilBlockEntity, StoneAnvilRenderState> {
    private final ItemModelResolver itemModelResolver;

    public StoneAnvilRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public StoneAnvilRenderState createRenderState() {
        return new StoneAnvilRenderState();
    }

    @Override
    public void extractRenderState(StoneAnvilBlockEntity blockEntity, StoneAnvilRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.facing = blockEntity.getBlockState().getValue(StoneAnvilBlock.FACING);
        int seed = (int) blockEntity.getBlockPos().asLong();
        var itemStackRenderState = new ItemStackRenderState();
        this.itemModelResolver.updateForTopItem(itemStackRenderState, blockEntity.getItem(), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seed);
        renderState.item = itemStackRenderState;
    }

    @Override
    public void submit(StoneAnvilRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        Direction direction = renderState.facing;
        ItemStackRenderState itemStackRenderState = renderState.item;
        if (!itemStackRenderState.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.9375f, 0.5f);
            Direction direction1 = Direction.from2DDataValue((direction.get2DDataValue()) % 4);
            float f = -direction1.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(f));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            itemStackRenderState.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
