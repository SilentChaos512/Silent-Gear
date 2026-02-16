package net.silentchaos512.gear.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.gui.book.MaterialBookScreen;

public class MaterialBookItem extends Item {
    public MaterialBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        SilentGear.PROXY.openMaterialBookScreen();
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
