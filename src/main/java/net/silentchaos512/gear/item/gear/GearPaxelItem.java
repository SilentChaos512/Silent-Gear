package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolAction;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Set;

public class GearPaxelItem extends GearPickaxeItem {
    private static final Set<Material> PAXEL_EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.DECORATION,
            Material.GLASS,
            Material.PISTON,
            Material.BUILDABLE_GLASS,
            Material.BAMBOO,
            Material.LEAVES,
            Material.PLANT,
            Material.REPLACEABLE_PLANT,
            Material.WOOD
    );

    public GearPaxelItem(GearType gearType) {
        super(gearType, PAXEL_EFFECTIVE_MATERIALS);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return getGearType().canPerformAction(toolAction);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return super.isCorrectToolForDrops(stack, state)
                || SgItems.SHOVEL.get().isCorrectToolForDrops(stack, state)
                || SgItems.AXE.get().isCorrectToolForDrops(stack, state);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // No action if broken or player is sneaking
        if (GearHelper.isBroken(context.getItemInHand()) || context.getPlayer() != null && context.getPlayer().isCrouching())
            return InteractionResult.PASS;
        // Try to let traits do their thing first
        InteractionResult result = GearHelper.onItemUse(context);
        // Other vanilla actions (strip, scrape, wax off)
        if (result == InteractionResult.PASS) {
            return GearHelper.useAndCheckBroken(context, Items.NETHERITE_AXE::useOn);
        }
        return result;
    }
}
