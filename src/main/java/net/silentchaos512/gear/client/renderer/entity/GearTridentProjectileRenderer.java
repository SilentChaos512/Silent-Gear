package net.silentchaos512.gear.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.model.GearTridentModel;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.entity.projectile.GearTridentProjectile;
import net.silentchaos512.gear.setup.gear.PartTypes;

public class GearTridentProjectileRenderer extends EntityRenderer<GearTridentProjectile> {
    public static final ResourceLocation TRIDENT_LOCATION = GearTridentModel.TEXTURE;
    private final GearTridentModel model;

    public GearTridentProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = GearTridentModel.bakeModel();
    }

    public void render(GearTridentProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(
            buffer, this.model.renderType(TRIDENT_LOCATION), false, entity.isFoil()
        );
	    this.model.renderWithColors(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 
	    		entity.getToolRodColor(), entity.getGripColor(), entity.getSpikesColor(), entity.getTipColor());
        poseStack.popPose();
        //super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public ResourceLocation getTextureLocation(GearTridentProjectile entity) {
        return TRIDENT_LOCATION;
    }
}
