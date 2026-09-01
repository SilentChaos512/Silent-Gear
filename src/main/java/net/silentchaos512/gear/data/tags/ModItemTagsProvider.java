package net.silentchaos512.gear.data.tags;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.blueprint.AbstractBlueprintItem;
import net.silentchaos512.gear.item.gear.GearCurioItem;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.data.tag.LibItemTagsProvider;

import java.util.Arrays;
import java.util.Comparator;

public class ModItemTagsProvider extends LibItemTagsProvider {
    public ModItemTagsProvider(GatherDataEvent event, BlockTagsProvider blocks) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), SilentGear.MOD_ID);
    }

    @Override
    protected DirectTagAppender<Item> tag(TagKey<Item> tag) {
        return new DirectTagAppender<>(super.tag(tag), item -> item.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        (new ModBlockItemTagsProvider() {
            @Override
            protected DirectTagAppender<Block> tag(TagKey<Block> blockTag, TagKey<Item> itemTag) {
                return new DirectTagAppender<>(
                        new LibItemTagsProvider.BlockToItemConverter(ModItemTagsProvider.this.tag(itemTag)),
                        block -> block.builtInRegistryHolder().key()
                );
            }
        }).run();

        // Common

        builder(SgTags.Items.STORAGE_BLOCKS_RAW_CRIMSON_IRON, SgBlocks.RAW_CRIMSON_IRON_BLOCK);
        builder(SgTags.Items.STORAGE_BLOCKS_RAW_AZURE_SILVER, SgBlocks.RAW_AZURE_SILVER_BLOCK);

        builder(Tags.Items.ORES_IN_GROUND_STONE, SgBlocks.BORT_ORE);
        builder(Tags.Items.ORES_IN_GROUND_DEEPSLATE, SgBlocks.DEEPSLATE_BORT_ORE);
        builder(Tags.Items.ORES_IN_GROUND_NETHERRACK, SgBlocks.CRIMSON_IRON_ORE);
        builder(SgTags.Items.ORES_IN_GROUND_BLACKSTONE, SgBlocks.BLACKSTONE_CRIMSON_IRON_ORE);
        builder(SgTags.Items.ORES_IN_GROUND_END_STONE, SgBlocks.AZURE_SILVER_BLOCK);

        builder(SgTags.Items.RAW_MATERIALS_CRIMSON_IRON, CraftingItems.RAW_CRIMSON_IRON);
        builder(SgTags.Items.RAW_MATERIALS_AZURE_SILVER, CraftingItems.RAW_AZURE_SILVER);
        tag(Tags.Items.RAW_MATERIALS)
                .addTag(SgTags.Items.RAW_MATERIALS_CRIMSON_IRON)
                .addTag(SgTags.Items.RAW_MATERIALS_AZURE_SILVER);

        builder(SgTags.Items.COAL_GENERATOR_FUELS, SgItems.NETHERWOOD_CHARCOAL, SgBlocks.NETHERWOOD_CHARCOAL_BLOCK);

        builder(SgTags.Items.DUSTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_DUST);
        builder(SgTags.Items.DUSTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_DUST);
        builder(SgTags.Items.DUSTS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_DUST);
        builder(SgTags.Items.DUSTS_AZURE_SILVER, CraftingItems.AZURE_SILVER_DUST);
        builder(SgTags.Items.DUSTS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_DUST);
        builder(SgTags.Items.DUSTS_TYRIAN_STEEL, CraftingItems.TYRIAN_STEEL_DUST);
        builder(SgTags.Items.DUSTS_STARMETAL, CraftingItems.STARMETAL_DUST);
        tag(Tags.Items.DUSTS)
                .addTag(SgTags.Items.DUSTS_BLAZE_GOLD)
                .addTag(SgTags.Items.DUSTS_CRIMSON_IRON)
                .addTag(SgTags.Items.DUSTS_CRIMSON_STEEL)
                .addTag(SgTags.Items.DUSTS_AZURE_SILVER)
                .addTag(SgTags.Items.DUSTS_AZURE_ELECTRUM)
                .addTag(SgTags.Items.DUSTS_TYRIAN_STEEL)
                .addTag(SgTags.Items.DUSTS_STARMETAL);

        builder(SgTags.Items.GEMS_BORT, CraftingItems.BORT);
        tag(Tags.Items.GEMS)
                .addTag(SgTags.Items.GEMS_BORT);

        builder(SgTags.Items.INGOTS_BRONZE, CraftingItems.BRONZE_INGOT);
        builder(SgTags.Items.INGOTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_INGOT);
        builder(SgTags.Items.INGOTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_INGOT);
        builder(SgTags.Items.INGOTS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_INGOT);
        builder(SgTags.Items.INGOTS_AZURE_SILVER, CraftingItems.AZURE_SILVER_INGOT);
        builder(SgTags.Items.INGOTS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_INGOT);
        builder(SgTags.Items.INGOTS_TYRIAN_STEEL, CraftingItems.TYRIAN_STEEL_INGOT);
        tag(Tags.Items.INGOTS)
                .addTag(SgTags.Items.INGOTS_BLAZE_GOLD)
                .addTag(SgTags.Items.INGOTS_CRIMSON_IRON)
                .addTag(SgTags.Items.INGOTS_CRIMSON_STEEL)
                .addTag(SgTags.Items.INGOTS_AZURE_SILVER)
                .addTag(SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .addTag(SgTags.Items.INGOTS_TYRIAN_STEEL);

        builder(SgTags.Items.NUGGETS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_NUGGET);
        builder(SgTags.Items.NUGGETS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_NUGGET);
        builder(SgTags.Items.NUGGETS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_NUGGET);
        builder(SgTags.Items.NUGGETS_AZURE_SILVER, CraftingItems.AZURE_SILVER_NUGGET);
        builder(SgTags.Items.NUGGETS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_NUGGET);
        builder(SgTags.Items.NUGGETS_TYRIAN_STEEL, CraftingItems.TYRIAN_STEEL_NUGGET);
        builder(SgTags.Items.NUGGETS_DIAMOND, CraftingItems.DIAMOND_SHARD);
        builder(SgTags.Items.NUGGETS_EMERALD, CraftingItems.EMERALD_SHARD);
        tag(Tags.Items.NUGGETS)
                .addTag(SgTags.Items.NUGGETS_BLAZE_GOLD)
                .addTag(SgTags.Items.NUGGETS_CRIMSON_IRON)
                .addTag(SgTags.Items.NUGGETS_CRIMSON_STEEL)
                .addTag(SgTags.Items.NUGGETS_AZURE_SILVER)
                .addTag(SgTags.Items.NUGGETS_AZURE_ELECTRUM)
                .addTag(SgTags.Items.NUGGETS_TYRIAN_STEEL)
                .addTag(SgTags.Items.NUGGETS_DIAMOND)
                .addTag(SgTags.Items.NUGGETS_EMERALD);

        builder(SgTags.Items.RODS_IRON, CraftingItems.IRON_ROD);
        builder(SgTags.Items.RODS_NETHERWOOD, CraftingItems.NETHERWOOD_STICK);
        builder(SgTags.Items.RODS_STONE, CraftingItems.STONE_ROD);
        builder(Tags.Items.RODS_WOODEN, CraftingItems.NETHERWOOD_STICK);
        // Rods without items
        tag(SgTags.Items.RODS_AZURE_ELECTRUM);
        tag(SgTags.Items.RODS_AZURE_SILVER);
        tag(SgTags.Items.RODS_BLAZE_GOLD);
        tag(SgTags.Items.RODS_BRONZE);
        tag(SgTags.Items.RODS_CRIMSON_IRON);
        tag(SgTags.Items.RODS_CRIMSON_STEEL);
        tag(SgTags.Items.RODS_TYRIAN_STEEL);
        tag(SgTags.Items.RODS_COPPER);
        tag(SgTags.Items.RODS_GOLD);
        tag(SgTags.Items.RODS_DIAMOND);
        tag(SgTags.Items.RODS_EMERALD);
        tag(SgTags.Items.RODS_QUARTZ);
        // All rods
        tag(Tags.Items.RODS)
                .addTag(SgTags.Items.RODS_IRON)
                .addTag(SgTags.Items.RODS_NETHERWOOD)
                .addTag(SgTags.Items.RODS_STONE)
                .addTag(SgTags.Items.RODS_AZURE_ELECTRUM)
                .addTag(SgTags.Items.RODS_AZURE_SILVER)
                .addTag(SgTags.Items.RODS_BLAZE_GOLD)
                .addTag(SgTags.Items.RODS_BRONZE)
                .addTag(SgTags.Items.RODS_CRIMSON_IRON)
                .addTag(SgTags.Items.RODS_CRIMSON_STEEL)
                .addTag(SgTags.Items.RODS_TYRIAN_STEEL)
                .addTag(SgTags.Items.RODS_COPPER)
                .addTag(SgTags.Items.RODS_GOLD)
                .addTag(SgTags.Items.RODS_DIAMOND)
                .addTag(SgTags.Items.RODS_EMERALD)
                .addTag(SgTags.Items.RODS_QUARTZ);

        builder(SgTags.Items.PAPER, Items.PAPER);
        builder(SgTags.Items.BLUEPRINT_PAPER, CraftingItems.BLUEPRINT_PAPER);
        builder(SgTags.Items.TEMPLATE_BOARDS, CraftingItems.TEMPLATE_BOARD);

        builder(SgTags.Items.FRUITS, SgItems.NETHER_BANANA);
        builder(SgTags.Items.SEEDS_FLAX, SgItems.FLAX_SEEDS);
        builder(SgTags.Items.SEEDS_FLUFFY_PUFF, SgItems.FLUFFY_SEEDS);
        tag(Tags.Items.SEEDS)
                .addTag(SgTags.Items.SEEDS_FLAX)
                .addTag(SgTags.Items.SEEDS_FLUFFY_PUFF);
        builder(Tags.Items.STRINGS, CraftingItems.FLAX_STRING, CraftingItems.SINEW_FIBER);

        // Tool/Armor tags
        builder(ItemTags.HEAD_ARMOR,
                GearItemSets.HELMET.gearItem());
        builder(ItemTags.CHEST_ARMOR,
                GearItemSets.CHESTPLATE.gearItem());
        builder(ItemTags.LEG_ARMOR,
                GearItemSets.LEGGINGS.gearItem());
        builder(ItemTags.FOOT_ARMOR,
                GearItemSets.BOOTS.gearItem());
        builder(SgTags.Items.ARMORS_ELYTRA,
                GearItemSets.ELYTRA.gearItem());
        builder(ItemTags.AXES,
                GearItemSets.AXE.gearItem());
        builder(Tags.Items.TOOLS_BOW,
                GearItemSets.BOW.gearItem());
        builder(Tags.Items.TOOLS_CROSSBOW,
                GearItemSets.CROSSBOW.gearItem());
        builder(SgTags.Items.TOOLS_HAMMER,
                GearItemSets.HAMMER.gearItem(),
                GearItemSets.PROSPECTOR_HAMMER.gearItem(),
                SgItems.CRUDE_HAMMER);
        builder(ItemTags.HOES,
                GearItemSets.HOE.gearItem());
        builder(SgTags.Items.TOOLS_KNIFE,
                GearItemSets.KNIFE.gearItem(),
                GearItemSets.DAGGER.gearItem(),
                SgItems.CRUDE_KNIFE);
        builder(ItemTags.PICKAXES,
                GearItemSets.PICKAXE.gearItem());
        builder(SgTags.Items.TOOLS_SAW,
                GearItemSets.SAW.gearItem());
        builder(Tags.Items.TOOLS_SHEAR,
                GearItemSets.SHEARS.gearItem());
        builder(Tags.Items.TOOLS_SHIELD,
                GearItemSets.SHIELD.gearItem());
        builder(ItemTags.SHOVELS,
                GearItemSets.SHOVEL.gearItem());
        builder(SgTags.Items.TOOLS_SICKLE,
                GearItemSets.SICKLE.gearItem());
        builder(ItemTags.SWORDS,
                GearItemSets.KATANA.gearItem(),
                GearItemSets.MACHETE.gearItem(),
                GearItemSets.SWORD.gearItem());
        builder(Tags.Items.MELEE_WEAPON_TOOLS,
                GearItemSets.AXE.gearItem(),
                GearItemSets.DAGGER.gearItem(),
                GearItemSets.KATANA.gearItem(),
                GearItemSets.KNIFE.gearItem(),
                GearItemSets.MACE.gearItem(),
                GearItemSets.MACHETE.gearItem(),
                GearItemSets.SPEAR.gearItem(),
                GearItemSets.SWORD.gearItem(),
                GearItemSets.TRIDENT.gearItem());
        builder(Tags.Items.RANGED_WEAPON_TOOLS,
                GearItemSets.BOW.gearItem(),
                GearItemSets.CROSSBOW.gearItem(),
                GearItemSets.SLINGSHOT.gearItem(),
                GearItemSets.TRIDENT.gearItem());
        builder(Tags.Items.MINING_TOOL_TOOLS,
                GearItemSets.HAMMER.gearItem(),
                GearItemSets.PAXEL.gearItem(),
                GearItemSets.PICKAXE.gearItem(),
                GearItemSets.PROSPECTOR_HAMMER.gearItem());

        // Minecraft

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(GearItemSets.BOOTS.gearItem());
        tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(GearItemSets.LEGGINGS.gearItem());
        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(GearItemSets.CHESTPLATE.gearItem());
        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(GearItemSets.HELMET.gearItem());
        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(GearItemSets.HELMET.gearItem())
                .add(GearItemSets.CHESTPLATE.gearItem())
                .add(GearItemSets.LEGGINGS.gearItem())
                .add(GearItemSets.BOOTS.gearItem());
        tag(ItemTags.MELEE_WEAPON_ENCHANTABLE)
                .add(GearItemSets.SWORD.gearItem())
                .add(GearItemSets.KATANA.gearItem())
                .add(GearItemSets.MACHETE.gearItem())
                .add(GearItemSets.SPEAR.gearItem());
        tag(ItemTags.SWEEPING_ENCHANTABLE)
                .add(GearItemSets.SWORD.gearItem())
                .add(GearItemSets.KATANA.gearItem())
                .add(GearItemSets.MACHETE.gearItem());
        tag(ItemTags.LUNGE_ENCHANTABLE)
                .add(GearItemSets.SPEAR.gearItem());
        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(GearItemSets.MACE.gearItem());
        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .add(GearItemSets.SPEAR.gearItem())
                .add(GearItemSets.PAXEL.gearItem())
                .add(GearItemSets.SICKLE.gearItem());
        tag(ItemTags.WEAPON_ENCHANTABLE)
                .add(GearItemSets.MACE.gearItem())
                .add(GearItemSets.HAMMER.gearItem());
        tag(ItemTags.MINING_ENCHANTABLE)
                .add(GearItemSets.MACHETE.gearItem())
                .add(GearItemSets.PICKAXE.gearItem())
                .add(GearItemSets.SHOVEL.gearItem())
                .add(GearItemSets.AXE.gearItem())
                .add(GearItemSets.PAXEL.gearItem())
                .add(GearItemSets.HAMMER.gearItem())
                .add(GearItemSets.EXCAVATOR.gearItem())
                .add(GearItemSets.SAW.gearItem())
                .add(GearItemSets.PROSPECTOR_HAMMER.gearItem())
                .add(GearItemSets.HOE.gearItem())
                .add(GearItemSets.MATTOCK.gearItem())
                .add(GearItemSets.SHEARS.gearItem());
        tag(ItemTags.MINING_LOOT_ENCHANTABLE)
                .add(GearItemSets.MACHETE.gearItem())
                .add(GearItemSets.PICKAXE.gearItem())
                .add(GearItemSets.SHOVEL.gearItem())
                .add(GearItemSets.AXE.gearItem())
                .add(GearItemSets.PAXEL.gearItem())
                .add(GearItemSets.HAMMER.gearItem())
                .add(GearItemSets.EXCAVATOR.gearItem())
                .add(GearItemSets.SAW.gearItem())
                .add(GearItemSets.PROSPECTOR_HAMMER.gearItem())
                .add(GearItemSets.HOE.gearItem())
                .add(GearItemSets.MATTOCK.gearItem());
        tag(ItemTags.FISHING_ENCHANTABLE).add(GearItemSets.FISHING_ROD.gearItem());
        tag(ItemTags.TRIDENT_ENCHANTABLE).add(GearItemSets.TRIDENT.gearItem());
        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(SgItems.CRUDE_KNIFE.get())
                .add(SgItems.CRUDE_HAMMER.get())
                .add(GearItemSets.SWORD.gearItem())
                .add(GearItemSets.KATANA.gearItem())
                .add(GearItemSets.MACHETE.gearItem())
                .add(GearItemSets.KNIFE.gearItem())
                .add(GearItemSets.DAGGER.gearItem())
                .add(GearItemSets.SPEAR.gearItem())
                .add(GearItemSets.TRIDENT.gearItem())
                .add(GearItemSets.MACE.gearItem())
                .add(GearItemSets.SHIELD.gearItem())
                .add(GearItemSets.BOW.gearItem())
                .add(GearItemSets.CROSSBOW.gearItem())
                .add(GearItemSets.SLINGSHOT.gearItem())
                .add(GearItemSets.PICKAXE.gearItem())
                .add(GearItemSets.SHOVEL.gearItem())
                .add(GearItemSets.AXE.gearItem())
                .add(GearItemSets.PAXEL.gearItem())
                .add(GearItemSets.HAMMER.gearItem())
                .add(GearItemSets.EXCAVATOR.gearItem())
                .add(GearItemSets.SAW.gearItem())
                .add(GearItemSets.PROSPECTOR_HAMMER.gearItem())
                .add(GearItemSets.HOE.gearItem())
                .add(GearItemSets.MATTOCK.gearItem())
                .add(GearItemSets.SICKLE.gearItem())
                .add(GearItemSets.SHEARS.gearItem())
                .add(GearItemSets.FISHING_ROD.gearItem())
                .add(GearItemSets.HELMET.gearItem())
                .add(GearItemSets.CHESTPLATE.gearItem())
                .add(GearItemSets.LEGGINGS.gearItem())
                .add(GearItemSets.BOOTS.gearItem())
                .add(GearItemSets.ELYTRA.gearItem());
        tag(ItemTags.BOW_ENCHANTABLE).add(GearItemSets.BOW.gearItem());
        tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(GearItemSets.ELYTRA.gearItem());
        tag(ItemTags.CROSSBOW_ENCHANTABLE).add(GearItemSets.CROSSBOW.gearItem());
        tag(ItemTags.MACE_ENCHANTABLE).add(GearItemSets.MACE.gearItem());

        tag(ItemTags.ARROWS).add(GearItemSets.ARROW.gearItem());
        builder(ItemTags.PIGLIN_LOVED,
                SgBlocks.BLAZE_GOLD_BLOCK,
                SgItems.GOLDEN_NETHER_BANANA,
                CraftingItems.BLAZE_GOLD_DUST,
                CraftingItems.BLAZE_GOLD_INGOT);

        builder(ItemTags.CLUSTER_MAX_HARVESTABLES,
                GearItemSets.PICKAXE.gearItem(),
                GearItemSets.PAXEL.gearItem(),
                GearItemSets.PROSPECTOR_HAMMER.gearItem()
        );

        tag(ItemTags.CHICKEN_FOOD)
                .add(SgItems.FLAX_SEEDS.get())
                .add(SgItems.FLUFFY_SEEDS.get());
        tag(ItemTags.PARROT_FOOD)
                .add(SgItems.FLAX_SEEDS.get())
                .add(SgItems.FLUFFY_SEEDS.get());

        // Silent Gear

        tag(SgTags.Items.PAINT_FILLER).addTag(Tags.Items.SLIME_BALLS);
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_1).add(CraftingItems.GLOWING_DUST.asItem());
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_2).add(CraftingItems.BLAZING_DUST.asItem());
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_3).add(CraftingItems.GLITTERY_DUST.asItem());
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_4);
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_5);
        tag(SgTags.Items.GRADER_CATALYSTS)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_1)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_2)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_3)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_4)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_5);

        tag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_1).addTag(SgTags.Items.DUSTS_BLAZE_GOLD);
        tag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_2).addTag(SgTags.Items.DUSTS_AZURE_SILVER);
        tag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_3).addTag(SgTags.Items.DUSTS_STARMETAL);
        tag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_1)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_2)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_3);

        builder(SgTags.Items.REPAIR_KITS,
                SgItems.VERY_CRUDE_REPAIR_KIT,
                SgItems.CRUDE_REPAIR_KIT,
                SgItems.STURDY_REPAIR_KIT,
                SgItems.CRIMSON_REPAIR_KIT,
                SgItems.AZURE_REPAIR_KIT);

        tag(SgTags.Items.IMPERIAL_DROPS).addTag(Tags.Items.GEMS);
        tag(SgTags.Items.GOLD_DIGGER_DROPS).addTag(Tags.Items.NUGGETS);
        tag(SgTags.Items.GREEDY_MAGNET_ATTRACTED)
                .addTag(Tags.Items.GEMS)
                .addTag(Tags.Items.RAW_MATERIALS)
                .addTag(Tags.Items.ORES)
                .addTag(Tags.Items.INGOTS)
                .addTag(Tags.Items.NUGGETS)
                .add(Items.ANCIENT_DEBRIS)
                .add(Items.NETHERITE_SCRAP);

        // Blueprints
        Multimap<Identifier, AbstractBlueprintItem> blueprints = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof AbstractBlueprintItem)
                .map(item -> (AbstractBlueprintItem) item)
                .sorted(Comparator.comparing(blueprint -> blueprint.getItemTag().location()))
                .forEach(item -> blueprints.put(item.getItemTag().location(), item));
        TagAppender<Item> blueprintsBuilder = tag(SgTags.Items.BLUEPRINTS);
        blueprints.keySet().forEach(tagId -> {
            TagKey<Item> tag = ItemTags.create(tagId);
            tag(tag).add(blueprints.get(tagId).toArray(new Item[0]));
            blueprintsBuilder.addTag(tag);
        });

        // Curios
        SgItems.getItems(GearCurioItem.class).forEach(item ->
                builder(makeWrapper(Const.CURIOS, item.getSlot()), item));

        builder(makeWrapper(Const.CURIOS, "back"), GearItemSets.ELYTRA.gearItem());
    }

    private TagKey<Item> makeWrapper(String namespace, String path) {
        return ItemTags.create(Identifier.fromNamespaceAndPath(namespace, path));
    }

    private void builder(TagKey<Item> tag, ItemLike... items) {
        tag(tag).add(Arrays.stream(items).map(ItemLike::asItem).toArray(Item[]::new));
    }
}
