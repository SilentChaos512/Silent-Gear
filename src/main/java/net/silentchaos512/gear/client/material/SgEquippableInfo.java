package net.silentchaos512.gear.client.material;

import net.minecraft.sounds.SoundEvents;
import net.silentchaos512.gear.api.material.MaterialEquippableInfo;
import net.silentchaos512.gear.item.equipment.SgEquipmentAssets;

public interface SgEquippableInfo {
    MaterialEquippableInfo GENERIC_SHINY = new MaterialEquippableInfo(
            SoundEvents.ARMOR_EQUIP_GENERIC,
            SgEquipmentAssets.GENERIC_SHINY,
            SgEquipmentAssets.GENERIC_SHINY,
            true
    );
    MaterialEquippableInfo GENERIC_FLAT = new MaterialEquippableInfo(
            SoundEvents.ARMOR_EQUIP_GENERIC,
            SgEquipmentAssets.GENERIC_FLAT,
            SgEquipmentAssets.GENERIC_FLAT,
            true
    );
    MaterialEquippableInfo CRIMSON_STEEL = new MaterialEquippableInfo(
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            SgEquipmentAssets.CRIMSON_STEEL,
            SgEquipmentAssets.GENERIC_NETHERITE,
            false
    );
}
