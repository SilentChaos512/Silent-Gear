package net.silentchaos512.gear.init;

import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.renderer.entity.RenderSlingshotProjectile;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;

import java.util.function.BiFunction;

public final class ModEntities {
    public static final RegistryObject<EntityType<GearArrowEntity>> ARROW = register("arrow",
            GearArrowEntity::new,
            MobCategory.MISC,
            GearArrowEntity::new);
    public static final RegistryObject<EntityType<SlingshotProjectile>> SLINGSHOT_PROJECTILE = register("slingshot_projectile",
            SlingshotProjectile::new,
            MobCategory.MISC,
            SlingshotProjectile::new);

    private ModEntities() {throw new IllegalAccessError("Utility class");}

    static void register() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ARROW.get(), TippableArrowRenderer::new); // TODO: custom renderer
        RenderingRegistry.registerEntityRenderingHandler(SLINGSHOT_PROJECTILE.get(), RenderSlingshotProjectile::new);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, MobCategory type, BiFunction<FMLPlayMessages.SpawnEntity, Level, T> customClientFactory) {
        return Registration.ENTITIES.register(name, () -> EntityType.Builder.of(factory, type)
                .setCustomClientFactory(customClientFactory)
                .build(SilentGear.getId(name).toString()));
    }
}
