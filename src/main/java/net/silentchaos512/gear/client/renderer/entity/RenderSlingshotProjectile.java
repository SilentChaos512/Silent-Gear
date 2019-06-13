package net.silentchaos512.gear.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;

import javax.annotation.Nullable;

public class RenderSlingshotProjectile extends EntityRenderer<SlingshotProjectile> {
    protected RenderSlingshotProjectile(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(SlingshotProjectile entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    public void doRender(SlingshotProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack stack = ItemStack.EMPTY; // entity.getItem(); FIXME
        if (stack.isEmpty()) return;

        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(1, 1, 1);
//        TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(stack.getItem());
        TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(Items.FEATHER);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        float f = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMaxU();
        float f2 = textureatlassprite.getMinV();
        float f3 = textureatlassprite.getMaxV();
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        GlStateManager.rotatef(180.0F - this.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float)(this.getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * -this.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
        }

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex((double)f, (double)f3).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex((double)f1, (double)f3).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex((double)f1, (double)f2).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex((double)f, (double)f2).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public static class Factory implements IRenderFactory<SlingshotProjectile> {
        @Override
        public EntityRenderer<? super SlingshotProjectile> createRenderFor(EntityRendererManager manager) {
            return new RenderSlingshotProjectile(manager);
        }
    }
}
