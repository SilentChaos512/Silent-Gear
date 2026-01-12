package net.silentchaos512.gear.client.model.object.projectile;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.silentchaos512.gear.client.renderer.entity.state.GearArrowRenderState;

public class GearArrowModel extends EntityModel<GearArrowRenderState> {
    public GearArrowModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(
                "back",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F),
                PartPose.offsetAndRotation(-11.0F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F, 0.0F).withScale(0.8F)
        );
        CubeListBuilder cubelistbuilder = CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-12.0F, -2.0F, 0.0F, 16.0F, 4.0F, 0.0F, CubeDeformation.NONE, 1.0F, 0.8F);
        partdefinition.addOrReplaceChild("cross_1", cubelistbuilder, PartPose.rotation((float) (Math.PI / 4), 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("cross_2", cubelistbuilder, PartPose.rotation((float) (Math.PI * 3.0 / 4.0), 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition.transformed(p_478842_ -> p_478842_.scaled(0.9F)), 32, 32);
    }

    public void setupAnim(GearArrowRenderState renderState) {
        super.setupAnim(renderState);
        if (renderState.shake > 0.0F) {
            float f = -Mth.sin(renderState.shake * 3.0F) * renderState.shake;
            this.root.zRot += f * (float) (Math.PI / 180.0);
        }
    }
}
