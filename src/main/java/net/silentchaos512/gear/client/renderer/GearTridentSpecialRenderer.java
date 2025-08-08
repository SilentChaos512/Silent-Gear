package net.silentchaos512.gear.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.model.GearTridentModel;
import net.silentchaos512.gear.core.ToolColors;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

public class GearTridentSpecialRenderer implements SpecialModelRenderer<ToolColors> {
    private final GearTridentModel model;

    public GearTridentSpecialRenderer() {
        model = GearTridentModel.bakeModel();
    }

    @Nullable
    @Override
    public ToolColors extractArgument(ItemStack itemStack) {
        if (GearHelper.isGear(itemStack)) {
            return ToolColors.from(itemStack);
        }
        return null;
    }

    @Override
    public void render(@Nullable ToolColors colors, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        poseStack.pushPose();
        poseStack.scale(1.0f, -1.0f, -1.0f);
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, this.model.renderType(GearTridentModel.TEXTURE), false, hasFoilType);
        int color_toolrod = colors != null ? colors.rod() : -1;
        int color_grip = colors != null ? colors.grip() : -1;
        int color_spikes = colors != null ? colors.main() : -1;
        int color_tip = colors != null ? colors.tip() : -1;
        int color_coating = colors != null ? colors.coating() : -1;
        color_spikes = (color_coating == -1) ? color_spikes : color_coating;
        color_grip = (color_grip == -1) ? color_toolrod : color_grip;
        color_tip = (color_tip) == -1 ? color_spikes : color_tip;
        model.renderWithColors(poseStack, vertexconsumer, packedLight, packedOverlay, color_toolrod, color_grip, color_spikes, color_tip);
        poseStack.popPose();
    }
}