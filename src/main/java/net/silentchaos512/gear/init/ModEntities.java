package net.silentchaos512.gear.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.renderer.entity.RenderSlingshotProjectile;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;
import net.silentchaos512.utils.Lazy;

import java.util.function.BiFunction;

public final class ModEntities {
    public static final Lazy<EntityType<SlingshotProjectile>> SLINGSHOT_PROJECTILE = makeType("slingshot_projectile", SlingshotProjectile::new, SlingshotProjectile::new);

    private ModEntities() {throw new IllegalAccessError("Utility class");}

    public static void registerTypes(RegistryEvent.Register<EntityType<?>> event) {
        registerType("slingshot_projectile", SLINGSHOT_PROJECTILE.get());
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(SLINGSHOT_PROJECTILE.get(), RenderSlingshotProjectile::new);
    }

    private static <T extends Entity> Lazy<EntityType<T>> makeType(String name, EntityType.IFactory<T> factory, BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory) {
        return Lazy.of(() -> EntityType.Builder.create(factory, EntityClassification.MONSTER).setCustomClientFactory(customClientFactory).build(SilentGear.getId(name).toString()));
    }

    private static void registerType(String name, EntityType<?> type) {
        ResourceLocation id = SilentGear.getId(name);
        type.setRegistryName(id);
        ForgeRegistries.ENTITIES.register(type);
    }
}
