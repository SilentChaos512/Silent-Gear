package net.silentchaos512.gear.item.gear;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.api.item.GearDiggerTool;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;

import java.util.function.Supplier;

public class GearPickaxeItem extends BasicGearItem implements GearDiggerTool {
    private final Supplier<GearType> gearType;

    public GearPickaxeItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(properties);
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return gearType.get();
    }

    @Override
    public TagKey<Block> getToolBlockSet(GearPropertiesData properties) {
        if (TraitHelper.hasTrait(properties, Const.Traits.SPOON)) {
            return SgTags.Blocks.MINEABLE_WITH_PICKAXE_WITH_SPOON;
        }
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // No action if broken or player is sneaking
        if (GearHelper.isBroken(context.getItemInHand()) || context.getPlayer() != null && context.getPlayer().isCrouching())
            return InteractionResult.PASS;
        // Try to let traits do their thing first
        InteractionResult result = GearHelper.useOn(context);
        // Do nothing or whatever
        if (result == InteractionResult.PASS)
            return GearHelper.useAndCheckBroken(context, super::useOn);
        return result;
    }
}
