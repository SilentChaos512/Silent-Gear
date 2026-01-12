package net.silentchaos512.gear.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.silentchaos512.gear.SilentGear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GearTridentModel extends Model<Unit> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SilentGear.MOD_ID, "textures/item/trident/model.png");
    private final ModelPart root;

    public GearTridentModel(ModelPart root) {
        super(root, RenderTypes::entitySolid);
        this.root = root;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_spike", CubeListBuilder.create().texOffs(4, 3).addBox(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("middle_spike", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_spike", CubeListBuilder.create().texOffs(4, 3).mirror().addBox(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static LayerDefinition createToolRodLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("pole_top", CubeListBuilder.create().texOffs(0, 7).addBox(-0.5F, 1F, -0.5F, 1F, 4F, 1F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("pole_bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 21F, -0.5F, 1F, 6F, 1F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("base_left", CubeListBuilder.create().texOffs(0, 29).addBox(-1.5F, 0F, -0.5F, 1F, 2F, 1F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("base_right", CubeListBuilder.create().texOffs(0, 29).addBox(0.5F, 0F, -0.5F, 1F, 2F, 1F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static LayerDefinition createGripLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("pole_middle", CubeListBuilder.create().texOffs(0, 12).addBox(-0.5F, 5F, -0.5F, 1F, 16F, 1F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static LayerDefinition createSpikesLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("left_spike", CubeListBuilder.create().texOffs(4, 0).addBox(-2.5F, -2.0F, -0.5F, 1.0F, 3.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("middle_spike", CubeListBuilder.create().texOffs(4, 4).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_spike", CubeListBuilder.create().texOffs(4, 0).mirror().addBox(1.5F, -2.0F, -0.5F, 1.0F, 3.0F, 1.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static LayerDefinition createTipLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("left_tip", CubeListBuilder.create().texOffs(4, 9).addBox(-2.5F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("middle_tip", CubeListBuilder.create().texOffs(4, 9).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_tip", CubeListBuilder.create().texOffs(4, 9).mirror().addBox(1.5F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }



    // FIXME
    /*@Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.getChild("tool_rod").render(poseStack, buffer, packedLight, packedOverlay, 31743);
        root.getChild("grip").render(poseStack, buffer, packedLight, packedOverlay, 16775936);
        root.getChild("spikes").render(poseStack, buffer, packedLight, packedOverlay, 65293);
        root.getChild("tip").render(poseStack, buffer, packedLight, packedOverlay, 16711888);
    }*/


    public static GearTridentModel bakeModel() {
        List<Cube> cubes = new ArrayList<Cube>();
        Map<String, ModelPart> children = new HashMap<String, ModelPart>();

        children.put("tool_rod", GearTridentModel.createToolRodLayer().bakeRoot());
        children.put("grip", GearTridentModel.createGripLayer().bakeRoot());
        children.put("spikes", GearTridentModel.createSpikesLayer().bakeRoot());
        children.put("tip", GearTridentModel.createTipLayer().bakeRoot());

        return new GearTridentModel(new ModelPart(cubes, children));
    }

    public void renderWithColors(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color_toolrod, int color_grip, int color_spikes, int color_tip) {
        root.getChild("tool_rod").render(poseStack, buffer, packedLight, packedOverlay, color_toolrod);
        root.getChild("grip").render(poseStack, buffer, packedLight, packedOverlay, color_grip);
        root.getChild("spikes").render(poseStack, buffer, packedLight, packedOverlay, color_spikes);
        root.getChild("tip").render(poseStack, buffer, packedLight, packedOverlay, color_tip);
    }
}
