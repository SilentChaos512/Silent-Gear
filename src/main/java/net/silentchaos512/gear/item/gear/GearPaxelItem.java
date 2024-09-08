package net.silentchaos512.gear.item.gear;

import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.util.GearHelper;

import java.util.function.Supplier;

public class GearPaxelItem extends GearPickaxeItem {
    public GearPaxelItem(Supplier<GearType> gearType) {
        super(gearType);
    }

    @Override
    public TagKey<Block> getToolBlockSet() {
        return SgTags.Blocks.MINEABLE_WITH_PAXEL;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return getGearType().canPerformAction(itemAbility);
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
