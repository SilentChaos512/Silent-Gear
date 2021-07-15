package net.silentchaos512.gear.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.network.OpenGuideBookPacket;

import net.minecraft.item.Item.Properties;

public class GuideBookItem extends Item {
    public GuideBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (playerIn instanceof ServerPlayerEntity) {
            Network.channel.sendTo(new OpenGuideBookPacket(),
                    ((ServerPlayerEntity) playerIn).connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT);
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
