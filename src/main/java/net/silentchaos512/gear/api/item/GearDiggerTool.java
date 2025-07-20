package net.silentchaos512.gear.api.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.api.property.HarvestTier;
import net.silentchaos512.gear.api.property.HarvestTierPropertyValue;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.gear.GearProperties;

import java.util.List;

public interface GearDiggerTool extends GearTool {
    TagKey<Block> getToolBlockSet(ItemStack gear);

    @Override
    default Tool createToolProperties(ItemStack gear, GearPropertiesData properties) {
        var harvestSpeed = properties.getNumber(GearProperties.HARVEST_SPEED);
        var harvestTier = properties.getOrDefault(GearProperties.HARVEST_TIER, new HarvestTierPropertyValue(HarvestTier.ZERO));
        var holderGetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return new Tool(
                List.of(
                        Tool.Rule.deniesDrops(holderGetter.getOrThrow(harvestTier.value().incorrectForTool())),
                        Tool.Rule.minesAndDrops(holderGetter.getOrThrow(getToolBlockSet(gear)), harvestSpeed)
                ),
                1.0F,
                1,
                true
        );
    }
}
