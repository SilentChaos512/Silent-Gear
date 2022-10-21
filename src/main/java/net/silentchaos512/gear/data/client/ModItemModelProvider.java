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
import net.silentchaos512.gear.init.SgBlocks;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.lib.registry.ItemRegistryObject;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnull;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SilentGear.MOD_ID, existingFileHelper);
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
        tempGear(SgItems.SWORD, itemHandheld);
        tempGear(SgItems.KATANA, itemHandheld);
        tempGear(SgItems.MACHETE, itemHandheld);
        tempGear(SgItems.SPEAR, itemHandheld);
        tempGear(SgItems.TRIDENT, itemHandheld);
        tempGear(SgItems.KNIFE, itemHandheld);
        tempGear(SgItems.DAGGER, itemHandheld);
        tempGear(SgItems.PICKAXE, itemHandheld);
        tempGear(SgItems.SHOVEL, itemHandheld);
        tempGear(SgItems.AXE, itemHandheld);
        tempGear(SgItems.PAXEL, itemHandheld);
        tempGear(SgItems.HAMMER, itemHandheld);
        tempGear(SgItems.EXCAVATOR, itemHandheld);
        tempGear(SgItems.SAW, itemHandheld);
        tempGear(SgItems.PROSPECTOR_HAMMER, itemHandheld);
        tempGear(SgItems.MATTOCK, itemHandheld);
        tempGear(SgItems.SICKLE, itemHandheld);
        tempGear(SgItems.SHEARS, itemHandheld);
        tempGearBow(SgItems.FISHING_ROD, itemHandheld);
        tempGearBow(SgItems.BOW, itemHandheld);
        tempGearBow(SgItems.CROSSBOW, itemHandheld);
        tempGearBow(SgItems.SLINGSHOT, itemHandheld);
        tempGear(SgItems.SHIELD, itemGenerated);
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
                .texture("layer2", "item/part_marker")
                .texture("layer3", "item/temp_model_overlay");
    }

    private ItemModelBuilder tempGearPart(ItemRegistryObject<CompoundPartItem> item) {
        String name = item.get().getPartType().getName().getPath();
        return tempGearPart(item, "item/part/" + name);
    }

    private ItemModelBuilder tempGearPart(ItemRegistryObject<CompoundPartItem> item, String texture) {
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(new ResourceLocation("item/generated")))
                .texture("layer0", texture)
                .texture("layer1", "item/part_marker")
                .texture("layer2", "item/temp_model_overlay");
    }

    private ItemModelBuilder tempCoatingPart(ItemRegistryObject<CompoundPartItem> item) {
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(new ResourceLocation("item/generated")))
                .texture("layer0", "item/part/coating_material")
                .texture("layer1", "item/part/coating_jar")
                .texture("layer2", "item/part_marker")
                .texture("layer3", "item/temp_model_overlay");
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
