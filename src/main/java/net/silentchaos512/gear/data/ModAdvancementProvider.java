package net.silentchaos512.gear.data;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
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
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.event.GearEvents;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.part.LazyPartData;
import net.silentchaos512.gear.gear.trait.DurabilityTrait;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.advancements.GenericIntTrigger;
import net.silentchaos512.lib.util.NameUtils;

import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;

public class ModAdvancementProvider extends ForgeAdvancementProvider {
    public ModAdvancementProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper(), Collections.singletonList(new Advancements()));
    }

    private static Path getPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }

    private static class Advancements implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
            ItemStack rootIcon = new ItemStack(SgItems.PICKAXE);
            GearData.writeConstructionParts(rootIcon, ImmutableList.of(
                    LazyPartData.of(Const.Parts.PICKAXE_HEAD, SgItems.PICKAXE_HEAD.get(), Const.Materials.CRIMSON_STEEL),
                    LazyPartData.of(Const.Parts.ROD, SgItems.ROD.get(), Const.Materials.BLAZE_GOLD),
                    LazyPartData.of(Const.Parts.TIP, SgItems.TIP.get(), Const.Materials.AZURE_ELECTRUM),
                    LazyPartData.of(Const.Parts.GRIP, SgItems.GRIP.get(), Const.Materials.WOOL_BLACK),
                    LazyPartData.of(Const.Parts.BINDING, SgItems.BINDING.get(), Const.Materials.STRING)
            ));
            Advancement root = Advancement.Builder.advancement()
                    .display(rootIcon, title("root"), description("root"), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false)
                    .addCriterion("get_item", getItem(Items.CRAFTING_TABLE))
                    .save(saver, id("root"));

            Advancement overworldPlants = Advancement.Builder.advancement()
                    .parent(root)
                    .display(CraftingItems.FLAX_FIBER, title("overworld_plants"), description("overworld_plants"), null, FrameType.TASK, true, true, false)
                    .addCriterion("flax_seeds", getItem(SgItems.FLAX_SEEDS))
                    .addCriterion("flax_fibers", getItem(CraftingItems.FLAX_FIBER))
                    .addCriterion("fluffy_seeds", getItem(SgItems.FLUFFY_SEEDS))
                    .addCriterion("fluffy_puffs", getItem(CraftingItems.FLUFFY_PUFF))
                    .requirements(RequirementsStrategy.AND)
                    .save(saver, id("overworld_plants"));
            Advancement kachink1 = Advancement.Builder.advancement()
                    .parent(root)
                    .display(Items.IRON_NUGGET, title("kachink1"), description("kachink1"), null, FrameType.TASK, true, true, false)
                    .addCriterion("kachink", genericInt(GearHelper.DAMAGE_FACTOR_CHANGE, 1))
                    .save(saver, id("kachink1"));
            Advancement kachink2 = Advancement.Builder.advancement()
                    .parent(kachink1)
                    .display(CraftingItems.DIAMOND_SHARD, title("kachink2"), description("kachink2"), null, FrameType.TASK, true, true, false)
                    .addCriterion("kachink", genericInt(DurabilityTrait.TRIGGER_BRITTLE, 1))
                    .save(saver, id("kachink2"));

            /*Advancement crudeTool = Advancement.Builder.advancement()
                    .parent(root)
                    .display(CraftingItems.ROUGH_ROD, title("crude_tool"), description("crude_tool"), null, FrameType.TASK, true, true, false)
                    .addCriterion("tool_has_rough_rod", genericInt(GearEvents.CRAFTED_WITH_ROUGH_ROD, 1))
                    .save(consumer, id("crude_tool"));
            Advancement survivalTool = Advancement.Builder.advancement()
                    .parent(crudeTool)
                    .display(ModItems.KNIFE, title("survival_tool"), description("survival_tool"), null, FrameType.TASK, true, true, false)
                    .addCriterion("knife", getItem(ModItems.KNIFE))
                    .addCriterion("dagger", getItem(ModItems.DAGGER))
                    .requirements(RequirementsStrategy.OR)
                    .save(consumer, id("survival_tool"));*/
            Advancement templateBoard = simpleGetItem(saver, CraftingItems.TEMPLATE_BOARD, root);

            Advancement blueprintPaper = simpleGetItem(saver, CraftingItems.BLUEPRINT_PAPER, templateBoard);
            Advancement repairKit;
            {
                Advancement.Builder builder = Advancement.Builder.advancement()
                        .parent(templateBoard)
                        .display(SgItems.CRUDE_REPAIR_KIT, title("repair_kit"), description("repair_kit"), null, FrameType.TASK, true, true, false)
                        .requirements(RequirementsStrategy.OR);
                SgItems.getItems(RepairKitItem.class).forEach(item ->
                        builder.addCriterion(NameUtils.fromItem(item).getPath(), getItem(item)));
                repairKit = builder.save(saver, id("repair_kit"));
            }

            Advancement crimsonRepairKit = simpleGetItem(saver, SgItems.CRIMSON_REPAIR_KIT, repairKit);
            Advancement azureRepairKit = simpleGetItem(saver, SgItems.AZURE_REPAIR_KIT, crimsonRepairKit);
            Advancement repairFromBroken = Advancement.Builder.advancement()
                    .parent(repairKit)
                    .display(Items.FLINT, title("repair_from_broken"), description("repair_from_broken"), null, FrameType.TASK, true, true, false)
                    .addCriterion("repair", genericInt(GearEvents.REPAIR_FROM_BROKEN, 1))
                    .save(saver, id("repair_from_broken"));

            Advancement blueprintBook = simpleGetItem(saver, SgItems.BLUEPRINT_BOOK, blueprintPaper);

            Advancement tipUpgrade = simpleGetItem(saver, SgItems.TIP, SgItems.TIP.get().create(LazyMaterialInstance.of(Const.Materials.EXAMPLE)), templateBoard, "tip_upgrade");

            //region Gear

            Advancement armor = Advancement.Builder.advancement()
                    .parent(blueprintPaper)
                    .display(SgItems.HELMET, title("armor"), description("armor"), null, FrameType.TASK, true, true, false)
                    .addCriterion("helmet", getItem(SgItems.HELMET))
                    .addCriterion("chestplate", getItem(SgItems.CHESTPLATE))
                    .addCriterion("leggings", getItem(SgItems.LEGGINGS))
                    .addCriterion("boots", getItem(SgItems.BOOTS))
                    .requirements(RequirementsStrategy.OR)
                    .save(saver, id("armor"));

            Advancement bow = Advancement.Builder.advancement()
                    .parent(blueprintPaper)
                    .display(SgItems.BOW, title("bow"), description("bow"), null, FrameType.TASK, true, true, false)
                    .addCriterion("get_item", getItem(SgItems.BOW))
                    .save(saver, id("bow"));
            Advancement standardTools = Advancement.Builder.advancement()
                    .parent(blueprintPaper)
                    .display(SgItems.PICKAXE, title("standard_tools"), description("standard_tools"), null, FrameType.TASK, true, true, false)
                    .addCriterion("pickaxe", getItem(SgItems.PICKAXE))
                    .addCriterion("shovel", getItem(SgItems.SHOVEL))
                    .addCriterion("axe", getItem(SgItems.AXE))
                    .requirements(RequirementsStrategy.AND)
                    .save(saver, id("standard_tools"));
            Advancement swords = Advancement.Builder.advancement()
                    .parent(blueprintPaper)
                    .display(SgItems.SWORD, title("swords"), description("swords"), null, FrameType.TASK, true, true, false)
                    .addCriterion("sword", getItem(SgItems.SWORD))
                    .addCriterion("katana", getItem(SgItems.KATANA))
                    .addCriterion("machete", getItem(SgItems.MACHETE))
                    .requirements(RequirementsStrategy.AND)
                    .save(saver, id("swords"));

            Advancement bigJobTools = Advancement.Builder.advancement()
                    .parent(standardTools)
                    .display(SgItems.HAMMER, title("big_job_tools"), description("big_job_tools"), null, FrameType.TASK, true, true, false)
                    .addCriterion("hammer", getItem(SgItems.HAMMER))
                    .addCriterion("excavator", getItem(SgItems.EXCAVATOR))
                    .addCriterion("lumber_axe", getItem(SgItems.SAW))
                    .requirements(RequirementsStrategy.AND)
                    .save(saver, id("big_job_tools"));

            Advancement crossbow = simpleGetItem(saver, SgItems.CROSSBOW, bow);

            Advancement mattock = simpleGetItem(saver, SgItems.MATTOCK, standardTools);

            Advancement sickle = simpleGetItem(saver, SgItems.SICKLE, mattock);

            //endregion

            //region Nether

            Advancement nether = Advancement.Builder.advancement()
                    .parent(root)
                    .display(Items.OBSIDIAN, title("nether"), description("nether"), null, FrameType.TASK, false, false, false)
                    .addCriterion("entered_nether", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER))
                    .save(saver, id("nether"));

            Advancement netherPlants = Advancement.Builder.advancement()
                    .parent(nether)
                    .display(SgItems.NETHER_BANANA, title("nether_plants"), description("nether_plants"), null, FrameType.TASK, true, true, false)
                    .addCriterion("banana", getItem(SgItems.NETHER_BANANA))
                    .addCriterion("sapling", getItem(SgBlocks.NETHERWOOD_SAPLING))
                    .requirements(RequirementsStrategy.AND)
                    .save(saver, id("nether_plants"));

            Advancement blazeGold = simpleGetItem(saver, CraftingItems.BLAZE_GOLD_INGOT, nether, "blaze_gold");
            Advancement crimsonIron = Advancement.Builder.advancement()
                    .parent(nether)
                    .display(CraftingItems.CRIMSON_IRON_INGOT, title("crimson_iron"), description("crimson_iron"), null, FrameType.TASK, true, true, false)
                    .addCriterion("get_ore", getItem(CraftingItems.RAW_CRIMSON_IRON))
                    .addCriterion("get_ingot", getItem(CraftingItems.CRIMSON_IRON_INGOT))
                    .save(saver, id("crimson_iron"));

            Advancement materialGrader = Advancement.Builder.advancement()
                    .parent(blazeGold)
                    .display(SgBlocks.MATERIAL_GRADER, title("material_grader"), description("material_grader"), null, FrameType.TASK, true, true, false)
                    .addCriterion("get_grader", getItem(SgBlocks.MATERIAL_GRADER))
                    .addCriterion("get_catalyst", getItem(SgTags.Items.GRADER_CATALYSTS_TIER_1))
                    .save(saver, id("material_grader"));

            Advancement crimsonSteel = simpleGetItem(saver, CraftingItems.CRIMSON_STEEL_INGOT, crimsonIron, "crimson_steel");
            Advancement salvager = simpleGetItem(saver, SgBlocks.SALVAGER, crimsonIron);

            Advancement highDurability = Advancement.Builder.advancement()
                    .parent(materialGrader)
                    .display(SgItems.TIP.get().create(LazyMaterialInstance.of(Const.Materials.EMERALD)), title("high_durability"), description("high_durability"), null, FrameType.TASK, true, true, false)
                    .addCriterion("durability", genericInt(GearEvents.MAX_DURABILITY, 16_000))
                    .save(saver, id("high_durability"));
            Advancement graderCatalyst2 = Advancement.Builder.advancement()
                    .parent(materialGrader)
                    .display(CraftingItems.BLAZING_DUST, title("grader_catalyst_2"), description("grader_catalyst_2"), null, FrameType.TASK, true, true, false)
                    .addCriterion("get_item", getItem(SgTags.Items.GRADER_CATALYSTS_TIER_2))
                    .save(saver, id("grader_catalyst_2"));

            Advancement graderCatalyst3 = Advancement.Builder.advancement()
                    .parent(graderCatalyst2)
                    .display(CraftingItems.GLITTERY_DUST, title("grader_catalyst_3"), description("grader_catalyst_3"), null, FrameType.TASK, true, true, false)
                    .addCriterion("get_item", getItem(SgTags.Items.GRADER_CATALYSTS_TIER_3))
                    .save(saver, id("grader_catalyst_3"));

            //endregion

            //region The End

            Advancement theEnd = Advancement.Builder.advancement()
                    .parent(nether)
                    .display(Items.END_STONE, title("the_end"), description("the_end"), null, FrameType.TASK, false, false, false)
                    .addCriterion("entered_the_end", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END))
                    .save(saver, id("the_end"));

            Advancement azureSilver = Advancement.Builder.advancement()
                    .parent(theEnd)
                    .display(CraftingItems.AZURE_SILVER_INGOT, title("azure_silver"), description("azure_silver"), null, FrameType.TASK, true, true, false)
                    .addCriterion("get_ore", getItem(CraftingItems.RAW_AZURE_SILVER))
                    .addCriterion("get_ingot", getItem(CraftingItems.AZURE_SILVER_INGOT))
                    .save(saver, id("azure_silver"));

            Advancement azureElectrum = Advancement.Builder.advancement()
                    .parent(azureSilver)
                    .display(CraftingItems.AZURE_ELECTRUM_INGOT, title("azure_electrum"), description("azure_electrum"), null, FrameType.TASK, true, true, false)
                    .addCriterion("get_ingot", getItem(CraftingItems.AZURE_ELECTRUM_INGOT))
                    .save(saver, id("azure_electrum"));

            ItemStack azureSilverBoots = new ItemStack(SgItems.BOOTS);
            GearData.writeConstructionParts(azureSilverBoots, Collections.singleton(LazyPartData.of(Const.Parts.ARMOR_BODY, SgItems.BOOT_PLATES.get(), LazyMaterialInstance.of(Const.Materials.AZURE_SILVER))));
            Advancement moonwalker = Advancement.Builder.advancement()
                    .parent(azureSilver)
                    .display(azureSilverBoots, title("moonwalker"), description("moonwalker"), null, FrameType.TASK, true, true, false)
                    .addCriterion("fall_with_moonwalker_boots", genericInt(GearEvents.FALL_WITH_MOONWALKER, 1))
                    .save(saver, id("moonwalker"));

            //endregion
        }

        private static Advancement simpleGetItem(Consumer<Advancement> consumer, ItemLike item, Advancement parent) {
            return simpleGetItem(consumer, item, parent, NameUtils.fromItem(item).getPath());
        }

        private static Advancement simpleGetItem(Consumer<Advancement> consumer, ItemLike item, Advancement parent, String key) {
            return simpleGetItem(consumer, item, new ItemStack(item), parent, key);
        }

        private static Advancement simpleGetItem(Consumer<Advancement> consumer, ItemLike item, ItemStack icon, Advancement parent, String key) {
            return Advancement.Builder.advancement()
                    .parent(parent)
                    .display(icon, title(key), description(key), null, FrameType.TASK, true, true, false)
                    .addCriterion("get_item", getItem(item))
                    .save(consumer, id(key));
        }

        private static String id(String path) {
            return SilentGear.getId(path).toString();
        }

        private static CriterionTriggerInstance getItem(ItemLike... items) {
            return InventoryChangeTrigger.TriggerInstance.hasItems(items);
        }

        private static CriterionTriggerInstance getItem(TagKey<Item> tag) {
            return InventoryChangeTrigger.TriggerInstance.hasItems(new ItemPredicate(tag, null, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY));
        }

        private static CriterionTriggerInstance genericInt(ResourceLocation id, int value) {
            return GenericIntTrigger.Instance.instance(id, value);
        }

        private static Component title(String key) {
            return Component.translatable("advancements.silentgear." + key + ".title");
        }

        private static Component description(String key) {
            return Component.translatable("advancements.silentgear." + key + ".description");
        }
    }
}
