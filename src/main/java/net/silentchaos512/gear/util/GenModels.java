package net.silentchaos512.gear.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.lib.util.generator.ModelGenerator;

public class GenModels {
    public static void generateAll() {
        ModelGenerator.create(ModelGenerator.BlockBuilder
                .create(ModBlocks.FLOWER.asBlock())
                .parent("block/cross")
                .texture("cross", "flower")
                .item(ModelGenerator.ItemBuilder
                        .create(ModBlocks.FLOWER.asItem())
                        .texture(id("block/flower"))
                )
        );
        ModelGenerator.create(ModelGenerator.BlockBuilder
                .create(ModBlocks.NETHERWOOD_SAPLING.asBlock())
                .parent("block/cross")
                .texture("cross", "netherwood_sapling")
                .item(ModelGenerator.ItemBuilder
                        .create(ModBlocks.NETHERWOOD_SAPLING.asItem())
                        .texture(id("block/netherwood_sapling"))
                )
        );

        for (CraftingItems item : CraftingItems.values()) {
            ModelGenerator.create(item.asItem());
        }

        // Overwrite tip upgrade models with corrected versions
        tipUpgrade(CraftingItems.DIAMOND_TIPPED_UPGRADE, "diamond");
        tipUpgrade(CraftingItems.EMERALD_TIPPED_UPGRADE, "emerald");
        tipUpgrade(CraftingItems.GLOWSTONE_COATED_UPGRADE, "glowstone");
        tipUpgrade(CraftingItems.GOLD_TIPPED_UPGRADE, "gold");
        tipUpgrade(CraftingItems.IRON_TIPPED_UPGRADE, "iron");
        tipUpgrade(CraftingItems.LAPIS_COATED_UPGRADE, "lapis");
        tipUpgrade(CraftingItems.QUARTZ_TIPPED_UPGRADE, "quartz");
        tipUpgrade(CraftingItems.REDSTONE_COATED_UPGRADE, "redstone");

        ModelGenerator.create(ModItems.blueprintPackage);
        ModelGenerator.create(ModItems.flaxseeds);
        ModelGenerator.create(ModItems.netherBanana);

        for (IBlueprint blueprint : ModItems.blueprints) {
            Item item = (Item) blueprint;
            ModelGenerator.create(ModelGenerator.ItemBuilder.create(item)
                    .texture(blueprint.isSingleUse(new ItemStack(item)) ? "template" : "blueprint")
                    .texture(item.getRegistryName().getPath().replaceFirst("template", "blueprint")));
        }

        // Temp gear models
        for (ICoreItem item : ModItems.gearClasses.values()) {
            ModelGenerator.create(ModelGenerator.ItemBuilder.create(item.asItem())
                    .comment("Placeholder gear model")
                    .texture("blueprint_" + item.getGearType().getName()));
        }
    }

    private static void tipUpgrade(IItemProvider item, String name) {
        ModelGenerator.create(ModelGenerator.ItemBuilder.create(item.asItem())
                .texture("upgrade_base")
                .texture("upgrade_" + name));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(SilentGear.MOD_ID, name);
    }
}
