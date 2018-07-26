package net.silentchaos512.gear.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.client.model.LayeredBakedModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This piece instance magic allows tools to have the enchanted glow without it being applied multiple times on each layer.
 * That bug has been a thorn in my side since 1.8... And as a bonus, I can control the effect color now!
 *
 * @author SilentChaos512
 * @since Experimental
 */
public class TEISREquipment extends TileEntityItemStackRenderer {

    public static TEISREquipment INSTANCE = new TEISREquipment();

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
        if (!(model instanceof LayeredBakedModel))
            return;

        int color = -1;
        LayeredBakedModel layerModel = (LayeredBakedModel) model;
        int layerCount = layerModel.getLayerCount();

        for (int i = 0; i < layerCount; ++i)
            // if (i == 0) // Uncomment to test individual layers
            this.renderModel(model, color, stack, i, null);

        // Mostly fixes the enchanted glint effect. Don't even ask me why this works. It's not a
        // perfect fix, but works well enough.
        if (stack.hasEffect()) {
            // Need to render the effect on all faces instance the first layer
            this.renderEffect(layerModel, 0, null);
            // Excluding the "front" and "back" magically fixes the duplication effect. I hate you.
            Collection<EnumFacing> stupidFaces = Arrays.asList(EnumFacing.SOUTH, EnumFacing.NORTH);
            for (int i = 1; i < layerCount; ++i)
                this.renderEffect(layerModel, i, stupidFaces);
        }
    }

    // Copied from RenderItem
    private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
        boolean flag = color == -1 && !stack.isEmpty();
        int i = 0;

        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = quads.get(i);
            int k = color;

            if (flag && bakedquad.hasTintIndex()) {
                k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());

//                if (EntityRenderer.anaglyphEnable) {
//                    k = TextureUtil.anaglyphColor(k);
//                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }

    // Copied from RenderItem
    private void renderEffect(IBakedModel model, int layerIndex, Collection<EnumFacing> excludeFaces) {
        // TODO: Something to control the color!
        int color = 0xFF808080; // 0xff8040cc;

        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        textureManager.bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);

        this.renderModel(model, color, layerIndex, excludeFaces);

        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);

        this.renderModel(model, color, layerIndex, excludeFaces);

        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    // Copied from RenderItem
    private void renderModel(IBakedModel model, int color, int layerIndex, Collection<EnumFacing> excludeFaces) {
        this.renderModel(model, color, ItemStack.EMPTY, layerIndex, excludeFaces);
    }

    // Copied from RenderItem
    private void renderModel(IBakedModel model, int color, ItemStack stack, int layerIndex, Collection<EnumFacing> excludeFaces) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        // for (EnumFacing enumfacing : EnumFacing.values()) {
        // List<BakedQuad> quads = model.getQuads(null, enumfacing, layerIndex);
        // this.renderQuads(bufferbuilder, quads, color, stack);
        // }

        List<BakedQuad> quads = new ArrayList<>();
        if (layerIndex < 0) {
            for (int i = 0; i < ((LayeredBakedModel) model).getLayerCount(); ++i) {
                for (BakedQuad quad : model.getQuads(null, null, i))
                    if (excludeFaces == null || !excludeFaces.contains(quad.getFace()))
                        quads.add(quad);
            }
        } else {
            for (BakedQuad quad : model.getQuads(null, null, layerIndex))
                if (excludeFaces == null || !excludeFaces.contains(quad.getFace()))
                    quads.add(quad);
        }

        this.renderQuads(bufferbuilder, quads, color, stack);
        tessellator.draw();
    }
}
