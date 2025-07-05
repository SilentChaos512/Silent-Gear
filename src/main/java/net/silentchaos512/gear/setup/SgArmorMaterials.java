package net.silentchaos512.gear.setup;

import com.google.common.collect.ImmutableMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.neoforge.common.Tags;

public class SgArmorMaterials {
    public static final ArmorMaterial DUMMY = new ArmorMaterial(
            1,
            ImmutableMap.of(
                    ArmorType.HELMET, 1,
                    ArmorType.CHESTPLATE, 1,
                    ArmorType.LEGGINGS, 1,
                    ArmorType.BOOTS, 1
            ),
            1,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            0f,
            0f,
            Tags.Items.DRINKS_WATER,
            EquipmentAssets.IRON
    );
}
