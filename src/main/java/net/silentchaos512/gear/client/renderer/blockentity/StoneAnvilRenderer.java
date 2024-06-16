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
    public void render(StoneAnvilBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Direction direction = pBlockEntity.getBlockState().getValue(CampfireBlock.FACING);
        ItemStack item = pBlockEntity.getItem();
        int i = (int) pBlockEntity.getBlockPos().asLong();

        if (!item.isEmpty()) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, 0.9375f, 0.5f);
            Direction direction1 = Direction.from2DDataValue((direction.get2DDataValue()) % 4);
            float f = -direction1.toYRot();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(f));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
//            pPoseStack.translate(-0.3125F, -0.3125F, 0.0F);
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
            this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, pBlockEntity.getLevel(), i);
            pPoseStack.popPose();
        }
    }
}
