package net.silentchaos512.gear.core;

import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.property.HarvestTier;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.setup.SgTags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public enum BuiltinMaterials {
    WOOD("wood", "0", ToolMaterial.WOOD),
    NETHERWOOD("netherwood", "0", ToolMaterial.WOOD),
    BAMBOO("bamboo", "0", ToolMaterial.WOOD),
    BONE("bone", "1", ToolMaterial.STONE),
    STONE("stone", "1", ToolMaterial.STONE),
    BASALT("basalt", "1", ToolMaterial.STONE),
    BLACKSTONE("blackstone", "1", ToolMaterial.STONE),
    END_STONE("end_stone", "1", ToolMaterial.STONE),
    FLINT("flint", "1", ToolMaterial.STONE),
    NETHERRACK("netherrack", "1", ToolMaterial.STONE),
    OBSIDIAN("obsidian", "1", ToolMaterial.STONE),
    SANDSTONE("sandstone", "1", ToolMaterial.STONE),
    TERRACOTTA("terracotta", "1", ToolMaterial.STONE),
    COPPER("copper", "1.5", ToolMaterial.STONE, SgTags.Blocks.NEEDS_COPPER_TOOL),
    GOLD("gold", "1", ToolMaterial.GOLD),
    IRON("iron", "2", ToolMaterial.IRON),
    DIAMOND("diamond", "3", ToolMaterial.DIAMOND),
    EMERALD("emerald", "2", ToolMaterial.IRON),
    LAPIS_LAZULI("lapis_lazuli", "1.5", ToolMaterial.STONE, SgTags.Blocks.NEEDS_COPPER_TOOL),
    QUARTZ("quartz", "2", ToolMaterial.IRON),
    AMETHYST("amethyst", "1.5", ToolMaterial.STONE, SgTags.Blocks.NEEDS_COPPER_TOOL),
    DIMERALD("dimerald", "3", ToolMaterial.DIAMOND),
    BLAZE_GOLD("blaze_gold", "2", ToolMaterial.IRON),
    BRONZE("bronze", "2", ToolMaterial.IRON),
    HIGH_CARBON_STEEL("high_carbon_steel", "2", ToolMaterial.IRON),
    CRIMSON_IRON("crimson_iron", "3", ToolMaterial.DIAMOND),
    CRIMSON_STEEL("crimson_steel", "4", ToolMaterial.NETHERITE),
    AZURE_SILVER("azure_silver", "3", ToolMaterial.DIAMOND),
    AZURE_ELECTRUM("azure_electrum", "4", ToolMaterial.NETHERITE),
    TYRIAN_STEEL("tyrian_steel", "4", ToolMaterial.NETHERITE)
    ;

    public static final List<BuiltinMaterials> EXAMPLE_SUB_ITEM_MATERIALS = List.of(
            WOOD,
            NETHERWOOD,
            STONE,
            FLINT,
            COPPER,
            GOLD,
            IRON,
            DIAMOND,
            EMERALD,
            DIMERALD,
            BLAZE_GOLD,
            CRIMSON_IRON,
            CRIMSON_STEEL,
            AZURE_SILVER,
            AZURE_ELECTRUM,
            TYRIAN_STEEL
    );

    private final Identifier id;
    private final DataResource<Material> material;
    private final HarvestTier harvestTier;
    private final TagKey<Block> equivalentIncorrectForToolTag;
    @Nullable private final TagKey<Block> additionalBlocksForTool;

    BuiltinMaterials(String path, String levelHint, ToolMaterial equivalentTier) {
        this(path, path, levelHint, equivalentTier);
    }

    BuiltinMaterials(String path, String levelHint, ToolMaterial equivalentTier, @Nullable TagKey<Block> additionalBlocksForTool) {
        this(path, path, levelHint, equivalentTier, additionalBlocksForTool);
    }

    BuiltinMaterials(String path, String harvestTierName, String levelHint, ToolMaterial equivalentTier) {
        this(path, harvestTierName, levelHint, equivalentTier, null);
    }

    BuiltinMaterials(String path, String harvestTierName, String levelHint, ToolMaterial equivalentTier, @Nullable TagKey<Block> additionalBlocksForTool) {
        this.id = SilentGear.getId(path);
        this.material = DataResource.material(this.id);
        this.harvestTier = HarvestTier.create(harvestTierName, levelHint);
        this.equivalentIncorrectForToolTag = equivalentTier.incorrectBlocksForDrops();
        this.additionalBlocksForTool = additionalBlocksForTool;
    }

    public DataResource<Material> getMaterial() {
        return material;
    }

    public HarvestTier getHarvestTier() {
        return harvestTier;
    }

    // Used by data generators
    public void generateTag(Function<TagKey<Block>, TagAppender<Block>> tagProvider) {
        var intrinsicTagAppender = tagProvider.apply(this.harvestTier.incorrectForTool());
        intrinsicTagAppender.addTag(this.equivalentIncorrectForToolTag);
        if (this.additionalBlocksForTool != null) {
            intrinsicTagAppender.remove(this.additionalBlocksForTool);
        }
    }
}
