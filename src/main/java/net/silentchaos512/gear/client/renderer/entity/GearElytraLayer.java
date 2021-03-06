package net.silentchaos512.gear.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.model.ElytraModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.item.gear.CoreElytra;
import net.silentchaos512.utils.Color;

import javax.annotation.Nonnull;

// Mostly copied from Caelus API's CaelusElytraLayer
public class GearElytraLayer<T extends PlayerEntity, M extends EntityModel<T>> extends ElytraLayer<T, M> {
    private static final ResourceLocation TEXTURE = SilentGear.getId("textures/entity/elytra.png");

    private final ElytraModel<T> modelElytra = new ElytraModel<>();

    public GearElytraLayer(IEntityRenderer<T, M> rendererIn) {
        super(rendererIn);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn,
                       int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entityIn.getItemBySlot(EquipmentSlotType.CHEST);

        if (stack.getItem() instanceof CoreElytra) {
            ResourceLocation resourcelocation;

            if (entityIn instanceof AbstractClientPlayerEntity) {
                AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity) entityIn;
                boolean hasElytra = abstractclientplayerentity.isElytraLoaded()
                        && abstractclientplayerentity.getElytraTextureLocation() != null;
                boolean hasCape = abstractclientplayerentity.isCapeLoaded()
                        && abstractclientplayerentity.getCloakTextureLocation() != null && abstractclientplayerentity
                        .isModelPartShown(PlayerModelPart.CAPE);

                if (hasElytra) {
                    resourcelocation = abstractclientplayerentity.getElytraTextureLocation();
                } else if (hasCape) {
                    resourcelocation = abstractclientplayerentity.getCloakTextureLocation();
                } else {
                    resourcelocation = TEXTURE;
                }
            } else {
                resourcelocation = TEXTURE;
            }

            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0D, 0.0D, 0.125D);
            this.getParentModel().copyPropertiesTo(this.modelElytra);

            this.modelElytra.setupAnim(entityIn,
                    limbSwing,
                    limbSwingAmount,
                    ageInTicks,
                    netHeadYaw,
                    headPitch);

            IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(bufferIn,
                    this.modelElytra.renderType(resourcelocation),
                    false,
                    stack.isEnchanted());

            Color color = new Color(GearClientHelper.getColor(stack, PartType.MAIN));
            this.modelElytra.renderToBuffer(matrixStackIn,
                    ivertexbuilder,
                    packedLightIn,
                    OverlayTexture.NO_OVERLAY,
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    color.getAlpha());

            matrixStackIn.popPose();
        }
    }
}
