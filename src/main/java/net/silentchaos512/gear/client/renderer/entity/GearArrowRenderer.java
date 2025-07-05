package net.silentchaos512.gear.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.renderer.entity.state.GearArrowRenderState;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;

public class GearArrowRenderer extends ArrowRenderer<GearArrowEntity, GearArrowRenderState> {
    public static final ResourceLocation GEAR_ARROW_LOCATION = SilentGear.getId("textures/entity/arrow.png");

    public GearArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public GearArrowRenderState createRenderState() {
        return new GearArrowRenderState();
    }

    @Override
    public void extractRenderState(GearArrowEntity entity, GearArrowRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.rodColor = entity.getRodColor();
        reusedState.tipColor = entity.getTipColor();
        reusedState.fletchingColor = entity.getFletchingColor();
    }

    @Override
    public void render(GearArrowRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(renderState.xRot));

        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);
        poseStack.translate(-4.0F, 0.0F, 0.0F);

        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutout(GEAR_ARROW_LOCATION));
        if (renderState.shake > 0.0F) {
            float f10 = -Mth.sin(renderState.shake * 3.0F) * renderState.shake;
            poseStack.mulPose(Axis.ZP.rotationDegrees(f10));
        }
        PoseStack.Pose posestack$pose = poseStack.last();
        
        //fletching (front/back)
        this.vertex(posestack$pose, vertexconsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight, renderState.fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight, renderState.fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight, renderState.fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight, renderState.fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight, renderState.fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight, renderState.fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight, renderState.fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight, renderState.fletchingColor);
        
        //rod (front/back)
        this.vertex(posestack$pose, vertexconsumer, -7, -0.4F, -0.4F, 0.15625F, 0.15625F, -1, 0, 0, packedLight, renderState.rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -0.4F, 0.4F, 0.1875F, 0.15625F, -1, 0, 0, packedLight, renderState.rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 0.4F, 0.4F, 0.1875F, 0.1875F, -1, 0, 0, packedLight, renderState.rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 0.4F, -0.4F, 0.15625F, 0.1875F, -1, 0, 0, packedLight, renderState.rodColor);
        
        this.vertex(posestack$pose, vertexconsumer, -7, 0.4F, -0.4F, 0.0F, 0.15625F, 1, 0, 0, packedLight, renderState.rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 0.4F, 0.4F, 0.15625F, 0.15625F, 1, 0, 0, packedLight, renderState.rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -0.4F, 0.4F, 0.15625F, 0.3125F, 1, 0, 0, packedLight, renderState.rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -0.4F, -0.4F, 0.0F, 0.3125F, 1, 0, 0, packedLight, renderState.rodColor);
		
        for (int j = 0; j < 4; j++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            //rod
            this.vertex(posestack$pose, vertexconsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight, renderState.rodColor);
            this.vertex(posestack$pose, vertexconsumer, 5, -2, 0, 0.40625F, 0.0F, 0, 1, 0, packedLight, renderState.rodColor);
            this.vertex(posestack$pose, vertexconsumer, 5, 2, 0, 0.40625F, 0.15625F, 0, 1, 0, packedLight, renderState.rodColor);
            this.vertex(posestack$pose, vertexconsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight, renderState.rodColor);
            //tip
            this.vertex(posestack$pose, vertexconsumer, 5, -2, 0, 0.40625F, 0.0F, 0, 1, 0, packedLight, renderState.tipColor);
            this.vertex(posestack$pose, vertexconsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight, renderState.tipColor);
            this.vertex(posestack$pose, vertexconsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight, renderState.tipColor);
            this.vertex(posestack$pose, vertexconsumer, 5, 2, 0, 0.40625F, 0.15625F, 0, 1, 0, packedLight, renderState.tipColor);
            //fletching
            this.vertex(posestack$pose, vertexconsumer, -8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight, renderState.fletchingColor);
            this.vertex(posestack$pose, vertexconsumer, -4, -2, 0, 0.625F, 0.0F, 0, 1, 0, packedLight, renderState.fletchingColor);
            this.vertex(posestack$pose, vertexconsumer, -4, 2, 0, 0.625F, 0.15625F, 0, 1, 0, packedLight, renderState.fletchingColor);
            this.vertex(posestack$pose, vertexconsumer, -8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight, renderState.fletchingColor);
        }

        poseStack.popPose();
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    @Override
    protected ResourceLocation getTextureLocation(GearArrowRenderState renderState) {
        return GEAR_ARROW_LOCATION;
    }

    public void vertex(
        PoseStack.Pose pose,
        VertexConsumer consumer,
        float x,
        float y,
        float z,
        float u,
        float v,
        int normalX,
        int normalY,
        int normalZ,
        int packedLight,
        int color
    ) {
        consumer.addVertex(pose, x, y, z)
            .setColor(color)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(pose, (float)normalX, (float)normalZ, (float)normalY);
    }
}
