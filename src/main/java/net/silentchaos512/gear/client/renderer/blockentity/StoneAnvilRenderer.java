package net.silentchaos512.gear.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.block.stoneanvil.StoneAnvilBlockEntity;

@OnlyIn(Dist.CLIENT)
public class StoneAnvilRenderer implements BlockEntityRenderer<StoneAnvilBlockEntity> {
    private final ItemRenderer itemRenderer;

    public StoneAnvilRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(StoneAnvilBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        Direction direction = blockEntity.getBlockState().getValue(CampfireBlock.FACING);
        ItemStack item = blockEntity.getItem();
        int i = (int) blockEntity.getBlockPos().asLong();

        if (!item.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.9375f, 0.5f);
            Direction direction1 = Direction.from2DDataValue((direction.get2DDataValue()) % 4);
            float f = -direction1.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(f));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), i);
            poseStack.popPose();
        }
    }
}
