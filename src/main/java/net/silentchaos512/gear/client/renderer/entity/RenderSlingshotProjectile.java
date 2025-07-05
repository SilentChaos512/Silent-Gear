package net.silentchaos512.gear.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;

public class RenderSlingshotProjectile extends ArrowRenderer<SlingshotProjectile, ArrowRenderState> {
    private static final ResourceLocation PEBBLE_TEXTURE = SilentGear.getId("textures/item/pebble.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(PEBBLE_TEXTURE);

    public RenderSlingshotProjectile(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTextureLocation(ArrowRenderState renderState) {
        return PEBBLE_TEXTURE;
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    public void render(ArrowRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float scale = 0.5f;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        VertexConsumer ivertexbuilder = bufferSource.getBuffer(RENDER_TYPE);
        PoseStack.Pose lastPose = poseStack.last();
        vertex(ivertexbuilder, lastPose, packedLight, 0.0F, 0, 0, 1);
        vertex(ivertexbuilder, lastPose, packedLight, 1.0F, 0, 1, 1);
        vertex(ivertexbuilder, lastPose, packedLight, 1.0F, 1, 1, 0);
        vertex(ivertexbuilder, lastPose, packedLight, 0.0F, 1, 0, 0);
        poseStack.popPose();
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    private static void vertex(VertexConsumer pConsumer, PoseStack.Pose pPose, int pPackedLight, float pX, int pY, int pU, int pV) {
        pConsumer.addVertex(pPose, pX - 0.5F, (float)pY - 0.25F, 0.0F)
                .setColor(-1)
                .setUv((float)pU, (float)pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(pPackedLight)
                .setNormal(pPose, 0.0F, 1.0F, 0.0F);
    }
}
