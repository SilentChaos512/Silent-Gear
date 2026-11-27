package net.silentchaos512.gear.item.gear;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearDiggerTool;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.function.Supplier;

public class GearHoeItem extends BasicGearItem implements GearDiggerTool {
    private final Supplier<GearType> gearType;

    public GearHoeItem(Supplier<GearType> gearType, Properties properties) {
        super(properties.hoe(ToolMaterial.NETHERITE, -4f, 0f));
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public TagKey<Block> getToolBlockSet(GearPropertiesData properties) {
        return BlockTags.MINEABLE_WITH_HOE;
    }

    @Override
    public boolean canPerformAction(ItemInstance instance, ItemAbility itemAbility) {
        return !GearHelper.isBroken(instance) && getGearType().canPerformAction(itemAbility);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // No action if broken
        if (GearHelper.isBroken(context.getItemInHand()))
            return InteractionResult.PASS;
        // Try to let traits do their thing first if player is not crouching
        var playerCrouching = context.getPlayer() != null && context.getPlayer().isCrouching();
        InteractionResult result = playerCrouching ? InteractionResult.PASS : GearHelper.useOn(context);
        // Till dirt or whatever (even when crouching)
        if (result == InteractionResult.PASS)
            return GearHelper.useAndCheckBroken(context, ctx -> GearHelper.transformBlock(ctx, BlockTransformers.HOE));
        return result;
    }
}
