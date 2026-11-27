package net.silentchaos512.gear.item.gear;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.item.GearDiggerTool;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class GearAxeItem extends BasicGearItem implements GearDiggerTool {
    private final Supplier<GearType> gearType;

    public GearAxeItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(properties.axe(ToolMaterial.NETHERITE, 0f, -3f));
        this.gearType = gearType;
    }

    @Override
    public void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        GearDiggerTool.super.onRecalculatePost(gear, player, finalProperties);
        if (!GearHelper.isBroken(gear)) {
            gear.set(DataComponents.WEAPON, new Weapon(2, 5.0f));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // No action if broken or player is sneaking
        if (GearHelper.isBroken(context.getItemInHand()) || context.getPlayer() != null && context.getPlayer().isCrouching())
            return InteractionResult.PASS;
        // Try to let traits do their thing first
        InteractionResult result = GearHelper.useOn(context);
        // Other vanilla actions (strip, scrape, wax off)
        if (result == InteractionResult.PASS) {
            return GearHelper.useAndCheckBroken(context, ctx -> GearHelper.transformBlock(ctx, BlockTransformers.AXE));
        }
        return result;
    }

    @Override
    public GearType getGearType() {
        return gearType.get();
    }

    @Override
    public TagKey<Block> getToolBlockSet(GearPropertiesData properties) {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    // TODO: Remove in favor of just removing/modifying the TOOL data component?
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return GearHelper.getDestroySpeed(stack, state);
    }
}
