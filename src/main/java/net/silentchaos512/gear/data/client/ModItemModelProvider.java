package net.silentchaos512.gear.data.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.GearItemSet;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), SilentGear.MOD_ID, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Silent Gear - Item Models";
    }

    @Override
    protected void registerModels() {
        // Blocks
        SgBlocks.BLOCKS.getEntries().stream()
                .map(DeferredHolder::get)
                .forEach(this::blockItemModel);

        ModelFile itemGenerated = getExistingFile(ResourceLocation.withDefaultNamespace("item/generated"));
        ModelFile itemHandheld = getExistingFile(ResourceLocation.withDefaultNamespace("item/handheld"));

        for (CraftingItems item : CraftingItems.values()) {
            builder(item, itemGenerated, "item/" + item.getName());
        }

        builder(SgItems.NETHERWOOD_CHARCOAL, itemGenerated);

        // Crafted materials
        builder(SgItems.SHEET_METAL)
                .parent(itemGenerated)
                .texture("layer0", "item/sheet_metal")
                .texture("layer1", "item/sheet_metal_highlight");

        // Compound materials
        builder(SgItems.ALLOY_INGOT)
                .parent(itemGenerated)
                .texture("layer0", "item/alloy_ingot")
                .texture("layer1", "item/alloy_ingot_highlight");
        builder(SgItems.HYBRID_GEM)
                .parent(itemGenerated)
                .texture("layer0", "item/hybrid_gem")
                .texture("layer1", "item/hybrid_gem_highlight");
        builder(SgItems.MIXED_FABRIC, itemGenerated, "item/mixed_fabric");
        builder(SgItems.SUPER_ALLOY)
                .parent(itemGenerated)
                .texture("layer0", "item/super_alloy")
                .texture("layer1", "item/super_alloy_highlight");

        // Custom materials
        builder(SgItems.CUSTOM_INGOT)
                .parent(itemGenerated)
                .texture("layer0", "item/alloy_ingot")
                .texture("layer1", "item/alloy_ingot_highlight");
        builder(SgItems.CUSTOM_GEM)
                .parent(itemGenerated)
                .texture("layer0", "item/hybrid_gem")
                .texture("layer1", "item/hybrid_gem_highlight");

        builder(SgItems.BLUEPRINT_BOOK)
                .parent(itemGenerated)
                .texture("layer0", "item/blueprint_book_cover")
                .texture("layer1", "item/blueprint_book_pages")
                .texture("layer2", "item/blueprint_book_deco");

        builder(SgItems.JEWELER_TOOLS, itemGenerated, "item/jeweler_tools");

        // Blueprints and templates
        SgItems.getItems(PartBlueprintItem.class).forEach(item -> {
            if (item.hasStandardModel()) {
                var key = SgRegistries.PART_TYPE.getKey(item.getPartType());
                builder(item)
                        .parent(itemGenerated)
                        .texture("layer0", "item/" + (item.isSingleUse() ? "template" : "blueprint"))
                        .texture("layer1", "item/blueprint_" + Objects.requireNonNull(key).getPath());
            }
        });
        SgItems.getItems(GearBlueprintItem.class).forEach(item -> {
            var key = SgRegistries.GEAR_TYPE.getKey(item.gearType());
            builder(item)
                    .parent(itemGenerated)
                    .texture("layer0", "item/" + (item.isSingleUse() ? "template" : "blueprint"))
                    .texture("layer1", "item/blueprint_" + Objects.requireNonNull(key).getPath());
        });

        builder(SgItems.MOD_KIT, itemGenerated);

        // Repair kits
        builder(SgItems.VERY_CRUDE_REPAIR_KIT, itemGenerated);
        builder(SgItems.CRUDE_REPAIR_KIT, itemGenerated);
        builder(SgItems.STURDY_REPAIR_KIT, itemGenerated);
        builder(SgItems.CRIMSON_REPAIR_KIT, itemGenerated);
        builder(SgItems.AZURE_REPAIR_KIT, itemGenerated);

        // Smithing templates
        builder(SgItems.COATING_SMITHING_TEMPLATE, itemGenerated);

        // Misc
        builder(SgItems.GUIDE_BOOK, itemGenerated);
        builder(SgItems.BLUEPRINT_PACKAGE, itemGenerated);
        builder(SgItems.FLAX_SEEDS, itemGenerated);
        builder(SgItems.FLUFFY_SEEDS, itemGenerated);
        builder(SgItems.GOLDEN_NETHER_BANANA, itemGenerated);
        builder(SgItems.NETHER_BANANA, itemGenerated);
        builder(SgItems.PEBBLE, itemGenerated);

        // Temp models
        // Gear
        tempGearStandardTool(GearItemSets.SWORD, itemHandheld);
        tempGearStandardTool(GearItemSets.KATANA, itemHandheld);
        tempGearStandardTool(GearItemSets.MACHETE, itemHandheld);
        tempGearStandardTool(GearItemSets.SPEAR, itemHandheld);
        tempGearStandardTool(GearItemSets.TRIDENT, itemHandheld);
        tempGearStandardTool(GearItemSets.MACE, itemHandheld);
        tempGearStandardTool(GearItemSets.KNIFE, itemHandheld);
        tempGearStandardTool(GearItemSets.DAGGER, itemHandheld);
        tempGearStandardTool(GearItemSets.PICKAXE, itemHandheld);
        tempGearStandardTool(GearItemSets.SHOVEL, itemHandheld);
        tempGearStandardTool(GearItemSets.AXE, itemHandheld);
        tempGearStandardTool(GearItemSets.PAXEL, itemHandheld);
        tempGearStandardTool(GearItemSets.HAMMER, itemHandheld);
        tempGearStandardTool(GearItemSets.EXCAVATOR, itemHandheld);
        tempGearStandardTool(GearItemSets.SAW, getExistingFile(modLoc("item/saw_base")));
        tempGearStandardTool(GearItemSets.PROSPECTOR_HAMMER, itemHandheld);
        tempGearStandardTool(GearItemSets.HOE, itemHandheld);
        tempGearStandardTool(GearItemSets.MATTOCK, itemHandheld);
        tempGearStandardTool(GearItemSets.SICKLE, itemHandheld);
        tempGearStandardTool(GearItemSets.SHEARS, itemHandheld);
        tempGearBow(GearItemSets.FISHING_ROD, itemHandheld);
        // tempGearBow(SgItems.BOW, itemHandheld);
        // tempGearBow(SgItems.CROSSBOW, itemHandheld); // manual override in resources
        // tempGearBow(SgItems.SLINGSHOT, itemHandheld);
        tempGearArrow(GearItemSets.ARROW, itemGenerated);
        tempGearArmor(GearItemSets.HELMET, itemGenerated);
        tempGearArmor(GearItemSets.CHESTPLATE, itemGenerated);
        tempGearArmor(GearItemSets.LEGGINGS, itemGenerated);
        tempGearArmor(GearItemSets.BOOTS, itemGenerated);
        tempGearElytra(GearItemSets.ELYTRA, itemGenerated);
        tempGearCurio(GearItemSets.RING, itemGenerated);
        tempGearCurio(GearItemSets.BRACELET, itemGenerated);
        tempGearCurio(GearItemSets.NECKLACE, itemGenerated);
        // Parts
        tempMainPart(GearItemSets.SWORD);
        tempMainPart(GearItemSets.KATANA);
        tempMainPart(GearItemSets.MACHETE);
        tempMainPart(GearItemSets.SPEAR);
        tempMainPart(GearItemSets.TRIDENT);
        tempMainPart(GearItemSets.MACE);
        tempMainPart(GearItemSets.KNIFE);
        tempMainPart(GearItemSets.DAGGER);
        tempMainPart(GearItemSets.PICKAXE);
        tempMainPart(GearItemSets.SHOVEL);
        tempMainPart(GearItemSets.AXE);
        tempMainPart(GearItemSets.PAXEL);
        tempMainPart(GearItemSets.HAMMER);
        tempMainPart(GearItemSets.EXCAVATOR);
        tempMainPart(GearItemSets.SAW);
        tempMainPart(GearItemSets.HOE);
        tempMainPart(GearItemSets.MATTOCK);
        tempMainPart(GearItemSets.PROSPECTOR_HAMMER);
        tempMainPart(GearItemSets.SICKLE);
        tempMainPart(GearItemSets.SHEARS);
        tempMainPart(GearItemSets.FISHING_ROD);
        tempMainPart(GearItemSets.BOW);
        tempMainPart(GearItemSets.CROSSBOW);
        tempMainPart(GearItemSets.SLINGSHOT);
        tempMainPart(GearItemSets.SHIELD);
        tempMainPart(GearItemSets.HELMET);
        tempMainPart(GearItemSets.CHESTPLATE);
        tempMainPart(GearItemSets.LEGGINGS);
        tempMainPart(GearItemSets.BOOTS);
        tempMainPart(GearItemSets.ELYTRA);
        tempMainPart(GearItemSets.ARROW);
        tempMainPart(GearItemSets.RING);
        tempMainPart(GearItemSets.BRACELET);
        tempMainPart(GearItemSets.NECKLACE);
        tempGearPart(SgItems.ROD);
        tempGearPart(SgItems.TIP);
        tempCoatingPart(SgItems.COATING);
        tempGearPart(SgItems.GRIP);
        tempGearPart(SgItems.BINDING);
        tempGearPart(SgItems.LINING, "item/part/lining_cloth");
        tempGearPart(SgItems.CORD);
        tempGearPart(SgItems.FLETCHING);
        tempGearPart(SgItems.SETTING);
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

    private ItemModelBuilder tempGearStandardTool(GearItemSet<? extends GearItem> item, ModelFile parent) {
        String name = gearTypeName(item.type());
        String path = BuiltInRegistries.ITEM.getKey(item.gearItem()).getPath();
        ModelFile mainModelFile = new ModelFile.UncheckedModelFile(modLoc("item/" + path));

        ItemModelBuilder model_lc = getBuilder(path + "_lc")
                .parent(mainModelFile)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_lc");
        ItemModelBuilder model_hc = getBuilder(path + "_hc")
                .parent(mainModelFile)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight");
        ItemModelBuilder model_lc_tip = getBuilder(path + "_lc_tip")
                .parent(mainModelFile)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_lc")
                .texture("layer2", "item/blank")
                .texture("layer3", "item/" + name + "/tip_sharp");
        ItemModelBuilder model_hc_tip = getBuilder(path + "_hc_tip")
                .parent(mainModelFile)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight")
                .texture("layer3", "item/" + name + "/tip_sharp");
        ItemModelBuilder model_lc_grip = getBuilder(path + "_lc_grip")
                .parent(mainModelFile)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_lc")
                .texture("layer2", "item/blank")
                .texture("layer3", "item/blank")
                .texture("layer4", "item/" + name + "/grip_wool");
        ItemModelBuilder model_hc_grip = getBuilder(path + "_hc_grip")
                .parent(mainModelFile)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight")
                .texture("layer3", "item/blank")
                .texture("layer4", "item/" + name + "/grip_wool");
        ItemModelBuilder model_lc_tip_grip = getBuilder(path + "_lc_tip_grip")
                .parent(mainModelFile)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_lc")
                .texture("layer2", "item/blank")
                .texture("layer3", "item/" + name + "/tip_sharp")
                .texture("layer4", "item/" + name + "/grip_wool");
        ItemModelBuilder model_hc_tip_grip = getBuilder(path + "_hc_tip_grip")
                .parent(mainModelFile)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight")
                .texture("layer3", "item/" + name + "/tip_sharp")
                .texture("layer4", "item/" + name + "/grip_wool");

        ItemModelBuilder mainBuilder = getBuilder(path)
                .parent(parent)
                .override().predicate(Const.MODEL, 2).model(model_lc).end()
                .override().predicate(Const.MODEL, 3).model(model_hc).end()
                .override().predicate(Const.MODEL, 4 | 2).model(model_lc_tip).end()
                .override().predicate(Const.MODEL, 4 | 3).model(model_hc_tip).end()
                .override().predicate(Const.MODEL, 8 | 2).model(model_lc_grip).end()
                .override().predicate(Const.MODEL, 8 | 3).model(model_hc_grip).end()
                .override().predicate(Const.MODEL, 8 | 4 | 2).model(model_lc_tip_grip).end()
                .override().predicate(Const.MODEL, 8 | 4 | 3).model(model_hc_tip_grip).end()
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_lc");
        return mainBuilder;
    }

    private ItemModelBuilder tempGear(DeferredItem<? extends GearItem> item, ModelFile parent) {
        String name = gearTypeName(item.get().getGearType());
        return getBuilder(item.getId().getPath())
                .parent(parent)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight");
    }

    private ItemModelBuilder tempGearBow(GearItemSet<? extends GearItem> item, ModelFile parent) {
        String name = gearTypeName(item.type());
        return getBuilder(itemNamePath(item))
                .parent(parent)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight")
                .texture("layer3", "item/" + name + "/bowstring_string");
    }

    private ItemModelBuilder tempGearCurio(GearItemSet<? extends GearItem> item, ModelFile parent) {
        String name = gearTypeName(item.type());
        return getBuilder(itemNamePath(item))
                .parent(parent)
                .texture("layer0", "item/" + name + "/main_generic_hc")
                .texture("layer1", "item/" + name + "/_highlight")
                .texture("layer2", "item/" + name + "/adornment_generic")
                .texture("layer3", "item/" + name + "/adornment_highlight");
    }

    private ItemModelBuilder tempGearArmor(GearItemSet<? extends GearItem> item, ModelFile parent) {
        String name = gearTypeName(item.type());
        return getBuilder(itemNamePath(item))
                .parent(parent)
                .texture("layer0", "item/" + name + "/main_generic_hc")
                .texture("layer1", "item/" + name + "/_highlight");
    }

    private ItemModelBuilder tempGearElytra(GearItemSet<? extends GearItem> item, ModelFile parent) {
        String name = gearTypeName(item.type());
        return getBuilder(itemNamePath(item))
                .parent(parent)
                .texture("layer0", "item/" + name + "/main_generic_hc")
                .texture("layer1", "item/" + name + "/_highlight")
                .texture("layer2", "item/" + name + "/binding_generic");
    }

    private ItemModelBuilder tempGearArrow(GearItemSet<? extends GearItem> item, ModelFile parent) {
        String name = gearTypeName(item.type());
        return getBuilder(itemNamePath(item))
                .parent(parent)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight")
                .texture("layer3", "item/" + name + "/fletching_generic");
    }

    private ItemModelBuilder tempMainPart(GearItemSet<? extends GearItem> item) {
        String name = gearTypeName(item.type());
        return getBuilder(itemNamePath(item.mainPart()))
                .parent(getExistingFile(ResourceLocation.withDefaultNamespace("item/generated")))
                .texture("layer0", "item/" + name + "/main_generic_hc")
                .texture("layer1", "item/" + name + "/_highlight")
                .texture("layer2", "item/part_marker");
    }

    private ItemModelBuilder tempGearPart(DeferredItem<CompoundPartItem> item) {
        String name = Objects.requireNonNull(SgRegistries.PART_TYPE.getKey(item.get().getPartType())).getPath();
        return tempGearPart(item, "item/part/" + name);
    }

    private ItemModelBuilder tempGearPart(DeferredItem<CompoundPartItem> item, String texture) {
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(ResourceLocation.withDefaultNamespace("item/generated")))
                .texture("layer0", texture)
                .texture("layer1", "item/part_marker");
    }

    private ItemModelBuilder tempCoatingPart(DeferredItem<CompoundPartItem> item) {
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(ResourceLocation.withDefaultNamespace("item/generated")))
                .texture("layer0", "item/part/coating_material")
                .texture("layer1", "item/part/coating_jar")
                .texture("layer2", "item/part_marker");
    }

    private void blockItemModel(Block block) {
        if (block == SgBlocks.FLAX_PLANT.get() || block == SgBlocks.FLUFFY_PLANT.get()) {
            return;
        }

        if (block == SgBlocks.PHANTOM_LIGHT.get())
            builder(block, getExistingFile(mcLoc("item/generated")), "item/phantom_light");
        else if (block == SgBlocks.NETHERWOOD_SAPLING.get() || block == SgBlocks.STONE_TORCH.get())
            builder(block, getExistingFile(mcLoc("item/generated")), "block/" + NameUtils.fromBlock(block).getPath());
        else if (block == SgBlocks.NETHERWOOD_FENCE.get())
            withExistingParent("netherwood_fence", modLoc("block/netherwood_fence_inventory"));
        else if (block == SgBlocks.NETHERWOOD_DOOR.get())
            builder(block, getExistingFile(mcLoc("item/generated")), "item/netherwood_door");
        else if (block == SgBlocks.NETHERWOOD_TRAPDOOR.get())
            withExistingParent("netherwood_trapdoor", modLoc("block/netherwood_trapdoor_bottom"));
        else if (block.asItem() != Items.AIR) {
            String name = NameUtils.fromBlock(block).getPath();
            withExistingParent(name, modLoc("block/" + name));
        }
    }

    private ItemModelBuilder builder(ItemLike item) {
        return getBuilder(NameUtils.fromItem(item).getPath());
    }

    private ItemModelBuilder builder(ItemLike item, ModelFile parent) {
        String name = NameUtils.fromItem(item).getPath();
        return builder(item, parent, "item/" + name);
    }

    private ItemModelBuilder builder(ItemLike item, ModelFile parent, String texture) {
        return getBuilder(NameUtils.fromItem(item).getPath()).parent(parent).texture("layer0", texture);
    }
}
