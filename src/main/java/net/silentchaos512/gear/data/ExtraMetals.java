package net.silentchaos512.gear.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Locale;

public enum ExtraMetals {
    ALUMINUM,
    ALUMINUM_STEEL,
    BISMUTH,
    BISMUTH_BRASS,
    BISMUTH_STEEL,
    BRASS,
    COMPRESSED_IRON,
    ELECTRUM,
    ENDERIUM,
    INVAR,
    LEAD,
    LUMIUM,
    NICKEL,
    OSMIUM,
    PLATINUM,
    REDSTONE_ALLOY,
    REFINED_GLOWSTONE,
    REFINED_IRON,
    REFINED_OBSIDIAN,
    SIGNALUM,
    SILVER,
    STEEL,
    TIN,
    TITANIUM,
    URANIUM,
    ZINC;

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public TagKey<Item> getMainTag() {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/" + getName()));
    }

    public TagKey<Item> getRodTag() {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "rods/" + getName()));
    }
}
