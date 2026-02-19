package net.silentchaos512.gear.data.client;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.client.setup.SgItemTintSources;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.GearItemSet;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.util.Objects;
import java.util.function.BiConsumer;

public class ModItemModelProvider extends ItemModelGenerators {
    public ModItemModelProvider(ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
        super(itemModelOutput, modelOutput);
    }

    @Override
    public void run() {
        for (CraftingItems item : CraftingItems.values()) {
            flatItem(item);
        }

        flatItem(SgItems.NETHERWOOD_CHARCOAL);

        // Crafted materials
        generateWithTintedBaseLayer(SgItems.SHEET_METAL, "_highlight", SgItemTintSources.blendedMaterialColor());

        // Compound materials
        generateWithTintedBaseLayer(SgItems.ALLOY_INGOT, "_highlight", SgItemTintSources.blendedMaterialColor());
        generateWithTintedBaseLayer(SgItems.HYBRID_GEM, "_highlight", SgItemTintSources.blendedMaterialColor());
        generateTintedSingleLayer(SgItems.MIXED_FABRIC, SgItemTintSources.blendedMaterialColor());
        generateWithTintedBaseLayer(SgItems.CRUDE_ALLOY, "_highlight", SgItemTintSources.blendedMaterialColor());
        generateWithTintedBaseLayer(SgItems.SUPER_ALLOY, "_highlight", SgItemTintSources.blendedMaterialColor());

        // Custom materials
        generateWithTintedBaseLayer(SgItems.CUSTOM_INGOT, "item/alloy_ingot", "_highlight", SgItemTintSources.blendedMaterialColor());
        generateWithTintedBaseLayer(SgItems.CUSTOM_GEM, "item/hybrid_gem", "_highlight", SgItemTintSources.blendedMaterialColor());


        // Blueprints and templates and related
        layeredItem(SgItems.BLUEPRINT_BOOK, "item/blueprint_book_cover", "item/blueprint_book_pages", "item/blueprint_book_deco");
        flatItem(SgItems.JEWELER_TOOLS);
        SgItems.getItems(PartBlueprintItem.class).forEach(item -> {
            if (item.hasStandardModel()) {
                var partTypeKey = SgRegistries.PART_TYPE.getKey(item.getPartType());
                layeredItem(
                        item,
                        "item/" + (item.isSingleUse() ? "template" : "blueprint"),
                        "item/blueprint_" + Objects.requireNonNull(partTypeKey).getPath()
                );
            }
        });
        SgItems.getItems(GearBlueprintItem.class).forEach(item -> {
            var gearTypeKey = SgRegistries.GEAR_TYPE.getKey(item.gearType());
            layeredItem(
                    item,
                    "item/" + (item.isSingleUse() ? "template" : "blueprint"),
                    "item/blueprint_" + Objects.requireNonNull(gearTypeKey).getPath()
            );
        });

        flatItem(SgItems.MOD_KIT);

        // Repair kits
        flatItem(SgItems.VERY_CRUDE_REPAIR_KIT);
        flatItem(SgItems.CRUDE_REPAIR_KIT);
        flatItem(SgItems.STURDY_REPAIR_KIT);
        flatItem(SgItems.CRIMSON_REPAIR_KIT);
        flatItem(SgItems.AZURE_REPAIR_KIT);

        // Crude tools
        flatItem(SgItems.CRUDE_KNIFE);
        flatItem(SgItems.CRUDE_HAMMER);

        // Smithing templates
        flatItem(SgItems.COATING_SMITHING_TEMPLATE);

        // Misc
        flatItem(SgItems.MATERIAL_BOOK);
        flatItem(SgItems.BLUEPRINT_PACKAGE);
//        flatItem(SgItems.FLAX_SEEDS);
//        flatItem(SgItems.FLUFFY_SEEDS);
        flatItem(SgItems.GOLDEN_NETHER_BANANA);
        flatItem(SgItems.NETHER_BANANA);
        flatItem(SgItems.PEBBLE);

        // Gear
        tempGearStandardTool(GearItemSets.SWORD);
        tempGearStandardTool(GearItemSets.KATANA);
        tempGearStandardTool(GearItemSets.MACHETE);
        // Trident model created manually
        tempGearStandardTool(GearItemSets.MACE);
        tempGearStandardTool(GearItemSets.KNIFE);
        tempGearStandardTool(GearItemSets.DAGGER);
        tempGearStandardTool(GearItemSets.PICKAXE);
        tempGearStandardTool(GearItemSets.SHOVEL);
        tempGearStandardTool(GearItemSets.AXE);
        tempGearStandardTool(GearItemSets.PAXEL);
        tempGearStandardTool(GearItemSets.HAMMER);
        tempGearStandardTool(GearItemSets.EXCAVATOR);
        gearSawItem(GearItemSets.SAW);
        tempGearStandardTool(GearItemSets.PROSPECTOR_HAMMER);
        tempGearStandardTool(GearItemSets.HOE);
        tempGearStandardTool(GearItemSets.MATTOCK);
        tempGearStandardTool(GearItemSets.SICKLE);
        tempGearStandardTool(GearItemSets.SHEARS);
        gearFishingRodItem(GearItemSets.FISHING_ROD);
        // Bow, crossbow, and slingshot are manually created right now
        gearArrowItem(GearItemSets.ARROW);
        gearArmorItem(GearItemSets.HELMET);
        gearArmorItem(GearItemSets.CHESTPLATE);
        gearArmorItem(GearItemSets.LEGGINGS);
        gearArmorItem(GearItemSets.BOOTS);
        gearElytraItem(GearItemSets.ELYTRA);
        gearCurioItem(GearItemSets.RING);
        gearCurioItem(GearItemSets.BRACELET);
        gearCurioItem(GearItemSets.NECKLACE);
        // Parts
        mainPartItem(GearItemSets.SWORD);
        mainPartItem(GearItemSets.KATANA);
        mainPartItem(GearItemSets.MACHETE);
        mainPartItemNewTextureNames(GearItemSets.SPEAR);
        mainPartItem(GearItemSets.TRIDENT);
        mainPartItem(GearItemSets.MACE);
        mainPartItem(GearItemSets.KNIFE);
        mainPartItem(GearItemSets.DAGGER);
        mainPartItem(GearItemSets.PICKAXE);
        mainPartItem(GearItemSets.SHOVEL);
        mainPartItem(GearItemSets.AXE);
        mainPartItem(GearItemSets.PAXEL);
        mainPartItem(GearItemSets.HAMMER);
        mainPartItem(GearItemSets.EXCAVATOR);
        mainPartItem(GearItemSets.SAW);
        mainPartItem(GearItemSets.HOE);
        mainPartItem(GearItemSets.MATTOCK);
        mainPartItem(GearItemSets.PROSPECTOR_HAMMER);
        mainPartItem(GearItemSets.SICKLE);
        mainPartItem(GearItemSets.SHEARS);
        mainPartItem(GearItemSets.FISHING_ROD);
        mainPartItem(GearItemSets.BOW);
        mainPartItem(GearItemSets.CROSSBOW);
        mainPartItem(GearItemSets.SLINGSHOT);
        mainPartItem(GearItemSets.SHIELD);
        mainPartItem(GearItemSets.HELMET);
        mainPartItem(GearItemSets.CHESTPLATE);
        mainPartItem(GearItemSets.LEGGINGS);
        mainPartItem(GearItemSets.BOOTS);
        mainPartItem(GearItemSets.ELYTRA);
        mainPartItem(GearItemSets.ARROW);
        mainPartItem(GearItemSets.RING);
        mainPartItem(GearItemSets.BRACELET);
        mainPartItem(GearItemSets.NECKLACE);
        gearPartItem(SgItems.ROD);
        tipUpgradePartItem(SgItems.TIP);
        coatingPartItem(SgItems.COATING);
        gearPartItem(SgItems.GRIP);
        gearPartItem(SgItems.BINDING);
        gearPartItem(SgItems.LINING, "item/part/lining_cloth");
        gearPartItem(SgItems.CORD);
        gearPartItem(SgItems.FLETCHING);
        gearPartItem(SgItems.SETTING);
    }

    private void flatItem(ItemLike item) {
        generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
    }

    private void generateWithTintedBaseLayer(ItemLike item, String overlaySuffix, ItemTintSource tintSource) {
        Identifier key = this.generateLayeredItem(
                item.asItem(),
                TextureMapping.getItemTexture(item.asItem()),
                TextureMapping.getItemTexture(item.asItem(), overlaySuffix)
        );
        this.itemModelOutput.accept(item.asItem(), ItemModelUtils.tintedModel(key, tintSource, BLANK_LAYER));
    }

    private void generateWithTintedBaseLayer(ItemLike item, String texturePath, String overlaySuffix, ItemTintSource tintSource) {
        Identifier key = this.generateLayeredItem(
                item.asItem(),
                SilentGear.getId(texturePath),
                SilentGear.getId(texturePath + overlaySuffix)
        );
        this.itemModelOutput.accept(item.asItem(), ItemModelUtils.tintedModel(key, tintSource, BLANK_LAYER));
    }

    private void generateTintedSingleLayer(ItemLike item, ItemTintSource tintSource) {
        Identifier key = createFlatItemModel(item.asItem(), ModelTemplates.FLAT_ITEM);
        this.itemModelOutput.accept(item.asItem(), ItemModelUtils.tintedModel(key, tintSource));
    }

    private void layeredItem(ItemLike item, String texture0, String texture1) {
        var model = generateLayeredItem(item.asItem(), SilentGear.getId(texture0), SilentGear.getId(texture1));
        this.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
    }

    private void layeredItem(ItemLike item, String texture0, String texture1, String texture2) {
        var model = ModelTemplates.THREE_LAYERED_ITEM.create(
                item.asItem(),
                TextureMapping.layered(SilentGear.getId(texture0), SilentGear.getId(texture1), SilentGear.getId(texture2)),
                this.modelOutput
        );
        this.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
    }

    private void generateMaterialColoredItemWithOverlay(ItemLike item, String texturePath, String overlayTexture) {
        Identifier key = this.generateLayeredItem(
                item.asItem(),
                SilentGear.getId(texturePath),
                SilentGear.getId(overlayTexture)
        );
        this.itemModelOutput.accept(item.asItem(), ItemModelUtils.tintedModel(key, SgItemTintSources.blendedMaterialColor(), BLANK_LAYER));
    }

    private void generateMaterialColoredItemWithOverlay(ItemLike item, String texturePath, String overlayTexture1, String overlayTexture2) {
        Identifier key = ModelTemplates.THREE_LAYERED_ITEM.create(
                item.asItem(),
                TextureMapping.layered(
                        SilentGear.getId(texturePath),
                        SilentGear.getId(overlayTexture1),
                        SilentGear.getId(overlayTexture2)
                ),
                this.modelOutput
        );
        this.itemModelOutput.accept(item.asItem(), ItemModelUtils.tintedModel(key, SgItemTintSources.blendedMaterialColor(), BLANK_LAYER));
    }

    private String gearTypeName(GearType gearType) {
        return Objects.requireNonNull(SgRegistries.GEAR_TYPE.getKey(gearType)).getPath();
    }

    private String itemNamePath(GearItemSet<?> itemSet) {
        return BuiltInRegistries.ITEM.getKey(itemSet.gearItem()).getPath();
    }

    private String itemNamePath(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    private void tempGearStandardTool(GearItemSet<? extends GearItem> item) {
        tempGearStandardTool(item, true);
    }

    private void tempGearStandardTool(GearItemSet<? extends GearItem> itemSet, boolean buildMainModel) {
        GearItemModelBuilder.handheldItem(itemSet)
                .tintedLayer(PartTypes.ROD, "rod_generic_lc")
                .tintedLayer(PartTypes.MAIN, "main_generic_hc")
                .simpleLayer(PartTypes.NONE, "_highlight")
                .generateModel(this.itemModelOutput, this.modelOutput);
    }

    private void gearBowItem(GearItemSet<? extends GearItem> itemSet) {
        GearItemModelBuilder.handheldItem(itemSet)
                .tintedLayer(PartTypes.ROD, "rod_generic_lc")
                .tintedLayer(PartTypes.MAIN, "main_generic_hc")
                .simpleLayer(PartTypes.NONE, "_highlight")
                .tintedLayer(PartTypes.CORD, "bowstring_string")
                .generateModel(this.itemModelOutput, this.modelOutput);
    }

    private void gearFishingRodItem(GearItemSet<? extends GearItem> itemSet) {
        GearItemModelBuilder.handheldRodItem(itemSet)
                .tintedLayer(PartTypes.ROD, "rod_generic_lc")
                .tintedLayer(PartTypes.MAIN, "main_generic_hc")
                .simpleLayer(PartTypes.NONE, "_highlight")
                .tintedLayer(PartTypes.CORD, "bowstring_string")
                .generateModel(this.itemModelOutput, this.modelOutput);
    }

    private void gearSawItem(GearItemSet<? extends GearItem> itemSet) {
        GearItemModelBuilder.unique(itemSet, layers -> ExtraModelTemplates.THREE_LAYER_SAW_BASE)
                .tintedLayer(PartTypes.ROD, "rod_generic_lc")
                .tintedLayer(PartTypes.MAIN, "main_generic_hc")
                .simpleLayer(PartTypes.NONE, "_highlight")
                .generateModel(this.itemModelOutput, this.modelOutput);
    }

    private void gearCurioItem(GearItemSet<? extends GearItem> itemSet) {
        GearItemModelBuilder.handheldItem(itemSet)
                .tintedLayer(PartTypes.MAIN, "main_generic_hc")
                .tintedLayer(PartTypes.SETTING, "adornment_generic")
                .simpleLayer(PartTypes.NONE, "_highlight")
                .generateModel(this.itemModelOutput, this.modelOutput);
    }

    private void gearArmorItem(GearItemSet<? extends GearItem> itemSet) {
        GearItemModelBuilder.handheldItem(itemSet)
                .tintedLayer(PartTypes.MAIN, "main_generic_hc")
                .simpleLayer(PartTypes.NONE, "_highlight")
                .generateModel(this.itemModelOutput, this.modelOutput);
    }

    private void gearElytraItem(GearItemSet<? extends GearItem> itemSet) {
        GearItemModelBuilder.handheldItem(itemSet)
                .tintedLayer(PartTypes.MAIN, "main_generic_hc")
                .simpleLayer(PartTypes.NONE, "_highlight")
                .tintedLayer(PartTypes.BINDING, "binding_generic")
                .generateModel(this.itemModelOutput, this.modelOutput);
    }

    private void gearArrowItem(GearItemSet<? extends GearItem> itemSet) {
        GearItemModelBuilder.handheldItem(itemSet)
                .tintedLayer(PartTypes.ROD, "rod_generic_lc")
                .tintedLayer(PartTypes.MAIN, "main_generic_hc")
                .simpleLayer(PartTypes.NONE, "_highlight")
                .tintedLayer(PartTypes.FLETCHING, "fletching_generic")
                .generateModel(this.itemModelOutput, this.modelOutput);
    }

    private void mainPartItem(GearItemSet<? extends GearItem> item) {
        String name = gearTypeName(item.type());
        generateMaterialColoredItemWithOverlay(
                item.mainPart(),
                "item/" + name + "/main_generic_hc",
                "item/" + name + "/_highlight",
                "item/part_marker"
        );
    }

    private void mainPartItemNewTextureNames(GearItemSet<? extends GearItem> item) {
        String name = gearTypeName(item.type());
        generateMaterialColoredItemWithOverlay(
                item.mainPart(),
                "item/" + name + "/main_hc",
                "item/" + name + "/icon_highlight",
                "item/part_marker"
        );
    }

    private void gearPartItem(DeferredItem<CompoundPartItem> item) {
        String name = Objects.requireNonNull(SgRegistries.PART_TYPE.getKey(item.get().getPartType())).getPath();
        gearPartItem(item, "item/part/" + name);
    }

    private void gearPartItem(DeferredItem<CompoundPartItem> item, String texture) {
        generateMaterialColoredItemWithOverlay(item, texture, "item/part_marker");
    }

    private void tipUpgradePartItem(DeferredItem<CompoundPartItem> item) {
        generateMaterialColoredItemWithOverlay(
                item,
                "item/part/tip",
                "item/part/tip_shine",
                "item/part_marker"
        );
    }

    private void coatingPartItem(DeferredItem<CompoundPartItem> item) {
        generateMaterialColoredItemWithOverlay(
                item,
                "item/part/coating_material",
                "item/part/coating_jar",
                "item/part_marker"
        );
    }

    static class ExtraSlots {
        static final TextureSlot LAYER3 = TextureSlot.create("layer3");
        static final TextureSlot LAYER4 = TextureSlot.create("layer4");
        static final TextureSlot LAYER5 = TextureSlot.create("layer5");
    }

    static class ExtraModelTemplates {
        static final ModelTemplate FOUR_LAYERED_ITEM = ModelTemplates.createItem("generated", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3);
        static final ModelTemplate FIVE_LAYERED_ITEM = ModelTemplates.createItem("generated", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3, ExtraSlots.LAYER4);
        static final ModelTemplate SIX_LAYERED_ITEM = ModelTemplates.createItem("generated", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3, ExtraSlots.LAYER4, ExtraSlots.LAYER5);

        static final ModelTemplate TWO_LAYERED_HANDHELD_ITEM = ModelTemplates.createItem("handheld", TextureSlot.LAYER0, TextureSlot.LAYER1);
        static final ModelTemplate THREE_LAYERED_HANDHELD_ITEM = ModelTemplates.createItem("handheld", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2);
        static final ModelTemplate FOUR_LAYERED_HANDHELD_ITEM = ModelTemplates.createItem("handheld", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3);
        static final ModelTemplate FIVE_LAYERED_HANDHELD_ITEM = ModelTemplates.createItem("handheld", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3, ExtraSlots.LAYER4);
        static final ModelTemplate SIX_LAYERED_HANDHELD_ITEM = ModelTemplates.createItem("handheld", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3, ExtraSlots.LAYER4, ExtraSlots.LAYER5);

        static final ModelTemplate TWO_LAYERED_HANDHELD_ROD_ITEM = ModelTemplates.createItem("handheld_rod", TextureSlot.LAYER0, TextureSlot.LAYER1);
        static final ModelTemplate THREE_LAYERED_HANDHELD_ROD_ITEM = ModelTemplates.createItem("handheld_rod", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2);
        static final ModelTemplate FOUR_LAYERED_HANDHELD_ROD_ITEM = ModelTemplates.createItem("handheld_rod", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3);
        static final ModelTemplate FIVE_LAYERED_HANDHELD_ROD_ITEM = ModelTemplates.createItem("handheld_rod", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3, ExtraSlots.LAYER4);
        static final ModelTemplate SIX_LAYERED_HANDHELD_ROD_ITEM = ModelTemplates.createItem("handheld_rod", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, ExtraSlots.LAYER3, ExtraSlots.LAYER4, ExtraSlots.LAYER5);

        static final ModelTemplate THREE_LAYER_SAW_BASE = ModelTemplates.createItem("silentgear:saw_base", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2);
    }
}
