package net.silentchaos512.gear.item.equipment;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.silentchaos512.gear.SilentGear;

public interface SgEquipmentAssets {
    ResourceKey<EquipmentAsset> GENERIC_SHINY = createId("generic_shiny");
    ResourceKey<EquipmentAsset> GENERIC_NETHERITE = createId("generic_netherite");
    ResourceKey<EquipmentAsset> GENERIC_TURTLE = createId("generic_turtle");
    ResourceKey<EquipmentAsset> CRIMSON_STEEL = createId("crimson_steel");

    static ResourceKey<EquipmentAsset> createId(String path) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, SilentGear.getId(path));
    }
}
