package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.material.Material;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Set;

public class GearShovelItem extends GearDiggerItem {
    private static final Set<Material> SHOVEL_EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.CLAY,
            Material.SNOW,
            Material.GRASS,
            Material.DIRT,
            Material.SAND,
            Material.TOP_SNOW
    );

    public GearShovelItem(GearType gearType) {
        super(gearType, BlockTags.MINEABLE_WITH_SHOVEL, SHOVEL_EFFECTIVE_MATERIALS, GearHelper.getBaseItemProperties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // No action if broken or player is sneaking
        if (GearHelper.isBroken(context.getItemInHand()) || context.getPlayer() != null && context.getPlayer().isCrouching())
            return InteractionResult.PASS;
        // Try to let traits do their thing first
        InteractionResult result = GearHelper.onItemUse(context);
        // Make paths or whatever
        if (result == InteractionResult.PASS)
            return GearHelper.useAndCheckBroken(context, Items.NETHERITE_SHOVEL::useOn);
        return result;
    }
}
