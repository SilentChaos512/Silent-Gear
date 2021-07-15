package net.silentchaos512.gear.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
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
    public void fallOn(World world, BlockPos pos, Entity entity, float distance) {
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
        entity.causeFallDamage(newDistance, 1f);
    }

    @Override
    public void updateEntityAfterFallOn(IBlockReader worldIn, Entity entityIn) {
        if (entityIn.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(worldIn, entityIn);
        } else {
            FluffyBlock.bounceEntity(entityIn);
        }
    }

    private static void bounceEntity(Entity entity) {
        Vector3d vector3d = entity.getDeltaMovement();
        if (vector3d.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(vector3d.x, -vector3d.y * (double) 0.5f * d0, vector3d.z);
        }
    }

    private static void onGetBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack mainHand = event.getPlayer().getItemInHand(Hand.MAIN_HAND);
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

