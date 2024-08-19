package net.silentchaos512.gear.data;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.advancements.criterion.GearPropertyTrigger;
import net.silentchaos512.gear.advancements.criterion.GearRepairedTrigger;
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
import java.util.Optional;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper(), Collections.singletonList(new Advancements()));
    }

    private static class Advancements implements AdvancementGenerator {
        @SuppressWarnings("unused")
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            ItemStack rootIcon = new ItemStack(GearItemSets.PICKAXE.gearItem());
            GearData.writeConstructionParts(rootIcon, ImmutableList.of(
                    PartInstance.create(Const.Parts.PICKAXE_HEAD, GearItemSets.PICKAXE.mainPart(), Const.Materials.CRIMSON_STEEL),
                    PartInstance.create(Const.Parts.ROD, SgItems.ROD.get(), Const.Materials.BLAZE_GOLD),
                    PartInstance.create(Const.Parts.TIP, SgItems.TIP.get(), Const.Materials.AZURE_ELECTRUM),
                    PartInstance.create(Const.Parts.GRIP, SgItems.GRIP.get(), Const.Materials.WOOL_BLACK),
                    PartInstance.create(Const.Parts.BINDING, SgItems.BINDING.get(), Const.Materials.STRING)
            ));
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(rootIcon, title("root"), description("root"), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/adventure.png"), AdvancementType.TASK, false, false, false)
                    .addCriterion("get_item", getItem(Items.CRAFTING_TABLE))
                    .save(saver, id("root"));

            AdvancementHolder overworldPlants = Advancement.Builder.advancement()
                    .parent(root)
                    .display(CraftingItems.FLAX_FIBER, title("overworld_plants"), description("overworld_plants"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("flax_seeds", getItem(SgItems.FLAX_SEEDS))
                    .addCriterion("flax_fibers", getItem(CraftingItems.FLAX_FIBER))
                    .addCriterion("fluffy_seeds", getItem(SgItems.FLUFFY_SEEDS))
                    .addCriterion("fluffy_puffs", getItem(CraftingItems.FLUFFY_PUFF))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(saver, id("overworld_plants"));
            AdvancementHolder kachink1 = Advancement.Builder.advancement()
                    .parent(root)
                    .display(Items.IRON_NUGGET, title("kachink1"), description("kachink1"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("kachink", SgCriteriaTriggers.DAMAGE_FACTOR_CHANGE.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .save(saver, id("kachink1"));
            AdvancementHolder kachink2 = Advancement.Builder.advancement()
                    .parent(kachink1)
                    .display(CraftingItems.DIAMOND_SHARD, title("kachink2"), description("kachink2"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("kachink", SgCriteriaTriggers.BRITTLE_DAMAGE.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .save(saver, id("kachink2"));

            /*Advancement crudeTool = Advancement.Builder.advancement()
                    .parent(root)
                    .display(CraftingItems.ROUGH_ROD, title("crude_tool"), description("crude_tool"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("tool_has_rough_rod", genericInt(GearEvents.CRAFTED_WITH_ROUGH_ROD, 1))
                    .save(consumer, id("crude_tool"));
            Advancement survivalTool = Advancement.Builder.advancement()
                    .parent(crudeTool)
                    .display(ModItems.KNIFE, title("survival_tool"), description("survival_tool"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("knife", getItem(ModItems.KNIFE))
                    .addCriterion("dagger", getItem(ModItems.DAGGER))
                    .requirements(RequirementsStrategy.OR)
                    .save(consumer, id("survival_tool"));*/
            AdvancementHolder templateBoard = simpleGetItem(saver, CraftingItems.TEMPLATE_BOARD, root);

            AdvancementHolder blueprintPaper = simpleGetItem(saver, CraftingItems.BLUEPRINT_PAPER, templateBoard);
            AdvancementHolder repairKit;
            {
                Advancement.Builder builder = Advancement.Builder.advancement()
                        .parent(templateBoard)
                        .display(SgItems.CRUDE_REPAIR_KIT, title("repair_kit"), description("repair_kit"), null, AdvancementType.TASK, true, true, false)
                        .requirements(AdvancementRequirements.Strategy.OR);
                SgItems.getItems(RepairKitItem.class).forEach(item ->
                        builder.addCriterion(NameUtils.fromItem(item).getPath(), getItem(item)));
                repairKit = builder.save(saver, id("repair_kit"));
            }

            AdvancementHolder crimsonRepairKit = simpleGetItem(saver, SgItems.CRIMSON_REPAIR_KIT, repairKit);
            AdvancementHolder azureRepairKit = simpleGetItem(saver, SgItems.AZURE_REPAIR_KIT, crimsonRepairKit);
            AdvancementHolder repairFromBroken = Advancement.Builder.advancement()
                    .parent(repairKit)
                    .display(Items.FLINT, title("repair_from_broken"), description("repair_from_broken"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("repair", SgCriteriaTriggers.GEAR_REPAIRED.get().createCriterion(new GearRepairedTrigger.Instance(Optional.empty(), MinMaxBounds.Ints.atLeast(1), MinMaxBounds.Ints.atLeast(1))))
                    .save(saver, id("repair_from_broken"));

            AdvancementHolder blueprintBook = simpleGetItem(saver, SgItems.BLUEPRINT_BOOK, blueprintPaper);

            AdvancementHolder tipUpgrade = simpleGetItem(saver, SgItems.TIP, SgItems.TIP.get().create(MaterialInstance.of(Const.Materials.EXAMPLE)), templateBoard, "tip_upgrade");

            //region Gear

            AdvancementHolder armor = Advancement.Builder.advancement()
                    .parent(blueprintPaper)
                    .display(GearItemSets.HELMET.gearItem(), title("armor"), description("armor"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("helmet", getItem(GearItemSets.HELMET.gearItem()))
                    .addCriterion("chestplate", getItem(GearItemSets.CHESTPLATE.gearItem()))
                    .addCriterion("leggings", getItem(GearItemSets.LEGGINGS.gearItem()))
                    .addCriterion("boots", getItem(GearItemSets.BOOTS.gearItem()))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(saver, id("armor"));

            AdvancementHolder bow = Advancement.Builder.advancement()
                    .parent(blueprintPaper)
                    .display(GearItemSets.BOW.gearItem(), title("bow"), description("bow"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", getItem(GearItemSets.BOW.gearItem()))
                    .save(saver, id("bow"));
            AdvancementHolder standardTools = Advancement.Builder.advancement()
                    .parent(blueprintPaper)
                    .display(GearItemSets.PICKAXE.gearItem(), title("standard_tools"), description("standard_tools"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("pickaxe", getItem(GearItemSets.PICKAXE.gearItem()))
                    .addCriterion("shovel", getItem(GearItemSets.SHOVEL.gearItem()))
                    .addCriterion("axe", getItem(GearItemSets.AXE.gearItem()))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(saver, id("standard_tools"));
            AdvancementHolder swords = Advancement.Builder.advancement()
                    .parent(blueprintPaper)
                    .display(GearItemSets.SWORD.gearItem(), title("swords"), description("swords"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("sword", getItem(GearItemSets.SWORD.gearItem()))
                    .addCriterion("katana", getItem(GearItemSets.KATANA.gearItem()))
                    .addCriterion("machete", getItem(GearItemSets.MACHETE.gearItem()))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(saver, id("swords"));

            AdvancementHolder bigJobTools = Advancement.Builder.advancement()
                    .parent(standardTools)
                    .display(GearItemSets.HAMMER.gearItem(), title("big_job_tools"), description("big_job_tools"), null, AdvancementType.TASK, true, true, false)
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
                    .display(Items.OBSIDIAN, title("nether"), description("nether"), null, AdvancementType.TASK, false, false, false)
                    .addCriterion("entered_nether", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER))
                    .save(saver, id("nether"));

            AdvancementHolder netherPlants = Advancement.Builder.advancement()
                    .parent(nether)
                    .display(SgItems.NETHER_BANANA, title("nether_plants"), description("nether_plants"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("banana", getItem(SgItems.NETHER_BANANA))
                    .addCriterion("sapling", getItem(SgBlocks.NETHERWOOD_SAPLING))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(saver, id("nether_plants"));

            AdvancementHolder blazeGold = simpleGetItem(saver, CraftingItems.BLAZE_GOLD_INGOT, nether, "blaze_gold");
            AdvancementHolder crimsonIron = Advancement.Builder.advancement()
                    .parent(nether)
                    .display(CraftingItems.CRIMSON_IRON_INGOT, title("crimson_iron"), description("crimson_iron"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_ore", getItem(CraftingItems.RAW_CRIMSON_IRON))
                    .addCriterion("get_ingot", getItem(CraftingItems.CRIMSON_IRON_INGOT))
                    .save(saver, id("crimson_iron"));

            AdvancementHolder materialGrader = Advancement.Builder.advancement()
                    .parent(blazeGold)
                    .display(SgBlocks.MATERIAL_GRADER, title("material_grader"), description("material_grader"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_grader", getItem(SgBlocks.MATERIAL_GRADER))
                    .addCriterion("get_catalyst", getItem(SgTags.Items.GRADER_CATALYSTS_TIER_1))
                    .save(saver, id("material_grader"));

            AdvancementHolder crimsonSteel = simpleGetItem(saver, CraftingItems.CRIMSON_STEEL_INGOT, crimsonIron, "crimson_steel");
            AdvancementHolder salvager = simpleGetItem(saver, SgBlocks.SALVAGER, crimsonIron);

            AdvancementHolder highDurability = Advancement.Builder.advancement()
                    .parent(materialGrader)
                    .display(SgItems.TIP.get().create(MaterialInstance.of(Const.Materials.EMERALD)), title("high_durability"), description("high_durability"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("durability", SgCriteriaTriggers.GEAR_PROPERTY.get().createCriterion(new GearPropertyTrigger.Instance(Optional.empty(), GearProperties.DURABILITY.get(), MinMaxBounds.Doubles.atLeast(16_000))))
                    .save(saver, id("high_durability"));
            AdvancementHolder graderCatalyst2 = Advancement.Builder.advancement()
                    .parent(materialGrader)
                    .display(CraftingItems.BLAZING_DUST, title("grader_catalyst_2"), description("grader_catalyst_2"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", getItem(SgTags.Items.GRADER_CATALYSTS_TIER_2))
                    .save(saver, id("grader_catalyst_2"));

            AdvancementHolder graderCatalyst3 = Advancement.Builder.advancement()
                    .parent(graderCatalyst2)
                    .display(CraftingItems.GLITTERY_DUST, title("grader_catalyst_3"), description("grader_catalyst_3"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", getItem(SgTags.Items.GRADER_CATALYSTS_TIER_3))
                    .save(saver, id("grader_catalyst_3"));

            //endregion

            //region The End

            AdvancementHolder theEnd = Advancement.Builder.advancement()
                    .parent(nether)
                    .display(Items.END_STONE, title("the_end"), description("the_end"), null, AdvancementType.TASK, false, false, false)
                    .addCriterion("entered_the_end", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END))
                    .save(saver, id("the_end"));

            AdvancementHolder azureSilver = Advancement.Builder.advancement()
                    .parent(theEnd)
                    .display(CraftingItems.AZURE_SILVER_INGOT, title("azure_silver"), description("azure_silver"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_ore", getItem(CraftingItems.RAW_AZURE_SILVER))
                    .addCriterion("get_ingot", getItem(CraftingItems.AZURE_SILVER_INGOT))
                    .save(saver, id("azure_silver"));

            AdvancementHolder azureElectrum = Advancement.Builder.advancement()
                    .parent(azureSilver)
                    .display(CraftingItems.AZURE_ELECTRUM_INGOT, title("azure_electrum"), description("azure_electrum"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_ingot", getItem(CraftingItems.AZURE_ELECTRUM_INGOT))
                    .save(saver, id("azure_electrum"));

            ItemStack azureSilverBoots = new ItemStack(GearItemSets.BOOTS.gearItem());
            GearData.writeConstructionParts(azureSilverBoots, Collections.singleton(PartInstance.create(Const.Parts.ARMOR_BODY, GearItemSets.BOOTS.mainPart(), Const.Materials.AZURE_SILVER)));
            AdvancementHolder moonwalker = Advancement.Builder.advancement()
                    .parent(azureSilver)
                    .display(azureSilverBoots, title("moonwalker"), description("moonwalker"), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("fall_with_moonwalker_boots", SgCriteriaTriggers.FALL_WITH_MOONWALKER.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .save(saver, id("moonwalker"));

            //endregion
        }

        private static AdvancementHolder simpleGetItem(Consumer<AdvancementHolder> saver, ItemLike item, AdvancementHolder parent) {
            return simpleGetItem(saver, item, parent, NameUtils.fromItem(item).getPath());
        }

        private static AdvancementHolder simpleGetItem(Consumer<AdvancementHolder> saver, ItemLike item, AdvancementHolder parent, String key) {
            return simpleGetItem(saver, item, new ItemStack(item), parent, key);
        }

        private static AdvancementHolder simpleGetItem(Consumer<AdvancementHolder> saver, ItemLike item, ItemStack icon, AdvancementHolder parent, String key) {
            return Advancement.Builder.advancement()
                    .parent(parent)
                    .display(icon, title(key), description(key), null, AdvancementType.TASK, true, true, false)
                    .addCriterion("get_item", getItem(item))
                    .save(saver, id(key));
        }

        private static String id(String path) {
            return SilentGear.getId(path).toString();
        }

        private static Criterion<InventoryChangeTrigger.TriggerInstance> getItem(ItemLike... items) {
            return InventoryChangeTrigger.TriggerInstance.hasItems(items);
        }

        private static Criterion<InventoryChangeTrigger.TriggerInstance> getItem(TagKey<Item> tag) {
            return InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(tag).build()
            );
        }

        private static Component title(String key) {
            return Component.translatable("advancements.silentgear." + key + ".title");
        }

        private static Component description(String key) {
            return Component.translatable("advancements.silentgear." + key + ".description");
        }
    }
}
