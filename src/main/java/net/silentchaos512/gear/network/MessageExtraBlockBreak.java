package net.silentchaos512.gear.network;

import net.minecraft.util.math.BlockPos;

public class MessageExtraBlockBreak /*extends MessageSL*/ {
    public int entityID = 0;
    public int numBlocks = 0;
    public int progress = 0;
    int[][] positions = new int[numBlocks][3];

    public MessageExtraBlockBreak() {
    }

    public MessageExtraBlockBreak(int entityID, int progress, BlockPos... positions) {
        this.entityID = entityID;
        this.numBlocks = positions.length;
        this.progress = progress;
        this.positions = new int[numBlocks][3];

        for (int i = 0; i < numBlocks; ++i) {
            BlockPos pos = positions[i];
            this.positions[i][0] = pos.getX();
            this.positions[i][1] = pos.getY();
            this.positions[i][2] = pos.getZ();
        }
    }

    /*
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        this.positions = new int[this.numBlocks][3];
        for (int i = 0; i < this.numBlocks; ++i) {
            this.positions[i][0] = buf.readInt();
            this.positions[i][1] = buf.readInt();
            this.positions[i][2] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        for (int i = 0; i < this.numBlocks; ++i) {
            buf.writeInt(this.positions[i][0]);
            buf.writeInt(this.positions[i][1]);
            buf.writeInt(this.positions[i][2]);
        }
    }

    @Override
    public IMessage handleMessage(MessageContext context) {
        BlockPos[] positions = new BlockPos[this.numBlocks];
        for (int i = 0; i < this.numBlocks; ++i)
            positions[i] = new BlockPos(this.positions[i][0], this.positions[i][1], this.positions[i][2]);
        ExtraBlockBreakHandler.INSTANCE.sendBlockBreakProgress(this.entityID, positions, this.progress);

        return null;
    }
    */
}
