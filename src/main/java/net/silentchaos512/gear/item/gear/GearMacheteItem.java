package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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

    public GearMacheteItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(gearType, properties);
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
        if (GearHelper.isCorrectToolForDrops(stack, state, getToolBlockSet(stack)))
            return speed * 0.4f;
        return speed;
    }

    @Override
    public TagKey<Block> getToolBlockSet(ItemStack gear) {
        return SgTags.Blocks.MINEABLE_WITH_MACHETE;
    }

    @Override
    public Tool createToolProperties(ItemStack gear, GearPropertiesData properties) {
        // Works like both a sword and an axe
        var harvestSpeed = properties.getNumber(GearProperties.HARVEST_SPEED);
        var harvestTier = properties.getOrDefault(GearProperties.HARVEST_TIER, new HarvestTierPropertyValue(HarvestTier.ZERO));
        var holderGetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return new Tool(
                List.of(
                        Tool.Rule.deniesDrops(holderGetter.getOrThrow(harvestTier.value().incorrectForTool())),
                        Tool.Rule.minesAndDrops(holderGetter.getOrThrow(getToolBlockSet(gear)), harvestSpeed),
                        Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0F),
                        Tool.Rule.overrideSpeed(holderGetter.getOrThrow(BlockTags.SWORD_INSTANTLY_MINES), Float.MAX_VALUE),
                        Tool.Rule.overrideSpeed(holderGetter.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5f)
                ),
                1.0F,
                2,
                false
        );
    }
}
