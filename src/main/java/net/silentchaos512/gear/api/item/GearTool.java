package net.silentchaos512.gear.api.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.util.Collection;
import java.util.function.Supplier;

public interface GearTool extends GearItem {
    Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get(),
            PartTypes.CORD.get()
    ));

    @Override
    default boolean isValidSlot(String slot) {
        return EquipmentSlot.MAINHAND.getName().equalsIgnoreCase(slot)
                || EquipmentSlot.OFFHAND.getName().equalsIgnoreCase(slot);
    }

    @Override
    default Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    /**
     * The base damage done to the item when breaking a block, not considering enchantments
     *
     * @param gear  The item
     * @param world The world
     * @param state The block being broken
     * @param pos   The position of the block
     * @return The amount of damage done (durability lost) to the item
     */
    default int getDamageOnBlockBreak(ItemStack gear, Level world, BlockState state, BlockPos pos) {
        return !state.is(BlockTags.LEAVES) && state.getDestroySpeed(world, pos) > 0 ? 1 : 0;
    }

    /**
     * The base damage done to the item when attacking an entity
     *
     * @param gear     The item
     * @param target   The entity being attacked
     * @param attacker The entity attacking the target
     * @return The amount of damage done (durability lost) to the item
     */
    default int getDamageOnHitEntity(ItemStack gear, LivingEntity target, LivingEntity attacker) {
        return 2;
    }
}
