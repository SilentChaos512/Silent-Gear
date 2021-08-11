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

public class GearAxeItem extends GearDiggerItem {
    static final Set<Material> AXE_EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.VEGETABLE,
            Material.WOOD,
            Material.BAMBOO,
            Material.LEAVES,
            Material.PLANT,
            Material.REPLACEABLE_PLANT
    );

    public GearAxeItem(GearType gearType) {
        this(gearType, AXE_EFFECTIVE_MATERIALS);
    }

    public GearAxeItem(GearType gearType, Set<Material> extraMaterials) {
        super(gearType, BlockTags.MINEABLE_WITH_AXE, extraMaterials, GearHelper.getBaseItemProperties());
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
