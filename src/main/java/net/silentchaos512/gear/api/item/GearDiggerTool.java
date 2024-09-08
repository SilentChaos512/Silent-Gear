package net.silentchaos512.gear.api.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.api.property.HarvestTier;
import net.silentchaos512.gear.api.property.HarvestTierPropertyValue;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.gear.GearProperties;

import java.util.List;

public interface GearDiggerTool extends GearTool {
    TagKey<Block> getToolBlockSet();

    @Override
    default Tool createToolProperties(GearPropertiesData properties) {
        var harvestSpeed = properties.getNumber(GearProperties.HARVEST_SPEED);
        var harvestTier = properties.getOrDefault(GearProperties.HARVEST_TIER, new HarvestTierPropertyValue(HarvestTier.ZERO));
        return new Tool(
                List.of(
                        Tool.Rule.deniesDrops(harvestTier.value().incorrectForTool()),
                        Tool.Rule.minesAndDrops(getToolBlockSet(), harvestSpeed)
                ),
                1.0F,
                1
        );
    }
}
