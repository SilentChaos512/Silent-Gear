package net.silentchaos512.gear.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;

public class GearArrowRenderer extends EntityRenderer<GearArrowEntity> {
    public static final ResourceLocation GEAR_ARROW_LOCATION = SilentGear.getId("textures/entity/arrow.png");

    public GearArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public void render(GearArrowEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        float f9 = (float)entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -Mth.sin(f9 * 3.0F) * f9;
            poseStack.mulPose(Axis.ZP.rotationDegrees(f10));
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);
        poseStack.translate(-4.0F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutout(GEAR_ARROW_LOCATION));
        PoseStack.Pose posestack$pose = poseStack.last();
        
        int rodColor = entity.getRodColor();
        int tipColor = entity.getTipColor();
        int fletchingColor = entity.getFletchingColor();
        
        //fletching (front/back)
        this.vertex(posestack$pose, vertexconsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight, fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight, fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight, fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight, fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight, fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight, fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight, fletchingColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight, fletchingColor);
        
        //rod (front/back)
        this.vertex(posestack$pose, vertexconsumer, -7, -0.4F, -0.4F, 0.15625F, 0.15625F, -1, 0, 0, packedLight, rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -0.4F, 0.4F, 0.1875F, 0.15625F, -1, 0, 0, packedLight, rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 0.4F, 0.4F, 0.1875F, 0.1875F, -1, 0, 0, packedLight, rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 0.4F, -0.4F, 0.15625F, 0.1875F, -1, 0, 0, packedLight, rodColor);
        
        this.vertex(posestack$pose, vertexconsumer, -7, 0.4F, -0.4F, 0.0F, 0.15625F, 1, 0, 0, packedLight, rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, 0.4F, 0.4F, 0.15625F, 0.15625F, 1, 0, 0, packedLight, rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -0.4F, 0.4F, 0.15625F, 0.3125F, 1, 0, 0, packedLight, rodColor);
        this.vertex(posestack$pose, vertexconsumer, -7, -0.4F, -0.4F, 0.0F, 0.3125F, 1, 0, 0, packedLight, rodColor);
		
        for (int j = 0; j < 4; j++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            //rod
            this.vertex(posestack$pose, vertexconsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight, rodColor);
            this.vertex(posestack$pose, vertexconsumer, 5, -2, 0, 0.40625F, 0.0F, 0, 1, 0, packedLight, rodColor);
            this.vertex(posestack$pose, vertexconsumer, 5, 2, 0, 0.40625F, 0.15625F, 0, 1, 0, packedLight, rodColor);
            this.vertex(posestack$pose, vertexconsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight, rodColor);
            //tip
            this.vertex(posestack$pose, vertexconsumer, 5, -2, 0, 0.40625F, 0.0F, 0, 1, 0, packedLight, tipColor);
            this.vertex(posestack$pose, vertexconsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight, tipColor);
            this.vertex(posestack$pose, vertexconsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight, tipColor);
            this.vertex(posestack$pose, vertexconsumer, 5, 2, 0, 0.40625F, 0.15625F, 0, 1, 0, packedLight, tipColor);
            //fletching
            this.vertex(posestack$pose, vertexconsumer, -8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight, fletchingColor);
            this.vertex(posestack$pose, vertexconsumer, -4, -2, 0, 0.625F, 0.0F, 0, 1, 0, packedLight, fletchingColor);
            this.vertex(posestack$pose, vertexconsumer, -4, 2, 0, 0.625F, 0.15625F, 0, 1, 0, packedLight, fletchingColor);
            this.vertex(posestack$pose, vertexconsumer, -8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight, fletchingColor);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
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

    @Override
    public ResourceLocation getTextureLocation(GearArrowEntity entity) {
        return GEAR_ARROW_LOCATION;
    }
}
