package net.silentchaos512.gear.data;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.*;
import net.minecraft.advancements.predicates.*;
import net.minecraft.advancements.predicates.entity.*;
import net.minecraft.advancements.triggers.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.advancements.criterion.GearPropertyTrigger;
import net.silentchaos512.gear.advancements.criterion.GearRepairedTrigger;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.setup.*;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.util.NameUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Advancements are a reloadable datapack registry as of 26.3. Register with
 * {@code event.advancement(ModAdvancementProvider::new)} in {@code GatherDataRegistryEntriesEvent}.
 */
public class ModAdvancementProvider extends AdvancementSubProvider {
    public ModAdvancementProvider(BootstrapContext<Advancement> output) {
        super(output);
    }

    @SuppressWarnings("unused")
    @Override
    public void generate() {
        var saver = this.output;
        HolderGetter<Item> items = this.output.lookup(Registries.ITEM);

        var rootIcon = new ItemStackTemplate(GearItemSets.PICKAXE.gearItem(),
                DataComponentPatch.builder()
                        .set(
                                SgDataComponents.GEAR_CONSTRUCTION.get(),
                                new GearConstructionData(
                                        PartList.immutable(
                                                PartInstance.create(Const.Parts.PICKAXE_HEAD, GearItemSets.PICKAXE.mainPart(), Const.Materials.CRIMSON_STEEL),
                                                PartInstance.create(Const.Parts.ROD, SgItems.ROD.get(), Const.Materials.BLAZE_GOLD),
                                                PartInstance.create(Const.Parts.TIP, SgItems.TIP.get(), Const.Materials.AZURE_ELECTRUM),
                                                PartInstance.create(Const.Parts.GRIP, SgItems.GRIP.get(), Const.Materials.WOOL_BLACK),
                                                PartInstance.create(Const.Parts.BINDING, SgItems.BINDING.get(), Const.Materials.STRING)
                                        )
                                )
                        )
                        .build()
        );
        AdvancementHolder root = Advancement.Builder.advancement()
                .rootDisplay(rootIcon, title("root"), description("root"), Identifier.withDefaultNamespace("gui/advancements/backgrounds/adventure"), AdvancementType.TASK, false, false, false)
                .addCriterion("get_item", getItem(Items.CRAFTING_TABLE))
                .save(saver, id("root"));

        AdvancementHolder overworldPlants = Advancement.Builder.advancement()
                .parent(root)
                .display(icon(CraftingItems.FLAX_FIBER), title("overworld_plants"), description("overworld_plants"), AdvancementType.TASK, true, true, false)
                .addCriterion("flax_seeds", getItem(SgItems.FLAX_SEEDS))
                .addCriterion("flax_fibers", getItem(CraftingItems.FLAX_FIBER))
                .addCriterion("fluffy_seeds", getItem(SgItems.FLUFFY_SEEDS))
                .addCriterion("fluffy_puffs", getItem(CraftingItems.FLUFFY_PUFF))
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(saver, id("overworld_plants"));
        AdvancementHolder kachink1 = Advancement.Builder.advancement()
                .parent(root)
                .display(icon(Items.IRON_NUGGET), title("kachink1"), description("kachink1"), AdvancementType.TASK, true, true, false)
                .addCriterion("kachink", SgCriteriaTriggers.DAMAGE_FACTOR_CHANGE.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                .save(saver, id("kachink1"));
        AdvancementHolder kachink2 = Advancement.Builder.advancement()
                .parent(kachink1)
                .display(icon(CraftingItems.DIAMOND_SHARD), title("kachink2"), description("kachink2"), AdvancementType.TASK, true, true, false)
                .addCriterion("kachink", SgCriteriaTriggers.BRITTLE_DAMAGE.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                .save(saver, id("kachink2"));

        AdvancementHolder stoneAnvil = simpleGetItem(saver, SgBlocks.STONE_ANVIL, root);

        AdvancementHolder crudeKnife = simpleGetItem(saver, SgItems.CRUDE_KNIFE, stoneAnvil);

        AdvancementHolder templateBoard = simpleGetItem(saver, CraftingItems.TEMPLATE_BOARD, crudeKnife);

        AdvancementHolder blueprintPaper = simpleGetItem(saver, CraftingItems.BLUEPRINT_PAPER, root);
        AdvancementHolder repairKit;
        {
            Advancement.Builder builder = Advancement.Builder.advancement()
                    .parent(templateBoard)
                    .display(icon(SgItems.CRUDE_REPAIR_KIT), title("repair_kit"), description("repair_kit"), AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR);
            SgItems.getItems(RepairKitItem.class).forEach(item ->
                    builder.addCriterion(NameUtils.fromItem(item).getPath(), getItem(item)));
            repairKit = builder.save(saver, id("repair_kit"));
        }

        AdvancementHolder crimsonRepairKit = simpleGetItem(saver, SgItems.CRIMSON_REPAIR_KIT, repairKit);
        AdvancementHolder azureRepairKit = simpleGetItem(saver, SgItems.AZURE_REPAIR_KIT, crimsonRepairKit);
        AdvancementHolder repairFromBroken = Advancement.Builder.advancement()
                .parent(repairKit)
                .display(icon(Items.FLINT), title("repair_from_broken"), description("repair_from_broken"), AdvancementType.TASK, true, true, false)
                .addCriterion("repair", SgCriteriaTriggers.GEAR_REPAIRED.get().createCriterion(new GearRepairedTrigger.Instance(Optional.empty(), MinMaxBounds.Ints.atLeast(1), MinMaxBounds.Ints.atLeast(1))))
                .save(saver, id("repair_from_broken"));

        AdvancementHolder blueprintBook = simpleGetItem(saver, SgItems.BLUEPRINT_BOOK, blueprintPaper);

        var exampleTipUpgrade = new ItemStackTemplate(SgItems.TIP,
                DataComponentPatch.builder()
                        .set(
                                SgDataComponents.MATERIAL_LIST.get(),
                                List.of(
                                        MaterialInstance.of(Const.Materials.EXAMPLE)
                                )
                        )
                        .build()
        );
        AdvancementHolder tipUpgrade = simpleGetItem(saver, SgItems.TIP, exampleTipUpgrade, templateBoard, "tip_upgrade");

        //region Gear

        AdvancementHolder armor = Advancement.Builder.advancement()
                .parent(blueprintPaper)
                .display(icon(GearItemSets.HELMET.gearItem()), title("armor"), description("armor"), AdvancementType.TASK, true, true, false)
                .addCriterion("helmet", getItem(GearItemSets.HELMET.gearItem()))
                .addCriterion("chestplate", getItem(GearItemSets.CHESTPLATE.gearItem()))
                .addCriterion("leggings", getItem(GearItemSets.LEGGINGS.gearItem()))
                .addCriterion("boots", getItem(GearItemSets.BOOTS.gearItem()))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(saver, id("armor"));

        AdvancementHolder bow = Advancement.Builder.advancement()
                .parent(blueprintPaper)
                .display(icon(GearItemSets.BOW.gearItem()), title("bow"), description("bow"), AdvancementType.TASK, true, true, false)
                .addCriterion("get_item", getItem(GearItemSets.BOW.gearItem()))
                .save(saver, id("bow"));
        AdvancementHolder standardTools = Advancement.Builder.advancement()
                .parent(blueprintPaper)
                .display(icon(GearItemSets.PICKAXE.gearItem()), title("standard_tools"), description("standard_tools"), AdvancementType.TASK, true, true, false)
                .addCriterion("pickaxe", getItem(GearItemSets.PICKAXE.gearItem()))
                .addCriterion("shovel", getItem(GearItemSets.SHOVEL.gearItem()))
                .addCriterion("axe", getItem(GearItemSets.AXE.gearItem()))
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(saver, id("standard_tools"));
        AdvancementHolder swords = Advancement.Builder.advancement()
                .parent(blueprintPaper)
                .display(icon(GearItemSets.SWORD.gearItem()), title("swords"), description("swords"), AdvancementType.TASK, true, true, false)
                .addCriterion("sword", getItem(GearItemSets.SWORD.gearItem()))
                .addCriterion("katana", getItem(GearItemSets.KATANA.gearItem()))
                .addCriterion("machete", getItem(GearItemSets.MACHETE.gearItem()))
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(saver, id("swords"));

        AdvancementHolder bigJobTools = Advancement.Builder.advancement()
                .parent(standardTools)
                .display(icon(GearItemSets.HAMMER.gearItem()), title("big_job_tools"), description("big_job_tools"), AdvancementType.TASK, true, true, false)
                .addCriterion("hammer", getItem(GearItemSets.HAMMER.gearItem()))
                .addCriterion("excavator", getItem(GearItemSets.EXCAVATOR.gearItem()))
                .addCriterion("lumber_axe", getItem(GearItemSets.SAW.gearItem()))
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(saver, id("big_job_tools"));

        AdvancementHolder crossbow = simpleGetItem(saver, GearItemSets.CROSSBOW.gearItem(), bow);

        AdvancementHolder mattock = simpleGetItem(saver, GearItemSets.MATTOCK.gearItem(), standardTools);

        AdvancementHolder sickle = simpleGetItem(saver, GearItemSets.SICKLE.gearItem(), mattock);

        //endregion

        //region Nether

        AdvancementHolder nether = Advancement.Builder.advancement()
                .parent(root)
                .display(icon(Items.OBSIDIAN), title("nether"), description("nether"), AdvancementType.TASK, false, false, false)
                .addCriterion("entered_nether", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER))
                .save(saver, id("nether"));

        AdvancementHolder netherPlants = Advancement.Builder.advancement()
                .parent(nether)
                .display(icon(SgItems.NETHER_BANANA), title("nether_plants"), description("nether_plants"), AdvancementType.TASK, true, true, false)
                .addCriterion("banana", getItem(SgItems.NETHER_BANANA))
                .addCriterion("sapling", getItem(SgBlocks.NETHERWOOD_SAPLING))
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(saver, id("nether_plants"));

        AdvancementHolder blazeGold = simpleGetItem(saver, CraftingItems.BLAZE_GOLD_INGOT, nether, "blaze_gold");
        AdvancementHolder crimsonIron = Advancement.Builder.advancement()
                .parent(nether)
                .display(icon(CraftingItems.CRIMSON_IRON_INGOT), title("crimson_iron"), description("crimson_iron"), AdvancementType.TASK, true, true, false)
                .addCriterion("get_ore", getItem(CraftingItems.RAW_CRIMSON_IRON))
                .addCriterion("get_ingot", getItem(CraftingItems.CRIMSON_IRON_INGOT))
                .save(saver, id("crimson_iron"));

        AdvancementHolder materialGrader = Advancement.Builder.advancement()
                .parent(blazeGold)
                .display(icon(SgBlocks.MATERIAL_GRADER), title("material_grader"), description("material_grader"), AdvancementType.TASK, true, true, false)
                .addCriterion("get_grader", getItem(SgBlocks.MATERIAL_GRADER))
                .addCriterion("get_catalyst", getItem(items, SgTags.Items.GRADER_CATALYSTS_TIER_1))
                .save(saver, id("material_grader"));

        AdvancementHolder crimsonSteel = simpleGetItem(saver, CraftingItems.CRIMSON_STEEL_INGOT, crimsonIron, "crimson_steel");
        AdvancementHolder salvager = simpleGetItem(saver, SgBlocks.SALVAGER, crimsonIron);

        var emeraldTipUpgrade = new ItemStackTemplate(SgItems.TIP,
                DataComponentPatch.builder()
                        .set(
                                SgDataComponents.MATERIAL_LIST.get(),
                                List.of(
                                        MaterialInstance.of(Const.Materials.EMERALD)
                                )
                        )
                        .build()
        );
        AdvancementHolder highDurability = Advancement.Builder.advancement()
                .parent(materialGrader)
                .display(icon(emeraldTipUpgrade), title("high_durability"), description("high_durability"), AdvancementType.TASK, true, true, false)
                .addCriterion("durability", SgCriteriaTriggers.GEAR_PROPERTY.get().createCriterion(new GearPropertyTrigger.Instance(Optional.empty(), GearProperties.DURABILITY.get(), MinMaxBounds.Doubles.atLeast(16_000))))
                .save(saver, id("high_durability"));
        AdvancementHolder graderCatalyst2 = Advancement.Builder.advancement()
                .parent(materialGrader)
                .display(icon(CraftingItems.BLAZING_DUST), title("grader_catalyst_2"), description("grader_catalyst_2"), AdvancementType.TASK, true, true, false)
                .addCriterion("get_item", getItem(items, SgTags.Items.GRADER_CATALYSTS_TIER_2))
                .save(saver, id("grader_catalyst_2"));

        AdvancementHolder graderCatalyst3 = Advancement.Builder.advancement()
                .parent(graderCatalyst2)
                .display(icon(CraftingItems.GLITTERY_DUST), title("grader_catalyst_3"), description("grader_catalyst_3"), AdvancementType.TASK, true, true, false)
                .addCriterion("get_item", getItem(items, SgTags.Items.GRADER_CATALYSTS_TIER_3))
                .save(saver, id("grader_catalyst_3"));

        //endregion

        //region The End

        AdvancementHolder theEnd = Advancement.Builder.advancement()
                .parent(nether)
                .display(icon(Items.END_STONE), title("the_end"), description("the_end"), AdvancementType.TASK, false, false, false)
                .addCriterion("entered_the_end", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END))
                .save(saver, id("the_end"));

        AdvancementHolder azureSilver = Advancement.Builder.advancement()
                .parent(theEnd)
                .display(icon(CraftingItems.AZURE_SILVER_INGOT), title("azure_silver"), description("azure_silver"), AdvancementType.TASK, true, true, false)
                .addCriterion("get_ore", getItem(CraftingItems.RAW_AZURE_SILVER))
                .addCriterion("get_ingot", getItem(CraftingItems.AZURE_SILVER_INGOT))
                .save(saver, id("azure_silver"));

        AdvancementHolder azureElectrum = Advancement.Builder.advancement()
                .parent(azureSilver)
                .display(icon(CraftingItems.AZURE_ELECTRUM_INGOT), title("azure_electrum"), description("azure_electrum"), AdvancementType.TASK, true, true, false)
                .addCriterion("get_ingot", getItem(CraftingItems.AZURE_ELECTRUM_INGOT))
                .save(saver, id("azure_electrum"));

        ItemStackTemplate azureSilverBoots = new ItemStackTemplate(GearItemSets.BOOTS.gearItem(),
                DataComponentPatch.builder()
                        .set(
                                SgDataComponents.GEAR_CONSTRUCTION.get(),
                                new GearConstructionData(
                                        PartList.immutable(
                                                PartInstance.create(
                                                        DataResource.part(GearItemSets.BOOTS.partName()),
                                                        GearItemSets.BOOTS.mainPart(),
                                                        Const.Materials.AZURE_SILVER
                                                )
                                        )
                                )
                        )
                        .build()
        );
        AdvancementHolder moonwalker = Advancement.Builder.advancement()
                .parent(azureSilver)
                .display(icon(azureSilverBoots), title("moonwalker"), description("moonwalker"), AdvancementType.TASK, true, true, false)
                .addCriterion("fall_with_moonwalker_boots", SgCriteriaTriggers.FALL_WITH_MOONWALKER.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                .save(saver, id("moonwalker"));

        //endregion
    }

    private static AdvancementHolder simpleGetItem(BootstrapContext<Advancement> saver, ItemLike item, AdvancementHolder parent) {
        return simpleGetItem(saver, item, parent, NameUtils.fromItem(item).getPath());
    }

    private static AdvancementHolder simpleGetItem(BootstrapContext<Advancement> saver, ItemLike item, AdvancementHolder parent, String key) {
        return simpleGetItem(saver, item, new ItemStackTemplate(item.asItem()), parent, key);
    }

    private static AdvancementHolder simpleGetItem(BootstrapContext<Advancement> saver, ItemLike item, ItemStackTemplate icon, AdvancementHolder parent, String key) {
        return Advancement.Builder.advancement()
                .parent(parent)
                .display(icon(icon), title(key), description(key), AdvancementType.TASK, true, true, false)
                .addCriterion("get_item", getItem(item))
                .save(saver, id(key));
    }

    private static ItemStackTemplate icon(ItemLike item) {
        return new ItemStackTemplate(item.asItem());
    }

    private static ItemStackTemplate icon(ItemStackTemplate icon) {
        return icon;
    }

    private static String id(String path) {
        return SilentGear.getId(path).toString();
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> getItem(ItemLike... items) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(items);
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> getItem(HolderGetter<Item> items, TagKey<Item> tag) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, tag).build()
        );
    }

    private static Component title(String key) {
        return Component.translatable("advancements.silentgear." + key + ".title");
    }

    private static Component description(String key) {
        return Component.translatable("advancements.silentgear." + key + ".description");
    }
}
