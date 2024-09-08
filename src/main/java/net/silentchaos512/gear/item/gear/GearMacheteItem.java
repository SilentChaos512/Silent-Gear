package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.item.BreakEventHandler;
import net.silentchaos512.gear.api.item.GearDiggerTool;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.property.HarvestTier;
import net.silentchaos512.gear.api.property.HarvestTierPropertyValue;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearHelper;

import java.util.List;
import java.util.function.Supplier;

public class GearMacheteItem extends GearSwordItem implements BreakEventHandler, GearDiggerTool {
    private static final int BREAK_RANGE = 2;

    public GearMacheteItem(Supplier<GearType> gearType) {
        super(gearType);
    }

    @Override
    public void onBlockBreakEvent(ItemStack stack, Player player, Level level, BlockPos pos, BlockState state) {
        // Allow clearing vegetation, just like sickles but with a smaller range
        if (!player.isCrouching()) {
            GearItemSets.SICKLE.gearItem().breakPlantsInRange(stack, pos, player, BREAK_RANGE);
        }
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float axeSpeed = GearHelper.getDestroySpeed(stack, state);
        float speed = Math.max(axeSpeed, super.getDestroySpeed(stack, state));
        // Slower on materials normally harvested with axes
        if (GearHelper.isCorrectToolForDrops(stack, state, getToolBlockSet()))
            return speed * 0.4f;
        return speed;
    }

    @Override
    public TagKey<Block> getToolBlockSet() {
        return SgTags.Blocks.MINEABLE_WITH_MACHETE;
    }

    @Override
    public Tool createToolProperties(GearPropertiesData properties) {
        // Works like both a sword and an axe
        var harvestSpeed = properties.getNumber(GearProperties.HARVEST_SPEED);
        var harvestTier = properties.getOrDefault(GearProperties.HARVEST_TIER, new HarvestTierPropertyValue(HarvestTier.ZERO));
        return new Tool(
                List.of(
                        Tool.Rule.deniesDrops(harvestTier.value().incorrectForTool()),
                        Tool.Rule.minesAndDrops(getToolBlockSet(), harvestSpeed),
                        Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F),
                        Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5F)
                ),
                1.0F,
                2
        );
    }
}
