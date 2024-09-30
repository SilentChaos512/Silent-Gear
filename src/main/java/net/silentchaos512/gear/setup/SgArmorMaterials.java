package net.silentchaos512.gear.setup;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;

import java.util.List;

public class SgArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> REGISTRAR = DeferredRegister.create(Registries.ARMOR_MATERIAL, SilentGear.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> DUMMY = REGISTRAR.register(
            "dummy",
            () -> new ArmorMaterial(
                    ImmutableMap.of(
                            ArmorItem.Type.HELMET, 1,
                            ArmorItem.Type.CHESTPLATE, 1,
                            ArmorItem.Type.LEGGINGS, 1,
                            ArmorItem.Type.BOOTS, 1
                    ),
                    1,
                    SoundEvents.ARMOR_EQUIP_GENERIC,
                    () -> Ingredient.EMPTY,
                    List.of(
                            new ArmorMaterial.Layer(SilentGear.getId("main_generic_hc"), "", true)
                    ),
                    0f,
                    0f
            )
    );
}
