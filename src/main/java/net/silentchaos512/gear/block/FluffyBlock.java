package net.silentchaos512.gear.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.silentchaos512.gear.init.ModTags;

public class FluffyBlock extends Block {
    static {
        MinecraftForge.EVENT_BUS.addListener(FluffyBlock::onGetBreakSpeed);
    }

    private final DyeColor dyeColor;

    public FluffyBlock(DyeColor color) {
        super(Properties.of(Material.WOOL)
                .strength(0.8f, 3)
                .sound(SoundType.WOOL));
        this.dyeColor = color;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float distance) {
        if (distance < 2 || world.isClientSide) return;

        // Count the number of fluffy blocks that are stacked up.
        int stackedBlocks = 0;
        while (stackedBlocks < 10 && world.getBlockState(pos).is(ModTags.Blocks.FLUFFY_BLOCKS)) {
            pos = pos.below();
            ++stackedBlocks;
        }

        // Reduce fall distance per stacked block
        float newDistance = distance - Math.min(8 * stackedBlocks, distance);
        entity.fallDistance = 0f;
        entity.causeFallDamage(newDistance, 1f, DamageSource.FALL);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        if (entityIn.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(worldIn, entityIn);
        } else {
            FluffyBlock.bounceEntity(entityIn);
        }
    }

    private static void bounceEntity(Entity entity) {
        Vec3 vector3d = entity.getDeltaMovement();
        if (vector3d.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(vector3d.x, -vector3d.y * (double) 0.5f * d0, vector3d.z);
        }
    }

    private static void onGetBreakSpeed(PlayerEvent.BreakSpeed event) {
        // Increase harvest speed when player is using shears
        if (event.getState().is(ModTags.Blocks.FLUFFY_BLOCKS)) {
            ItemStack mainHand = event.getPlayer().getItemInHand(InteractionHand.MAIN_HAND);

            if (!mainHand.isEmpty() && mainHand.getItem() instanceof ShearsItem) {
                int efficiency = EnchantmentHelper.getBlockEfficiency(event.getPlayer());

                float speed = event.getNewSpeed() * 4;
                if (efficiency > 0) {
                    speed += (efficiency * efficiency + 1);
                }

                event.setNewSpeed(speed);
            }
        }
    }
}

