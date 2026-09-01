package net.silentchaos512.gear.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.model.GearTridentModel;
import net.silentchaos512.gear.core.ToolColors;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

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
    public void getExtents(Consumer<Vector3fc> output) {
        this.model.root().getExtentsForGui(new PoseStack(), output);
    }

    @Override
    public void submit(@Nullable ToolColors colors, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        int color_toolrod = colors != null ? colors.rod() : -1;
        int color_grip = colors != null ? colors.grip() : -1;
        int color_spikes = colors != null ? colors.main() : -1;
        int color_tip = colors != null ? colors.tip() : -1;
        int color_coating = colors != null ? colors.coating() : -1;
        color_spikes = (color_coating == -1) ? color_spikes : color_coating;
        color_grip = (color_grip == -1) ? color_toolrod : color_grip;
        color_tip = (color_tip) == -1 ? color_spikes : color_tip;

        this.submitLayers(submitNodeCollector.order(0), poseStack, this.model.renderType(GearTridentModel.TEXTURE), lightCoords, overlayCoords, outlineColor,
                color_toolrod, color_grip, color_spikes, color_tip);
        if (hasFoil) {
            this.submitLayers(submitNodeCollector.order(1), poseStack, RenderTypes.entityGlint(), lightCoords, overlayCoords, outlineColor,
                    -1, -1, -1, -1);
        }
    }

    private void submitLayers(OrderedSubmitNodeCollector submitNodeCollector, PoseStack poseStack,
                              net.minecraft.client.renderer.rendertype.RenderType renderType, int lightCoords, int overlayCoords, int outlineColor,
                              int rodColor, int gripColor, int spikesColor, int tipColor) {
        this.submitPart(submitNodeCollector, this.model.root().getChild("tool_rod"), poseStack, renderType, lightCoords, overlayCoords, outlineColor, rodColor);
        this.submitPart(submitNodeCollector, this.model.root().getChild("grip"), poseStack, renderType, lightCoords, overlayCoords, outlineColor, gripColor);
        this.submitPart(submitNodeCollector, this.model.root().getChild("spikes"), poseStack, renderType, lightCoords, overlayCoords, outlineColor, spikesColor);
        this.submitPart(submitNodeCollector, this.model.root().getChild("tip"), poseStack, renderType, lightCoords, overlayCoords, outlineColor, tipColor);
    }

    private void submitPart(OrderedSubmitNodeCollector submitNodeCollector, ModelPart part, PoseStack poseStack,
                            net.minecraft.client.renderer.rendertype.RenderType renderType, int lightCoords, int overlayCoords, int outlineColor, int color) {
        submitNodeCollector.submitModelPart(part, poseStack, renderType, lightCoords, overlayCoords, null, color, null, outlineColor);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<ToolColors> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public GearTridentSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new GearTridentSpecialRenderer();
        }
    }
}
