package net.silentchaos512.gear.client.event;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

public final class ExtraBlockBreakHandler implements ISelectiveResourceReloadListener {
    public static final ExtraBlockBreakHandler INSTANCE = new ExtraBlockBreakHandler(Minecraft.getInstance());

    private final Map<Integer, DestroyExtraBlocksProgress> extraDamagedBlocks = new HashMap<>();
    private Minecraft mc;
    private final TextureManager renderEngine;
    private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];

    private ExtraBlockBreakHandler(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderEngine = mcIn.getTextureManager();
        ((IReloadableResourceManager) mc.getResourceManager()).addReloadListener(this);
    }

    @SubscribeEvent
    public void renderBlockBreakAnim(RenderWorldLastEvent event) {
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        this.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), this.mc.getRenderViewEntity(), event.getPartialTicks());
        this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableBlend();
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        this.extraDamagedBlocks.clear();
    }

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event) {
        this.extraDamagedBlocks.clear();
    }

    private static void preRenderDamagedBlocks() {
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.polygonOffset(-3.0F, -3.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableAlphaTest();
        GlStateManager.pushMatrix();
    }

    private static void postRenderDamagedBlocks() {
        GlStateManager.disableAlphaTest();
        GlStateManager.polygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlphaTest();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    private void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder bufferBuilderIn, Entity entityIn, float partialTicks) {
        double d3 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
        double d4 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
        double d5 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;

        if (this.mc.world.getGameTime() % 20 == 0) {
            this.cleanupExtraDamagedBlocks();
        }

        if (!this.extraDamagedBlocks.isEmpty()) {
            this.renderEngine.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            ExtraBlockBreakHandler.preRenderDamagedBlocks();
            bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
            bufferBuilderIn.setTranslation(-d3, -d4, -d5);
            bufferBuilderIn.noColor();

            for (Entry<Integer, DestroyExtraBlocksProgress> entry : this.extraDamagedBlocks.entrySet()) {
                DestroyExtraBlocksProgress destroyblockprogress = entry.getValue();
                BlockPos[] blockpositions = destroyblockprogress.getPositions();
                for (int i = 0; i < blockpositions.length; i++) {
                    BlockPos blockpos = blockpositions[i];
                    double d6 = (double) blockpos.getX() - d3;
                    double d7 = (double) blockpos.getY() - d4;
                    double d8 = (double) blockpos.getZ() - d5;
                    Block block = this.mc.world.getBlockState(blockpos).getBlock();
                    TileEntity te = this.mc.world.getTileEntity(blockpos);
                    boolean hasBreak = block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof AbstractSignBlock || block instanceof SkullBlock;
                    if (!hasBreak)
                        hasBreak = te != null && te.canRenderBreaking();

                    if (!hasBreak) {
                        if (d6 * d6 + d7 * d7 + d8 * d8 > 16384) {
                            this.extraDamagedBlocks.remove(entry.getKey());
                        } else {
                            BlockState iblockstate = mc.world.getBlockState(blockpos);

                            if (iblockstate.getMaterial() != Material.AIR) {
                                int k1 = destroyblockprogress.getPartialBlockDamage();
                                TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[k1];
                                BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
                                blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, this.mc.world);
                            }
                        }
                    }
                }
            }

            tessellatorIn.draw();
            bufferBuilderIn.setTranslation(0.0D, 0.0D, 0.0D);
            ExtraBlockBreakHandler.postRenderDamagedBlocks();
        }
    }

    private void cleanupExtraDamagedBlocks() {
        for (Entry<Integer, DestroyExtraBlocksProgress> entry : this.extraDamagedBlocks.entrySet()) {
            DestroyExtraBlocksProgress destroyblockprogress = entry.getValue();
            int k1 = destroyblockprogress.getCreationWorldTick();

            if (this.mc.world.getGameTime() - k1 > 400) {
                this.extraDamagedBlocks.remove(entry.getKey());
            }
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        AtlasTexture texturemap = this.mc.getTextureMap();
        if (texturemap == null) return;

        for (int i = 0; i < this.destroyBlockIcons.length; ++i) {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
        }
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos[] positions, int progress) {
        if (positions.length > 0 && progress >= 0 && progress < 10) {
            DestroyExtraBlocksProgress destroyextrablocksprogress = new DestroyExtraBlocksProgress(breakerId, positions);
            this.extraDamagedBlocks.put(Integer.valueOf(breakerId), destroyextrablocksprogress);

            destroyextrablocksprogress.setPartialBlockDamage(progress);
            destroyextrablocksprogress.setWorldTick((int) this.mc.world.getGameTime());
        } else {
            this.extraDamagedBlocks.remove(breakerId);
        }
    }

    private static class DestroyExtraBlocksProgress {
        private final int miningPlayerEntId;
        private final BlockPos[] positions;
        /**
         * damage ranges from 1 to 10. -1 causes the client to delete the partial block renderer.
         */
        private int partialBlockProgress;
        /**
         * keeps track of how many ticks this PartiallyDestroyedBlock already exists
         */
        private int createdAtWorldTick;

        public DestroyExtraBlocksProgress(int miningPlayerEntIdIn, BlockPos... positionsIn) {

            this.miningPlayerEntId = miningPlayerEntIdIn;
            this.positions = positionsIn;
        }

        public BlockPos[] getPositions() {

            return this.positions;
        }

        public void setPartialBlockDamage(int damage) {

            if (damage > 10) {
                damage = 10;
            }

            this.partialBlockProgress = damage;
        }

        public int getPartialBlockDamage() {

            return this.partialBlockProgress;
        }

        /**
         * saves the current world tick into the DestroyExtraBlocksProgress
         */
        public void setWorldTick(int createdAtWorldTickIn) {

            this.createdAtWorldTick = createdAtWorldTickIn;
        }

        /**
         * retrieves the 'date' at which the DestroyExtraBlocksProgress was created
         */
        public int getCreationWorldTick() {

            return this.createdAtWorldTick;
        }
    }
}
