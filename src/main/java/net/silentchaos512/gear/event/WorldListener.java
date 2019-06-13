package net.silentchaos512.gear.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class WorldListener /*implements IWorldEventListener*/ {
    private final MinecraftServer server;
    private World world;

    public WorldListener(World world, MinecraftServer server) {
        this.server = server;
        this.world = world;
    }

/*    @Override
    public void notifyBlockUpdate(IBlockReader worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
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
    public void addParticle(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void addParticle(IParticleData particleData, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
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
                *//* FIXME
                final List<BlockPos> positions = iaoeTool.getExtraBlocks(world, rt, player, heldItem);
                final TargetPoint point = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 32D);
                SilentGear.network.wrapper.sendToAllAround(new MessageExtraBlockBreak(
                        player.getEntityId(), progress - 1, positions.toArray(new BlockPos[0])), point);
                *//*
            }
        }
    }*/
}
