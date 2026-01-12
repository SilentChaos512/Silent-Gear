package net.silentchaos512.gear.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.silentchaos512.gear.client.model.GearTridentModel;
import net.silentchaos512.gear.client.renderer.entity.state.GearTridentRenderState;
import net.silentchaos512.gear.entity.projectile.GearThrownTrident;

public class GearThrownTridentRenderer extends EntityRenderer<GearThrownTrident, GearTridentRenderState> {
    public static final Identifier TRIDENT_LOCATION = GearTridentModel.TEXTURE;
    private final GearTridentModel model;

    public GearThrownTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = GearTridentModel.bakeModel();
    }

    @Override
    public GearTridentRenderState createRenderState() {
        return new GearTridentRenderState();
    }

    @Override
    public void extractRenderState(GearThrownTrident entity, GearTridentRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.rodColor = entity.getToolRodColor();
        reusedState.gripColor = entity.getGripColor();
        reusedState.spikesColor = entity.getSpikesColor();
        reusedState.tipColor = entity.getTipColor();
        reusedState.yRot = entity.getYRot(partialTick);
        reusedState.xRot = entity.getXRot();
        reusedState.isFoil = entity.isFoil();
    }

    /*@Override
    public void render(GearTridentRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(renderState.xRot + 90.0F));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(
            bufferSource, this.model.renderType(TRIDENT_LOCATION), false, Config.Client.allowEnchantedEffect.get() && renderState.isFoil
        );
	    this.model.renderWithColors(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 
	    		renderState.rodColor, renderState.gripColor, renderState.spikesColor, renderState.tipColor);
        poseStack.popPose();
        //super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }*/

    public Identifier getTextureLocation(GearThrownTrident entity) {
        return TRIDENT_LOCATION;
    }
}
