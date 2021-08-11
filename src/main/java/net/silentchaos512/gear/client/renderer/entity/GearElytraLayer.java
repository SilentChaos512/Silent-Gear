package net.silentchaos512.gear.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.item.gear.GearElytraItem;
import net.silentchaos512.utils.Color;

import javax.annotation.Nonnull;

// Mostly copied from Caelus API's CaelusElytraLayer
public class GearElytraLayer<T extends Player, M extends EntityModel<T>> extends ElytraLayer<T, M> {
    private static final ResourceLocation TEXTURE = SilentGear.getId("textures/entity/elytra.png");

    private final ElytraModel<T> modelElytra;

    public GearElytraLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent, modelSet);
        this.modelElytra = new ElytraModel<>(modelSet.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn,
                       int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entityIn.getItemBySlot(EquipmentSlot.CHEST);

        if (stack.getItem() instanceof GearElytraItem) {
            ResourceLocation resourcelocation;

            if (entityIn instanceof AbstractClientPlayer) {
                AbstractClientPlayer abstractclientplayerentity = (AbstractClientPlayer) entityIn;
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

            VertexConsumer ivertexbuilder = ItemRenderer.getArmorFoilBuffer(bufferIn,
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
