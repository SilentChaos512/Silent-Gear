package net.silentchaos512.gear.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraftforge.registries.RegisterEvent;
import net.silentchaos512.gear.SilentGear;

public class SgWorldFeatures {
    public static final Feature<RandomPatchConfiguration> WILD_PLANT = new RandomPatchFeature(RandomPatchConfiguration.CODEC);

    public static void registerFeatures(RegisterEvent event) {
        if (!Registries.FEATURE.equals(event.getRegistryKey())) {
            return;
        }

        event.register(Registries.FEATURE, SilentGear.getId("wild_plant"), () -> WILD_PLANT);
    }
}
