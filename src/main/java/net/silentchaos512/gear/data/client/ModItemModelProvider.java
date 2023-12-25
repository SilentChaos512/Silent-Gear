package net.silentchaos512.gear.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.registry.ItemRegistryObject;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnull;

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
                .map(RegistryObject::get)
                .forEach(this::blockItemModel);

        ModelFile itemGenerated = getExistingFile(new ResourceLocation("item/generated"));
        ModelFile itemHandheld = getExistingFile(new ResourceLocation("item/handheld"));

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

        builder(SgItems.FRAGMENT)
                .parent(itemGenerated)
                .texture("layer0", "item/fragment/metal");

        builder(SgItems.JEWELER_TOOLS, itemGenerated, "item/jeweler_tools");

        // Blueprints and templates
        SgItems.getItems(PartBlueprintItem.class).forEach(item -> {
            if (item.hasStandardModel()) {
                builder(item)
                        .parent(itemGenerated)
                        .texture("layer0", "item/" + (item.isSingleUse() ? "template" : "blueprint"))
                        .texture("layer1", "item/blueprint_" + item.getPartType().getName().getPath());
            }
        });
        SgItems.getItems(GearBlueprintItem.class).forEach(item -> builder(item)
                .parent(itemGenerated)
                .texture("layer0", "item/" + (item.isSingleUse() ? "template" : "blueprint"))
                .texture("layer1", "item/blueprint_" + item.getGearType().getName()));

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
        tempGearStandardTool(SgItems.SWORD, itemHandheld);
        tempGearStandardTool(SgItems.KATANA, itemHandheld);
        tempGearStandardTool(SgItems.MACHETE, itemHandheld);
        tempGearStandardTool(SgItems.SPEAR, itemHandheld);
        tempGearStandardTool(SgItems.TRIDENT, itemHandheld);
        tempGearStandardTool(SgItems.KNIFE, itemHandheld);
        tempGearStandardTool(SgItems.DAGGER, itemHandheld);
        tempGearStandardTool(SgItems.PICKAXE, itemHandheld);
        tempGearStandardTool(SgItems.SHOVEL, itemHandheld);
        tempGearStandardTool(SgItems.AXE, itemHandheld);
        tempGearStandardTool(SgItems.PAXEL, itemHandheld);
        tempGearStandardTool(SgItems.HAMMER, itemHandheld);
        tempGearStandardTool(SgItems.EXCAVATOR, itemHandheld);
        tempGearStandardTool(SgItems.SAW, getExistingFile(modLoc("item/saw_base")));
        tempGearStandardTool(SgItems.PROSPECTOR_HAMMER, itemHandheld);
        tempGearStandardTool(SgItems.HOE, itemHandheld);
        tempGearStandardTool(SgItems.MATTOCK, itemHandheld);
        tempGearStandardTool(SgItems.SICKLE, itemHandheld);
        tempGearStandardTool(SgItems.SHEARS, itemHandheld);
        tempGearBow(SgItems.FISHING_ROD, itemHandheld);
        // tempGearBow(SgItems.BOW, itemHandheld);
        // tempGearBow(SgItems.CROSSBOW, itemHandheld); // manual override in resources
        // tempGearBow(SgItems.SLINGSHOT, itemHandheld);
        tempGearArrow(SgItems.ARROW, itemGenerated);
        tempGearArmor(SgItems.HELMET, itemGenerated);
        tempGearArmor(SgItems.CHESTPLATE, itemGenerated);
        tempGearArmor(SgItems.LEGGINGS, itemGenerated);
        tempGearArmor(SgItems.BOOTS, itemGenerated);
        tempGearElytra(SgItems.ELYTRA, itemGenerated);
        tempGearCurio(SgItems.RING, itemGenerated);
        tempGearCurio(SgItems.BRACELET, itemGenerated);
        // Parts
        tempMainPart(SgItems.SWORD_BLADE);
        tempMainPart(SgItems.KATANA_BLADE);
        tempMainPart(SgItems.MACHETE_BLADE);
        tempMainPart(SgItems.SPEAR_TIP);
        tempMainPart(SgItems.TRIDENT_PRONGS);
        tempMainPart(SgItems.KNIFE_BLADE);
        tempMainPart(SgItems.DAGGER_BLADE);
        tempMainPart(SgItems.PICKAXE_HEAD);
        tempMainPart(SgItems.SHOVEL_HEAD);
        tempMainPart(SgItems.AXE_HEAD);
        tempMainPart(SgItems.PAXEL_HEAD);
        tempMainPart(SgItems.HAMMER_HEAD);
        tempMainPart(SgItems.EXCAVATOR_HEAD);
        tempMainPart(SgItems.SAW_BLADE);
        tempMainPart(SgItems.HOE_HEAD);
        tempMainPart(SgItems.MATTOCK_HEAD);
        tempMainPart(SgItems.PROSPECTOR_HAMMER_HEAD);
        tempMainPart(SgItems.SICKLE_BLADE);
        tempMainPart(SgItems.SHEARS_BLADES);
        tempMainPart(SgItems.FISHING_REEL_AND_HOOK);
        tempMainPart(SgItems.BOW_LIMBS);
        tempMainPart(SgItems.CROSSBOW_LIMBS);
        tempMainPart(SgItems.SLINGSHOT_LIMBS);
        tempMainPart(SgItems.SHIELD_PLATE);
        tempMainPart(SgItems.HELMET_PLATES);
        tempMainPart(SgItems.CHESTPLATE_PLATES);
        tempMainPart(SgItems.LEGGING_PLATES);
        tempMainPart(SgItems.BOOT_PLATES);
        tempMainPart(SgItems.ELYTRA_WINGS);
        tempMainPart(SgItems.ARROW_HEADS);
        tempMainPart(SgItems.RING_SHANK);
        tempMainPart(SgItems.BRACELET_BAND);
        tempGearPart(SgItems.ROD);
        tempGearPart(SgItems.TIP);
        tempCoatingPart(SgItems.COATING);
        tempGearPart(SgItems.GRIP);
        tempGearPart(SgItems.BINDING);
        tempGearPart(SgItems.LINING, "item/part/lining_cloth");
        tempGearPart(SgItems.CORD);
        tempGearPart(SgItems.FLETCHING);
        tempGearPart(SgItems.ADORNMENT);
    }

    private ItemModelBuilder tempGearStandardTool(ItemRegistryObject<? extends ICoreItem> item, ModelFile parent) {
        String name = item.get().getGearType().getName();
        String path = item.getId().getPath();
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

    private ItemModelBuilder tempGear(ItemRegistryObject<? extends ICoreItem> item, ModelFile parent) {
        String name = item.get().getGearType().getName();
        return getBuilder(item.getId().getPath())
                .parent(parent)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight");
    }

    private ItemModelBuilder tempGearBow(ItemRegistryObject<? extends ICoreItem> item, ModelFile parent) {
        String name = item.get().getGearType().getName();
        return getBuilder(item.getId().getPath())
                .parent(parent)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight")
                .texture("layer3", "item/" + name + "/bowstring_string");
    }

    private ItemModelBuilder tempGearCurio(ItemRegistryObject<? extends ICoreItem> item, ModelFile parent) {
        String name = item.get().getGearType().getName();
        return getBuilder(item.getId().getPath())
                .parent(parent)
                .texture("layer0", "item/" + name + "/main_generic_hc")
                .texture("layer1", "item/" + name + "/_highlight")
                .texture("layer2", "item/" + name + "/adornment_generic")
                .texture("layer3", "item/" + name + "/adornment_highlight");
    }

    private ItemModelBuilder tempGearArmor(ItemRegistryObject<? extends ICoreItem> item, ModelFile parent) {
        String name = item.get().getGearType().getName();
        return getBuilder(item.getId().getPath())
                .parent(parent)
                .texture("layer0", "item/" + name + "/main_generic_hc")
                .texture("layer1", "item/" + name + "/_highlight");
    }

    private ItemModelBuilder tempGearElytra(ItemRegistryObject<? extends ICoreItem> item, ModelFile parent) {
        String name = item.get().getGearType().getName();
        return getBuilder(item.getId().getPath())
                .parent(parent)
                .texture("layer0", "item/" + name + "/main_generic_hc")
                .texture("layer1", "item/" + name + "/_highlight")
                .texture("layer2", "item/" + name + "/binding_generic");
    }

    private ItemModelBuilder tempGearArrow(ItemRegistryObject<? extends ICoreItem> item, ModelFile parent) {
        String name = item.get().getGearType().getName();
        return getBuilder(item.getId().getPath())
                .parent(parent)
                .texture("layer0", "item/" + name + "/rod_generic_lc")
                .texture("layer1", "item/" + name + "/main_generic_hc")
                .texture("layer2", "item/" + name + "/_highlight")
                .texture("layer3", "item/" + name + "/fletching_generic");
    }

    private ItemModelBuilder tempMainPart(ItemRegistryObject<MainPartItem> item) {
        String name = item.get().getGearType().getName();
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(new ResourceLocation("item/generated")))
                .texture("layer0", "item/" + name + "/main_generic_hc")
                .texture("layer1", "item/" + name + "/_highlight")
                .texture("layer2", "item/part_marker");
    }

    private ItemModelBuilder tempGearPart(ItemRegistryObject<CompoundPartItem> item) {
        String name = item.get().getPartType().getName().getPath();
        return tempGearPart(item, "item/part/" + name);
    }

    private ItemModelBuilder tempGearPart(ItemRegistryObject<CompoundPartItem> item, String texture) {
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(new ResourceLocation("item/generated")))
                .texture("layer0", texture)
                .texture("layer1", "item/part_marker");
    }

    private ItemModelBuilder tempCoatingPart(ItemRegistryObject<CompoundPartItem> item) {
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(new ResourceLocation("item/generated")))
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
