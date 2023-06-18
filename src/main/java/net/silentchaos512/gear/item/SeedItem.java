package net.silentchaos512.gear.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

// Copied from Simple Farming (https://github.com/cweckerl/simplefarming/blob/1.16/src/main/java/enemeez/simplefarming/item/SeedItem.java)

public class SeedItem extends ItemNameBlockItem {
    public SeedItem(Block blockIn, Properties properties) {
        super(blockIn, properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemstack, Player player, LivingEntity entity, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!entity.level().isClientSide && !entity.isBaby() && entity instanceof AgeableMob && ((AgeableMob) entity).getAge() == 0) {
            //noinspection ChainOfInstanceofChecks
            if (entity instanceof Chicken) {
                if (((Chicken) entity).isInLove()) {
                    return InteractionResult.FAIL;
                } else {
                    ((Chicken) entity).setInLove(player);
                    if (!player.isCreative())
                        stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }

            if (entity instanceof Parrot)
                if (!entity.level().isClientSide) {
                    if (!((Parrot) entity).isTame())
                        if (Math.random() <= 0.33) {
                            ((Parrot) entity).tame(player);
                            ((Parrot) entity).setInLove(player);
                        }
                    if (!player.isCreative())
                        stack.shrink(1);
                }
        }

        if (entity.isBaby()) {
            if (!player.isCreative())
                stack.shrink(1);
            ((AgeableMob) entity).ageUp((int) ((float) (-((AgeableMob) entity).getAge() / 20) * 0.1F), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
