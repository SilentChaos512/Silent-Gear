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
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderSlingshotProjectile extends EntityRenderer<SlingshotProjectile> {
    private static final ResourceLocation PEBBLE_TEXTURE = SilentGear.getId("textures/item/pebble.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(PEBBLE_TEXTURE);

    public RenderSlingshotProjectile(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SlingshotProjectile entity) {
        return PEBBLE_TEXTURE;
    }

    @Override
    public void render(SlingshotProjectile entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
//        ItemStack stack = entityIn.getItem();
//        if (stack.isEmpty()) return;

        matrixStackIn.pushPose();
        float scale = 0.5f;
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose matrixstack$entry = matrixStackIn.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RENDER_TYPE);
        PoseStack.Pose lastPose = matrixStackIn.last();
        vertex(ivertexbuilder, lastPose, packedLightIn, 0.0F, 0, 0, 1);
        vertex(ivertexbuilder, lastPose, packedLightIn, 1.0F, 0, 1, 1);
        vertex(ivertexbuilder, lastPose, packedLightIn, 1.0F, 1, 1, 0);
        vertex(ivertexbuilder, lastPose, packedLightIn, 0.0F, 1, 0, 0);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private static void vertex(VertexConsumer pConsumer, PoseStack.Pose pPose, int pPackedLight, float pX, int pY, int pU, int pV) {
        pConsumer.vertex(pPose, pX - 0.5F, (float)pY - 0.25F, 0.0F)
                .color(255, 255, 255, 255)
                .uv((float)pU, (float)pV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(pPackedLight)
                .normal(pPose, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
