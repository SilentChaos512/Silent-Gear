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
        ItemStack stack = entityIn.getItemStackFromSlot(EquipmentSlotType.CHEST);

        if (stack.getItem() instanceof CoreElytra) {
            ResourceLocation resourcelocation;

            if (entityIn instanceof AbstractClientPlayerEntity) {
                AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity) entityIn;
                boolean hasElytra = abstractclientplayerentity.isPlayerInfoSet()
                        && abstractclientplayerentity.getLocationElytra() != null;
                boolean hasCape = abstractclientplayerentity.hasPlayerInfo()
                        && abstractclientplayerentity.getLocationCape() != null && abstractclientplayerentity
                        .isWearing(PlayerModelPart.CAPE);

                if (hasElytra) {
                    resourcelocation = abstractclientplayerentity.getLocationElytra();
                } else if (hasCape) {
                    resourcelocation = abstractclientplayerentity.getLocationCape();
                } else {
                    resourcelocation = TEXTURE;
                }
            } else {
                resourcelocation = TEXTURE;
            }

            matrixStackIn.push();
            matrixStackIn.translate(0.0D, 0.0D, 0.125D);
            this.getEntityModel().copyModelAttributesTo(this.modelElytra);

            this.modelElytra.setRotationAngles(entityIn,
                    limbSwing,
                    limbSwingAmount,
                    ageInTicks,
                    netHeadYaw,
                    headPitch);

            IVertexBuilder ivertexbuilder = ItemRenderer.getArmorVertexBuilder(bufferIn,
                    this.modelElytra.getRenderType(resourcelocation),
                    false,
                    stack.isEnchanted());

            Color color = new Color(GearClientHelper.getColor(stack, PartType.MAIN));
            this.modelElytra.render(matrixStackIn,
                    ivertexbuilder,
                    packedLightIn,
                    OverlayTexture.NO_OVERLAY,
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    color.getAlpha());

            matrixStackIn.pop();
        }
    }
}
