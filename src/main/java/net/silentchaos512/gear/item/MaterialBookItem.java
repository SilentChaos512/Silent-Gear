package net.silentchaos512.gear.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.client.gui.book.MaterialBookScreen;

public class MaterialBookItem extends Item {
    public MaterialBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(new MaterialBookScreen());
        }
        return InteractionResult.SUCCESS;
    }
}
