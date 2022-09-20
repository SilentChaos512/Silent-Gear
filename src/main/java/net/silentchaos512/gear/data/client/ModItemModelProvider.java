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
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.Registration;
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
        Registration.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(this::blockItemModel);

        ModelFile itemGenerated = getExistingFile(new ResourceLocation("item/generated"));
        ModelFile itemHandheld = getExistingFile(new ResourceLocation("item/handheld"));

        for (CraftingItems item : CraftingItems.values()) {
                builder(item, itemGenerated, "item/" + item.getName());
        }

        builder(ModItems.NETHERWOOD_CHARCOAL, itemGenerated);

        // Crafted materials
        builder(ModItems.SHEET_METAL)
                .parent(itemGenerated)
                .texture("layer0", "item/sheet_metal")
                .texture("layer1", "item/sheet_metal_highlight");

        // Compound materials
        builder(ModItems.ALLOY_INGOT)
                .parent(itemGenerated)
                .texture("layer0", "item/alloy_ingot")
                .texture("layer1", "item/alloy_ingot_highlight");
        builder(ModItems.HYBRID_GEM)
                .parent(itemGenerated)
                .texture("layer0", "item/hybrid_gem")
                .texture("layer1", "item/hybrid_gem_highlight");
        builder(ModItems.MIXED_FABRIC, itemGenerated, "item/mixed_fabric");

        // Custom materials
        builder(ModItems.CUSTOM_INGOT)
                .parent(itemGenerated)
                .texture("layer0", "item/alloy_ingot")
                .texture("layer1", "item/alloy_ingot_highlight");
        builder(ModItems.CUSTOM_GEM)
                .parent(itemGenerated)
                .texture("layer0", "item/hybrid_gem")
                .texture("layer1", "item/hybrid_gem_highlight");

        builder(ModItems.BLUEPRINT_BOOK)
                .parent(itemGenerated)
                .texture("layer0", "item/blueprint_book_cover")
                .texture("layer1", "item/blueprint_book_pages")
                .texture("layer2", "item/blueprint_book_deco");

        builder(ModItems.JEWELER_TOOLS, itemGenerated, "item/jeweler_tools");

        // Blueprints and templates
        Registration.getItems(PartBlueprintItem.class).forEach(item -> {
            if (item.hasStandardModel()) {
                builder(item)
                        .parent(itemGenerated)
                        .texture("layer0", "item/" + (item.isSingleUse() ? "template" : "blueprint"))
                        .texture("layer1", "item/blueprint_" + item.getPartType().getName().getPath());
            }
        });
        Registration.getItems(GearBlueprintItem.class).forEach(item -> builder(item)
                .parent(itemGenerated)
                .texture("layer0", "item/" + (item.isSingleUse() ? "template" : "blueprint"))
                .texture("layer1", "item/blueprint_" + item.getGearType().getName()));

        builder(ModItems.MOD_KIT, itemGenerated);

        // Repair kits
        builder(ModItems.VERY_CRUDE_REPAIR_KIT, itemGenerated);
        builder(ModItems.CRUDE_REPAIR_KIT, itemGenerated);
        builder(ModItems.STURDY_REPAIR_KIT, itemGenerated);
        builder(ModItems.CRIMSON_REPAIR_KIT, itemGenerated);
        builder(ModItems.AZURE_REPAIR_KIT, itemGenerated);

        // Misc
        builder(ModItems.GUIDE_BOOK, itemGenerated);
        builder(ModItems.BLUEPRINT_PACKAGE, itemGenerated);
        builder(ModItems.FLAX_SEEDS, itemGenerated);
        builder(ModItems.FLUFFY_SEEDS, itemGenerated);
        builder(ModItems.GOLDEN_NETHER_BANANA, itemGenerated);
        builder(ModItems.NETHER_BANANA, itemGenerated);
        builder(ModItems.PEBBLE, itemGenerated);

        // Temp models
        // Gear
        tempGear(ModItems.SWORD, itemHandheld);
        tempGear(ModItems.KATANA, itemHandheld);
        tempGear(ModItems.MACHETE, itemHandheld);
        tempGear(ModItems.SPEAR, itemHandheld);
        tempGear(ModItems.TRIDENT, itemHandheld);
        tempGear(ModItems.KNIFE, itemHandheld);
        tempGear(ModItems.DAGGER, itemHandheld);
        tempGear(ModItems.PICKAXE, itemHandheld);
        tempGear(ModItems.SHOVEL, itemHandheld);
        tempGear(ModItems.AXE, itemHandheld);
        tempGear(ModItems.PAXEL, itemHandheld);
        tempGear(ModItems.HAMMER, itemHandheld);
        tempGear(ModItems.EXCAVATOR, itemHandheld);
        tempGear(ModItems.SAW, itemHandheld);
        tempGear(ModItems.PROSPECTOR_HAMMER, itemHandheld);
        tempGear(ModItems.MATTOCK, itemHandheld);
        tempGear(ModItems.SICKLE, itemHandheld);
        tempGear(ModItems.SHEARS, itemHandheld);
        tempGear(ModItems.FISHING_ROD, itemHandheld);
        tempGear(ModItems.BOW, itemHandheld);
        tempGear(ModItems.CROSSBOW, itemHandheld);
        tempGear(ModItems.SLINGSHOT, itemHandheld);
        tempGear(ModItems.SHIELD, itemGenerated);
        tempGear(ModItems.ARROW, itemGenerated);
        tempGear(ModItems.HELMET, itemGenerated);
        tempGear(ModItems.CHESTPLATE, itemGenerated);
        tempGear(ModItems.LEGGINGS, itemGenerated);
        tempGear(ModItems.BOOTS, itemGenerated);
        tempGear(ModItems.ELYTRA, itemGenerated);
        tempGear(ModItems.RING, itemGenerated);
        tempGear(ModItems.BRACELET, itemGenerated);
        // Parts
        tempMainPart(ModItems.SWORD_BLADE);
        tempMainPart(ModItems.KATANA_BLADE);
        tempMainPart(ModItems.MACHETE_BLADE);
        tempMainPart(ModItems.SPEAR_TIP);
        tempMainPart(ModItems.TRIDENT_PRONGS);
        tempMainPart(ModItems.KNIFE_BLADE);
        tempMainPart(ModItems.DAGGER_BLADE);
        tempMainPart(ModItems.PICKAXE_HEAD);
        tempMainPart(ModItems.SHOVEL_HEAD);
        tempMainPart(ModItems.AXE_HEAD);
        tempMainPart(ModItems.PAXEL_HEAD);
        tempMainPart(ModItems.HAMMER_HEAD);
        tempMainPart(ModItems.EXCAVATOR_HEAD);
        tempMainPart(ModItems.SAW_BLADE);
        tempMainPart(ModItems.MATTOCK_HEAD);
        tempMainPart(ModItems.PROSPECTOR_HAMMER_HEAD);
        tempMainPart(ModItems.SICKLE_BLADE);
        tempMainPart(ModItems.SHEARS_BLADES);
        tempMainPart(ModItems.FISHING_REEL_AND_HOOK);
        tempMainPart(ModItems.BOW_LIMBS);
        tempMainPart(ModItems.CROSSBOW_LIMBS);
        tempMainPart(ModItems.SLINGSHOT_LIMBS);
        tempMainPart(ModItems.SHIELD_PLATE);
        tempMainPart(ModItems.HELMET_PLATES);
        tempMainPart(ModItems.CHESTPLATE_PLATES);
        tempMainPart(ModItems.LEGGING_PLATES);
        tempMainPart(ModItems.BOOT_PLATES);
        tempMainPart(ModItems.ELYTRA_WINGS);
        tempMainPart(ModItems.ARROW_HEADS);
        tempMainPart(ModItems.RING_SHANK);
        tempMainPart(ModItems.BRACELET_BAND);
        tempGearPart(ModItems.ROD);
        tempGearPart(ModItems.TIP);
        tempCoatingPart(ModItems.COATING);
        tempGearPart(ModItems.GRIP);
        tempGearPart(ModItems.BINDING);
        tempGearPart(ModItems.LINING, "item/part/lining_cloth");
        tempGearPart(ModItems.CORD);
        tempGearPart(ModItems.FLETCHING);
        tempGearPart(ModItems.ADORNMENT);
    }

    private ItemModelBuilder tempGear(ItemRegistryObject<? extends ICoreItem> item, ModelFile parent) {
        String name = item.get().getGearType().getName();
        return getBuilder(item.getId().getPath())
                .parent(parent)
                .texture("layer0", "item/blueprint_" + name)
                .texture("layer1", "item/" + name + "/_highlight");
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
        if (block == ModBlocks.FLAX_PLANT.get() || block == ModBlocks.FLUFFY_PLANT.get()) {
            return;
        }

        if (block == ModBlocks.PHANTOM_LIGHT.get())
            builder(block, getExistingFile(mcLoc("item/generated")), "item/phantom_light");
        else if (block == ModBlocks.NETHERWOOD_SAPLING.get() || block == ModBlocks.STONE_TORCH.get())
            builder(block, getExistingFile(mcLoc("item/generated")), "block/" + NameUtils.fromBlock(block).getPath());
        else if (block == ModBlocks.NETHERWOOD_FENCE.get())
            withExistingParent("netherwood_fence", modLoc("block/netherwood_fence_inventory"));
        else if (block == ModBlocks.NETHERWOOD_DOOR.get())
            builder(block, getExistingFile(mcLoc("item/generated")), "item/netherwood_door");
        else if (block == ModBlocks.NETHERWOOD_TRAPDOOR.get())
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
