package net.silentchaos512.gear.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.event.GearEvents;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.traits.DurabilityTrait;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.advancements.GenericIntTrigger;
import net.silentchaos512.lib.util.NameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class ModAdvancementProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public ModAdvancementProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (p_204017_3_) -> {
            if (!set.add(p_204017_3_.getId())) {
                throw new IllegalStateException("Duplicate advancement " + p_204017_3_.getId());
            } else {
                Path path1 = getPath(path, p_204017_3_);

                try {
                    IDataProvider.save(GSON, cache, p_204017_3_.copy().serialize(), path1);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }

            }
        };

        new Advancements().accept(consumer);
    }

    @Override
    public String getName() {
        return "Silent Gear - Advancements";
    }

    private static Path getPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }

    private static class Advancements implements Consumer<Consumer<Advancement>> {
        @Override
        public void accept(Consumer<Advancement> consumer) {
            Advancement root = Advancement.Builder.builder()
                    .withDisplay(ModItems.SHOVEL_BLUEPRINT, title("root"), description("root"), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false)
                    .withCriterion("get_item", getItem(Items.CRAFTING_TABLE))
                    .register(consumer, id("root"));

            Advancement overworldPlants = Advancement.Builder.builder()
                    .withParent(root)
                    .withDisplay(CraftingItems.FLAX_FIBER, title("overworld_plants"), description("overworld_plants"), null, FrameType.TASK, true, true, false)
                    .withCriterion("seeds", getItem(ModItems.FLAXSEEDS))
                    .withCriterion("fiber", getItem(CraftingItems.FLAX_FIBER))
                    .withRequirementsStrategy(IRequirementsStrategy.AND)
                    .register(consumer, id("overworld_plants"));
            Advancement kachink1 = Advancement.Builder.builder()
                    .withParent(root)
                    .withDisplay(Items.IRON_NUGGET, title("kachink1"), description("kachink1"), null, FrameType.TASK, true, true, false)
                    .withCriterion("kachink", genericInt(GearHelper.DAMAGE_FACTOR_CHANGE, 1))
                    .register(consumer, id("kachink1"));
            Advancement kachink2 = Advancement.Builder.builder()
                    .withParent(kachink1)
                    .withDisplay(CraftingItems.DIAMOND_SHARD, title("kachink2"), description("kachink2"), null, FrameType.TASK, true, true, false)
                    .withCriterion("kachink", genericInt(DurabilityTrait.TRIGGER_BRITTLE, 1))
                    .register(consumer, id("kachink2"));

            Advancement dagger = simpleGetItem(consumer, ModItems.DAGGER, root);

            Advancement templateBoard = simpleGetItem(consumer, CraftingItems.TEMPLATE_BOARD, dagger);
            Advancement crudeTool = Advancement.Builder.builder()
                    .withParent(dagger)
                    .withDisplay(CraftingItems.ROUGH_ROD, title("crude_tool"), description("crude_tool"), null, FrameType.TASK, true, true, false)
                    .withCriterion("tool_has_rough_rod", genericInt(GearEvents.CRAFTED_WITH_ROUGH_ROD, 1))
                    .register(consumer, id("crude_tool"));

            Advancement blueprintPaper = simpleGetItem(consumer, CraftingItems.BLUEPRINT_PAPER, templateBoard);
            Advancement upgradeBase = simpleGetItem(consumer, CraftingItems.UPGRADE_BASE, templateBoard);
            Advancement repairKit = Advancement.Builder.builder()
                    .withParent(templateBoard)
                    .withDisplay(ModItems.CRUDE_REPAIR_KIT, title("repair_kit"), description("repair_kit"), null, FrameType.TASK, true, true, false)
                    .withCriterion("crude", getItem(ModItems.CRUDE_REPAIR_KIT))
                    .withCriterion("sturdy", getItem(ModItems.STURDY_REPAIR_KIT))
                    .withCriterion("crimson", getItem(ModItems.CRIMSON_REPAIR_KIT))
                    .withRequirementsStrategy(IRequirementsStrategy.OR)
                    .register(consumer, id("repair_kit"));

            Advancement repairFromBroken = Advancement.Builder.builder()
                    .withParent(repairKit)
                    .withDisplay(Items.FLINT, title("repair_from_broken"), description("repair_from_broken"), null, FrameType.TASK, true, true, false)
                    .withCriterion("repair", genericInt(GearEvents.REPAIR_FROM_BROKEN, 1))
                    .register(consumer, id("repair_from_broken"));

            Advancement tipUpgrade = simpleGetItem(consumer, ModItems.TIP, upgradeBase, "tip_upgrade");

            //region Gear

            Advancement mixedMaterials = Advancement.Builder.builder()
                    .withParent(blueprintPaper)
                    .withDisplay(Items.EMERALD, title("mixed_materials"), description("mixed_materials"), null, FrameType.TASK, true, true, false)
                    .withCriterion("mixed_materials", genericInt(GearEvents.UNIQUE_MAIN_PARTS, 2))
                    .register(consumer, id("mixed_materials"));

            Advancement armor = Advancement.Builder.builder()
                    .withParent(blueprintPaper)
                    .withDisplay(ModItems.HELMET, title("armor"), description("armor"), null, FrameType.TASK, true, true, false)
                    .withCriterion("helmet", getItem(ModItems.HELMET))
                    .withCriterion("chestplate", getItem(ModItems.CHESTPLATE))
                    .withCriterion("leggings", getItem(ModItems.LEGGINGS))
                    .withCriterion("boots", getItem(ModItems.BOOTS))
                    .withRequirementsStrategy(IRequirementsStrategy.OR)
                    .register(consumer, id("armor"));

            Advancement bow = Advancement.Builder.builder()
                    .withParent(blueprintPaper)
                    .withDisplay(ModItems.BOW, title("bow"), description("bow"), null, FrameType.TASK, true, true, false)
                    .withCriterion("get_item", getItem(ModItems.BOW))
                    .register(consumer, id("bow"));
            Advancement standardTools = Advancement.Builder.builder()
                    .withParent(blueprintPaper)
                    .withDisplay(ModItems.PICKAXE, title("standard_tools"), description("standard_tools"), null, FrameType.TASK, true, true, false)
                    .withCriterion("pickaxe", getItem(ModItems.PICKAXE))
                    .withCriterion("shovel", getItem(ModItems.SHOVEL))
                    .withCriterion("axe", getItem(ModItems.AXE))
                    .withRequirementsStrategy(IRequirementsStrategy.AND)
                    .register(consumer, id("standard_tools"));
            Advancement swords = Advancement.Builder.builder()
                    .withParent(blueprintPaper)
                    .withDisplay(ModItems.SWORD, title("swords"), description("swords"), null, FrameType.TASK, true, true, false)
                    .withCriterion("sword", getItem(ModItems.SWORD))
                    .withCriterion("katana", getItem(ModItems.KATANA))
                    .withCriterion("machete", getItem(ModItems.MACHETE))
                    .withRequirementsStrategy(IRequirementsStrategy.AND)
                    .register(consumer, id("swords"));

            Advancement bigJobTools = Advancement.Builder.builder()
                    .withParent(standardTools)
                    .withDisplay(ModItems.HAMMER, title("big_job_tools"), description("big_job_tools"), null, FrameType.TASK, true, true, false)
                    .withCriterion("hammer", getItem(ModItems.HAMMER))
                    .withCriterion("excavator", getItem(ModItems.EXCAVATOR))
                    .withCriterion("lumber_axe", getItem(ModItems.LUMBER_AXE))
                    .withRequirementsStrategy(IRequirementsStrategy.AND)
                    .register(consumer, id("big_job_tools"));

            Advancement crossbow = simpleGetItem(consumer, ModItems.CROSSBOW, bow);

            Advancement mattock = simpleGetItem(consumer, ModItems.MATTOCK, standardTools);

            Advancement sickle = simpleGetItem(consumer, ModItems.SICKLE, mattock);

            //endregion

            //region Nether

            Advancement nether = Advancement.Builder.builder()
                    .withParent(root)
                    .withDisplay(Items.OBSIDIAN, title("nether"), description("nether"), null, FrameType.TASK, false, false, false)
                    .withCriterion("entered_nether", ChangeDimensionTrigger.Instance.changedDimensionTo(DimensionType.THE_NETHER))
                    .register(consumer, id("nether"));

            Advancement netherPlants = Advancement.Builder.builder()
                    .withParent(nether)
                    .withDisplay(ModItems.NETHER_BANANA, title("nether_plants"), description("nether_plants"), null, FrameType.TASK, true, true, false)
                    .withCriterion("banana", getItem(ModItems.NETHER_BANANA))
                    .withCriterion("sapling", getItem(ModBlocks.NETHERWOOD_SAPLING))
                    .withRequirementsStrategy(IRequirementsStrategy.AND)
                    .register(consumer, id("nether_plants"));

            Advancement blazeGold = simpleGetItem(consumer, CraftingItems.BLAZE_GOLD_INGOT, nether, "blaze_gold");
            Advancement crimsonIron = Advancement.Builder.builder()
                    .withParent(nether)
                    .withDisplay(CraftingItems.CRIMSON_IRON_INGOT, title("crimson_iron"), description("crimson_iron"), null, FrameType.TASK, true, true, false)
                    .withCriterion("get_ore", getItem(ModBlocks.CRIMSON_IRON_ORE))
                    .withCriterion("get_ingot", getItem(CraftingItems.CRIMSON_IRON_INGOT))
                    .register(consumer, id("crimson_iron"));

            Advancement materialGrader = Advancement.Builder.builder()
                    .withParent(blazeGold)
                    .withDisplay(ModBlocks.MATERIAL_GRADER, title("material_grader"), description("material_grader"), null, FrameType.TASK, true, true, false)
                    .withCriterion("get_grader", getItem(ModBlocks.MATERIAL_GRADER))
                    .withCriterion("get_catalyst", getItem(ModTags.Items.GRADER_CATALYSTS_TIER_1))
                    .register(consumer, id("material_grader"));

            Advancement crimsonSteel = simpleGetItem(consumer, CraftingItems.CRIMSON_STEEL_INGOT, crimsonIron, "crimson_steel");
            Advancement salvager = simpleGetItem(consumer, ModBlocks.SALVAGER, crimsonIron);

            Advancement highDurability = Advancement.Builder.builder()
                    .withParent(materialGrader)
                    .withDisplay(CraftingItems.EMERALD_TIPPED_UPGRADE, title("high_durability"), description("high_durability"), null, FrameType.TASK, true, true, false)
                    .withCriterion("durability", genericInt(GearEvents.MAX_DURABILITY, 16_000))
                    .register(consumer, id("high_durability"));
            Advancement graderCatalyst2 = Advancement.Builder.builder()
                    .withParent(materialGrader)
                    .withDisplay(CraftingItems.BLAZING_DUST, title("grader_catalyst_2"), description("grader_catalyst_2"), null, FrameType.TASK, true, true, false)
                    .withCriterion("get_item", getItem(ModTags.Items.GRADER_CATALYSTS_TIER_2))
                    .register(consumer, id("grader_catalyst_2"));

            Advancement graderCatalyst3 = Advancement.Builder.builder()
                    .withParent(graderCatalyst2)
                    .withDisplay(CraftingItems.GLITTERY_DUST, title("grader_catalyst_3"), description("grader_catalyst_3"), null, FrameType.TASK, true, true, false)
                    .withCriterion("get_item", getItem(ModTags.Items.GRADER_CATALYSTS_TIER_3))
                    .register(consumer, id("grader_catalyst_3"));

            //endregion
        }

        private static Advancement simpleGetItem(Consumer<Advancement> consumer, IItemProvider item, Advancement parent) {
            return simpleGetItem(consumer, item, parent, NameUtils.fromItem(item).getPath());
        }

        private static Advancement simpleGetItem(Consumer<Advancement> consumer, IItemProvider item, Advancement parent, String key) {
            return Advancement.Builder.builder()
                    .withParent(parent)
                    .withDisplay(item, title(key), description(key), null, FrameType.TASK, true, true, false)
                    .withCriterion("get_item", getItem(item))
                    .register(consumer, id(key));
        }

        private static String id(String path) {
            return SilentGear.getId(path).toString();
        }

        private static ICriterionInstance getItem(IItemProvider... items) {
            return InventoryChangeTrigger.Instance.forItems(items);
        }

        private static ICriterionInstance getItem(Tag<Item> tag) {
            return InventoryChangeTrigger.Instance.forItems(new ItemPredicate(tag, null, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, EnchantmentPredicate.field_226534_b_, EnchantmentPredicate.field_226534_b_, null, NBTPredicate.ANY));
        }

        private static ICriterionInstance genericInt(ResourceLocation id, int value) {
            return GenericIntTrigger.Instance.instance(id, value);
        }

        private static ITextComponent title(String key) {
            return new TranslationTextComponent("advancements.silentgear." + key + ".title");
        }

        private static ITextComponent description(String key) {
            return new TranslationTextComponent("advancements.silentgear." + key + ".description");
        }
    }
}
