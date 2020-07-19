package net.silentchaos512.gear.data.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fml.RegistryObject;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.ToolHeadItem;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SilentGear.MOD_ID, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Silent Gear - Item Models";
    }

    private static final Collection<CraftingItems> TIP_UPGRADES = ImmutableList.of(
            CraftingItems.IRON_TIPPED_UPGRADE,
            CraftingItems.GOLD_TIPPED_UPGRADE,
            CraftingItems.DIAMOND_TIPPED_UPGRADE,
            CraftingItems.EMERALD_TIPPED_UPGRADE,
            CraftingItems.REDSTONE_COATED_UPGRADE,
            CraftingItems.GLOWSTONE_COATED_UPGRADE,
            CraftingItems.LAPIS_COATED_UPGRADE,
            CraftingItems.QUARTZ_TIPPED_UPGRADE
    );

    @Override
    protected void registerModels() {
        // Blocks
        Registration.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(this::blockItemModel);

        ModelFile itemGenerated = getExistingFile(new ResourceLocation("item/generated"));

        for (CraftingItems item : CraftingItems.values()) {
            if (!TIP_UPGRADES.contains(item)) {
                builder(item, itemGenerated, "item/" + item.getName());
            } else {
                String upgradeType = item.getName().substring(0, item.getName().indexOf('_'));
                getBuilder(item.getName())
                        .parent(itemGenerated)
                        .texture("layer0", "item/upgrade_base")
                        .texture("layer1", "item/upgrade_" + upgradeType);
            }
        }

        // Blueprints and templates
        Registration.getItems(PartBlueprintItem.class).forEach(item -> getBuilder(NameUtils.from(item).getPath())
                .parent(itemGenerated)
                .texture("layer0", "item/" + (item.isSingleUse() ? "template" : "blueprint"))
                .texture("layer1", "item/blueprint_" + item.getPartType().getName().getPath()));
        Registration.getItems(GearBlueprintItem.class).forEach(item -> getBuilder(NameUtils.from(item).getPath())
                .parent(itemGenerated)
                .texture("layer0", "item/" + (item.isSingleUse() ? "template" : "blueprint"))
                .texture("layer1", "item/blueprint_" + item.getGearType().getName()));

        // Repair kits
        builder(ModItems.CRUDE_REPAIR_KIT, itemGenerated);
        builder(ModItems.STURDY_REPAIR_KIT, itemGenerated);
        builder(ModItems.CRIMSON_REPAIR_KIT, itemGenerated);

        // Tool Heads
        Registration.getItems(ToolHeadItem.class).forEach(item -> {
            // TODO: Add dynamic models to pull correct texture for material
            String texture = item.getGearType().matches(GearType.ARMOR)
                    ? "item/dummy_icon_main"
                    : "item/" + item.getGearType().getName() + "/main_generic_hc";
            getBuilder(NameUtils.from(item).getPath())
                    .parent(itemGenerated)
                    .texture("layer0", texture);
        });

        // Compound parts
        builder(ModItems.BINDING, itemGenerated);
        builder(ModItems.BOWSTRING, itemGenerated);
        builder(ModItems.FLETCHING, itemGenerated);
        builder(ModItems.GRIP, itemGenerated);
        builder(ModItems.ROD, itemGenerated);
        builder(ModItems.LONG_ROD, itemGenerated);
        getBuilder("tip")
                .parent(itemGenerated)
                .texture("layer0", "item/upgrade_base")
                .texture("layer1", "item/custom_tip_upgrade")
                .texture("layer2", "item/custom_tip_upgrade_shine");

        // Misc
        builder(ModItems.BLUEPRINT_PACKAGE, itemGenerated);
        builder(ModItems.FLAX_SEEDS, itemGenerated);
        builder(ModItems.GOLDEN_NETHER_BANANA, itemGenerated);
        builder(ModItems.NETHER_BANANA, itemGenerated);
        builder(ModItems.PEBBLE, itemGenerated);
    }

    private void blockItemModel(Block block) {
        if (block == ModBlocks.FLAX_PLANT.get() || block == ModBlocks.WILD_FLAX_PLANT.get()) {
            return;
        }

        SilentGear.LOGGER.debug("blockItemModel: {}, {}", block, block.asItem());
        if (block == ModBlocks.PHANTOM_LIGHT.get()) {
            builder(block, getExistingFile(mcLoc("item/generated")), "item/phantom_light");
        } else if (block == ModBlocks.NETHERWOOD_SAPLING.get() || block == ModBlocks.STONE_TORCH.get()) {
            builder(block, getExistingFile(mcLoc("item/generated")), "block/" + NameUtils.from(block).getPath());
        } else if (block == ModBlocks.NETHERWOOD_FENCE.get()) {
            withExistingParent("netherwood_fence", modLoc("block/netherwood_fence_inventory"));
        } else if (block == ModBlocks.NETHERWOOD_DOOR.get()) {
            builder(block, getExistingFile(mcLoc("item/generated")), "item/netherwood_door");
        } else if (block == ModBlocks.NETHERWOOD_TRAPDOOR.get()) {
            withExistingParent("netherwood_trapdoor", modLoc("block/netherwood_trapdoor_bottom"));
        } else if (block.asItem() != Items.AIR) {
            String name = NameUtils.from(block).getPath();
            withExistingParent(name, modLoc("block/" + name));
        }
    }

    private void builder(IItemProvider item, ModelFile parent) {
        String name = NameUtils.fromItem(item).getPath();
        builder(item, parent, "item/" + name);
    }

    private void builder(IItemProvider item, ModelFile parent, String texture) {
        getBuilder(NameUtils.fromItem(item).getPath()).parent(parent).texture("layer0", texture);
    }
}
