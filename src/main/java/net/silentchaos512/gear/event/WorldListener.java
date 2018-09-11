package net.silentchaos512.gear.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.network.MessageExtraBlockBreak;
import net.silentchaos512.gear.util.IAOETool;

import java.util.List;

public class WorldListener implements IWorldEventListener {

    private final MinecraftServer server;
    private World world;

    public WorldListener(World world, MinecraftServer server) {
        this.server = server;
        this.world = world;
    }

    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
    }

    @Override
    public void notifyLightSet(BlockPos pos) {
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    }

    @Override
    public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {
    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
    }

    @Override
    public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
    }

    @Override
    public void onEntityAdded(Entity entityIn) {
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        EntityPlayerMP player = null;
        for (EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers())
            if (entityplayermp != null && entityplayermp.getEntityId() == breakerId)
                player = entityplayermp;

        if (player == null) return;

        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.getItem() instanceof IAOETool) {
            IAOETool iaoeTool = (IAOETool) heldItem.getItem();
            RayTraceResult rt = iaoeTool.rayTraceBlocks(world, player);
            if (rt != null) {
                final List<BlockPos> positions = iaoeTool.getExtraBlocks(world, rt, player, heldItem);
                final TargetPoint point = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 32D);
                SilentGear.network.wrapper.sendToAllAround(new MessageExtraBlockBreak(
                        player.getEntityId(), progress - 1, positions.toArray(new BlockPos[0])), point);
            }
        }
    }
}
