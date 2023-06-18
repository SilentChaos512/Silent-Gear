package net.silentchaos512.gear.setup;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.renderer.entity.GearArrowRenderer;
import net.silentchaos512.gear.client.renderer.entity.GearFishingHookRenderer;
import net.silentchaos512.gear.client.renderer.entity.RenderSlingshotProjectile;
import net.silentchaos512.gear.entity.GearFishingHook;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;

import java.util.function.BiFunction;

public final class SgEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SilentGear.MOD_ID);

    public static final RegistryObject<EntityType<GearArrowEntity>> ARROW = register("arrow",
            GearArrowEntity::new,
            MobCategory.MISC,
            GearArrowEntity::new);
    public static final RegistryObject<EntityType<GearFishingHook>> FISHING_HOOK = register("fishing_hook",
            GearFishingHook::new,
            MobCategory.MISC,
            GearFishingHook::new);
    public static final RegistryObject<EntityType<SlingshotProjectile>> SLINGSHOT_PROJECTILE = register("slingshot_projectile",
            SlingshotProjectile::new,
            MobCategory.MISC,
            SlingshotProjectile::new);

    private SgEntities() {
        throw new IllegalAccessError("Utility class");
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, MobCategory type, BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, type)
                .setCustomClientFactory(customClientFactory)
                .build(SilentGear.getId(name).toString()));
    }

    @Mod.EventBusSubscriber(modid = SilentGear.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Events {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ARROW.get(), GearArrowRenderer::new); // TODO: custom renderer
            event.registerEntityRenderer(FISHING_HOOK.get(), GearFishingHookRenderer::new);
            event.registerEntityRenderer(SLINGSHOT_PROJECTILE.get(), RenderSlingshotProjectile::new);
        }
    }
}
