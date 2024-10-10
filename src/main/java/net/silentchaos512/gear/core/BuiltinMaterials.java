package net.silentchaos512.gear.core;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
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
    WOOD("wood", Tiers.WOOD),
    NETHERWOOD("netherwood", Tiers.WOOD),
    BAMBOO("bamboo", Tiers.WOOD),
    BONE("bone", Tiers.STONE),
    STONE("stone", Tiers.STONE),
    BASALT("basalt", Tiers.STONE),
    BLACKSTONE("blackstone", Tiers.STONE),
    END_STONE("end_stone", Tiers.STONE),
    FLINT("flint", Tiers.STONE),
    NETHERRACK("netherrack", Tiers.STONE),
    OBSIDIAN("obsidian", Tiers.STONE),
    SANDSTONE("sandstone", Tiers.STONE),
    TERRACOTTA("terracotta", Tiers.STONE),
    COPPER("copper", Tiers.STONE, SgTags.Blocks.NEEDS_COPPER_TOOL),
    GOLD("gold", Tiers.GOLD),
    IRON("iron", Tiers.IRON),
    DIAMOND("diamond", Tiers.DIAMOND),
    EMERALD("emerald", Tiers.IRON),
    LAPIS_LAZULI("lapis_lazuli", Tiers.STONE, SgTags.Blocks.NEEDS_COPPER_TOOL),
    QUARTZ("quartz", Tiers.IRON),
    AMETHYST("amethyst", Tiers.STONE, SgTags.Blocks.NEEDS_COPPER_TOOL),
    DIMERALD("dimerald", Tiers.DIAMOND),
    BLAZE_GOLD("blaze_gold", Tiers.IRON),
    BRONZE("bronze", Tiers.IRON),
    HIGH_CARBON_STEEL("high_carbon_steel", Tiers.IRON),
    CRIMSON_IRON("crimson_iron", Tiers.DIAMOND),
    CRIMSON_STEEL("crimson_steel", Tiers.NETHERITE),
    AZURE_SILVER("azure_silver", Tiers.DIAMOND),
    AZURE_ELECTRUM("azure_electrum", Tiers.NETHERITE),
    TYRIAN_STEEL("tyrian_steel", Tiers.NETHERITE)
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

    private final ResourceLocation id;
    private final DataResource<Material> material;
    private final HarvestTier harvestTier;
    private final TagKey<Block> equivalentIncorrectForToolTag;
    @Nullable private final TagKey<Block> additionalBlocksForTool;

    BuiltinMaterials(String path, Tier equivalentTier) {
        this(path, path, equivalentTier);
    }

    BuiltinMaterials(String path, Tier equivalentTier, @Nullable TagKey<Block> additionalBlocksForTool) {
        this(path, path, equivalentTier, additionalBlocksForTool);
    }

    BuiltinMaterials(String path, String harvestTierName, Tier equivalentTier) {
        this(path, harvestTierName, equivalentTier, null);
    }

    BuiltinMaterials(String path, String harvestTierName, Tier equivalentTier, @Nullable TagKey<Block> additionalBlocksForTool) {
        this.id = SilentGear.getId(path);
        this.material = DataResource.material(this.id);
        this.harvestTier = HarvestTier.create(harvestTierName);
        this.equivalentIncorrectForToolTag = equivalentTier.getIncorrectBlocksForDrops();
        this.additionalBlocksForTool = additionalBlocksForTool;
    }

    public DataResource<Material> getMaterial() {
        return material;
    }

    public HarvestTier getHarvestTier() {
        return harvestTier;
    }

    // Used by data generators
    public void generateTag(Function<TagKey<Block>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> tagProvider) {
        var intrinsicTagAppender = tagProvider.apply(this.harvestTier.incorrectForTool());
        intrinsicTagAppender.addTag(this.equivalentIncorrectForToolTag);
        if (this.additionalBlocksForTool != null) {
            intrinsicTagAppender.remove(this.additionalBlocksForTool);
        }
    }
}
