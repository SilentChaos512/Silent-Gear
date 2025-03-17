package net.silentchaos512.gear.client.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.model.GearTridentModel;

public class SgBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
	private GearTridentModel trident_model;
    // We need some boilerplate in the constructor, telling the superclass where to find the central block entity and entity renderers.
    public SgBlockEntityWithoutLevelRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        bakeModel();
    }
    
    private void bakeModel() {
    	List<Cube> cubes = new ArrayList<Cube>();
    	Map<String, ModelPart> children = new HashMap<String, ModelPart>();
    	
    	children.put("tool_rod", GearTridentModel.createToolRodLayer().bakeRoot());
    	children.put("grip", GearTridentModel.createGripLayer().bakeRoot());
    	children.put("spikes", GearTridentModel.createSpikesLayer().bakeRoot());
    	children.put("tip", GearTridentModel.createTipLayer().bakeRoot());
    	
    	trident_model = new GearTridentModel(new ModelPart(cubes, children));
    }
    
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
       poseStack.pushPose();
       poseStack.scale(1.0F, -1.0F, -1.0F);
       VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, trident_model.renderType(GearTridentModel.TEXTURE), false, false);
       trident_model.renderToBuffer(poseStack, vertexconsumer, packedLight, packedOverlay);
       poseStack.popPose();
    }
}