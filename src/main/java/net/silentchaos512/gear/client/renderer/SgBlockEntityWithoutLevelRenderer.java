package net.silentchaos512.gear.client.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.model.GearTridentModel;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.setup.SgEntities;
import net.silentchaos512.gear.setup.gear.PartTypes;

public class SgBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
	private GearTridentModel trident_model;
    // We need some boilerplate in the constructor, telling the superclass where to find the central block entity and entity renderers.
    public SgBlockEntityWithoutLevelRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        trident_model = GearTridentModel.bakeModel();
    }
    
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    	ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    	ModelManager modelManager = Minecraft.getInstance().getModelManager();
	   	if (transform == ItemDisplayContext.GUI || transform == ItemDisplayContext.FIXED || transform == ItemDisplayContext.GROUND) {
	   		//LogUtils.getLogger().info("loading item model");
			BakedModel model = modelManager.getModel(SgEntities.TRIDENT_ICON);
			model = model.getOverrides().resolve(model, stack, null, null, 0);
			itemRenderer.render(stack, transform, false, poseStack, bufferSource, packedLight, packedOverlay, model);
			//itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, null);
	   		//itemRenderer.renderStatic(stack, transform, packedLight, packedOverlay, poseStack, bufferSource, null, packedOverlay);
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