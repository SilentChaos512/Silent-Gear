package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.renderer.entity.GearArrowRenderer;
import net.silentchaos512.gear.client.renderer.entity.GearFishingHookRenderer;
import net.silentchaos512.gear.client.renderer.entity.RenderSlingshotProjectile;
import net.silentchaos512.gear.entity.GearFishingHook;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;

public final class SgEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SilentGear.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<GearArrowEntity>> ARROW = register("arrow",
            GearArrowEntity::new,
            MobCategory.MISC
    );
    public static final DeferredHolder<EntityType<?>, EntityType<GearFishingHook>> FISHING_HOOK = register("fishing_hook",
            GearFishingHook::new,
            MobCategory.MISC
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SlingshotProjectile>> SLINGSHOT_PROJECTILE = register("slingshot_projectile",
            SlingshotProjectile::new,
            MobCategory.MISC
    );

    private SgEntities() {
        throw new IllegalAccessError("Utility class");
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, MobCategory type) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, type)
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
