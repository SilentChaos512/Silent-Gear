package net.silentchaos512.gear.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.model.GearTridentModel;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.setup.gear.PartTypes;

public class GearTridentRenderer extends BlockEntityWithoutLevelRenderer {
	private GearTridentModel trident_model;
    public GearTridentRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        trident_model = GearTridentModel.bakeModel();
    }
    
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
	   	if (transform == ItemDisplayContext.GUI || transform == ItemDisplayContext.FIXED || transform == ItemDisplayContext.GROUND) {
			GearItemExtensions.renderer.renderByItem(stack, transform, poseStack, bufferSource, packedLight, packedOverlay);
		} else {
	       poseStack.pushPose();
	       poseStack.scale(1.0F, -1.0F, -1.0F);
	       VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, trident_model.renderType(GearTridentModel.TEXTURE), false, false);
	       int color_toolrod = ColorUtils.getBlendedColorForPartInGear(stack, PartTypes.ROD.get());
	       int color_grip = ColorUtils.getBlendedColorForPartInGear(stack, PartTypes.GRIP.get());
	       int color_spikes = ColorUtils.getBlendedColorForPartInGear(stack, PartTypes.MAIN.get());
	       int color_tip = ColorUtils.getBlendedColorForPartInGear(stack, PartTypes.TIP.get());
	       int color_coating = ColorUtils.getBlendedColorForPartInGear(stack, PartTypes.COATING.get());
	       color_spikes = (color_coating == -1) ? color_spikes : color_coating;
	       color_grip = (color_grip == -1) ? color_toolrod : color_grip;
	       color_tip = (color_tip) == -1 ? color_spikes : color_tip;
	       trident_model.renderWithColors(poseStack, vertexconsumer, packedLight, packedOverlay, color_toolrod, color_grip, color_spikes, color_tip);
	       poseStack.popPose();
		}
       
    }
}