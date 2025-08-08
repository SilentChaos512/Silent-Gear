package net.silentchaos512.gear.data.client;

import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.item.equipment.SgEquipmentAssets;

import java.util.function.BiConsumer;

public class ModEquipmentAssetsProvider extends EquipmentAssetProvider {
    public ModEquipmentAssetsProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void registerModels(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
        output.accept(
                SgEquipmentAssets.CRIMSON_STEEL,
                EquipmentClientInfo.builder()
                        .addHumanoidLayers(SilentGear.getId("crimson_steel"), false)
                        .build()
        );
        output.accept(
                SgEquipmentAssets.GENERIC_NETHERITE,
                EquipmentClientInfo.builder()
                        .addHumanoidLayers(SilentGear.getId("generic_netherite"), true)
                        .build()
        );
        output.accept(
                SgEquipmentAssets.GENERIC_SHINY,
                EquipmentClientInfo.builder()
                        .addHumanoidLayers(SilentGear.getId("generic_shiny"), true)
                        .addHumanoidLayers(SilentGear.getId("generic_shiny_overlay"), false)
                        .build()
        );
        output.accept(
                SgEquipmentAssets.GENERIC_TURTLE,
                EquipmentClientInfo.builder()
                        .addMainHumanoidLayer(SilentGear.getId("generic_turtle"), true)
                        .addMainHumanoidLayer(SilentGear.getId("generic_turtle_overlay"), false)
                        .build()
        );
    }

    @Override
    public String getName() {
        return "Silent Gear - Equipment Asset Definitions";
    }
}
