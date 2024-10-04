package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Locale;

public enum CraftingItems implements ItemLike {
    CRUDE_TOOL_PARTS,
    BLUEPRINT_PAPER,
    TEMPLATE_BOARD,
    UPGRADE_BASE,
    ADVANCED_UPGRADE_BASE,
    BORT,
    BRONZE_INGOT,
    CRIMSON_IRON_INGOT,
    CRIMSON_STEEL_INGOT,
    BLAZE_GOLD_INGOT,
    AZURE_SILVER_INGOT,
    AZURE_ELECTRUM_INGOT,
    TYRIAN_STEEL_INGOT,
    CRIMSON_IRON_NUGGET,
    CRIMSON_STEEL_NUGGET,
    BLAZE_GOLD_NUGGET,
    AZURE_SILVER_NUGGET,
    AZURE_ELECTRUM_NUGGET,
    TYRIAN_STEEL_NUGGET,
    RAW_CRIMSON_IRON,
    CRIMSON_IRON_DUST,
    CRIMSON_STEEL_DUST,
    BLAZE_GOLD_DUST,
    RAW_AZURE_SILVER,
    AZURE_SILVER_DUST,
    AZURE_ELECTRUM_DUST,
    TYRIAN_STEEL_DUST,
    DIAMOND_SHARD, // possible removal
    EMERALD_SHARD, // possible removal
    NETHER_STAR_FRAGMENT,
    STARMETAL_DUST,
    GLOWING_DUST,
    BLAZING_DUST,
    GLITTERY_DUST,
    CRUSHED_SHULKER_SHELL,
    LEATHER_SCRAP,
    SINEW,
    DRIED_SINEW,
    SINEW_FIBER,
    FINE_SILK,
    FINE_SILK_CLOTH,
    FLAX_FIBER,
    FLAX_STRING,
    FLAX_FLOWERS,
    FLUFFY_PUFF,
    FLUFFY_FABRIC,
    FLUFFY_STRING,
    FLUFFY_FEATHER,
    // Rods
    ROUGH_ROD,
    STONE_ROD,
    IRON_ROD,
    NETHERWOOD_STICK,
    // Misc Upgrades
    SPOON_UPGRADE,
    ROAD_MAKER_UPGRADE,
    WIDE_PLATE_UPGRADE,
    RED_CARD_UPGRADE;

    private DeferredItem<ItemInternal> item = null;

    @Override
    public Item asItem() {
        if (this.item == null) {
            throw new NullPointerException("CraftingItems accessed too early!");
        }
        return this.item.get();
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static void register(DeferredRegister.Items items) {
        for (CraftingItems item : values()) {
            item.item = items.register(item.getName(), ItemInternal::new);
        }
    }

    private static final class ItemInternal extends Item {
        ItemInternal() {
            super(new Properties());
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
            String descKey = this.getDescriptionId() + ".desc";
            if (I18n.exists(descKey)) {
                tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.ITALIC));
            }
        }
    }
}
