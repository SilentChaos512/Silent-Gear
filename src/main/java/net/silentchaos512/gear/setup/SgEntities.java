package net.silentchaos512.gear.setup;

import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.renderer.SgClientItemExtensions;
import net.silentchaos512.gear.client.renderer.entity.GearArrowRenderer;
import net.silentchaos512.gear.client.renderer.entity.GearThrownTridentRenderer;
import net.silentchaos512.gear.entity.GearFishingHook;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;
import net.silentchaos512.gear.entity.projectile.GearThrownTrident;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;
import net.silentchaos512.gear.item.gear.GearArmorItem;

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

    public static final DeferredHolder<EntityType<?>, EntityType<GearThrownTrident>> TRIDENT_PROJECTILE = ENTITIES.register(
            "thrown_trident",
            () -> EntityType.Builder.<GearThrownTrident>of(GearThrownTrident::new, MobCategory.MISC)
                    .eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, SilentGear.getId("thrown_trident")))
    );


    private SgEntities() {
        throw new IllegalAccessError("Utility class");
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, MobCategory type) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, type)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, SilentGear.getId(name))));
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = SilentGear.MOD_ID)
    public static class Events {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ARROW.get(), GearArrowRenderer::new);
            event.registerEntityRenderer(FISHING_HOOK.get(), FishingHookRenderer::new);
            event.registerEntityRenderer(SLINGSHOT_PROJECTILE.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(TRIDENT_PROJECTILE.get(), GearThrownTridentRenderer::new);
        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            // Register special model rendering for trident
            event.registerItem(
                    new SgClientItemExtensions(),
                    GearItemSets.TRIDENT.gearItem()
            );
            // Equipped armor colors
            event.registerItem(
                    new IClientItemExtensions() {
                        @Override
                        public int getArmorLayerTintColor(ItemStack stack, EquipmentClientInfo.Layer layer, int layerIdx, int fallbackColor) {
                            if (layerIdx == 0) {
                                return GearArmorItem.getArmorColor(stack);
                            }
                            return -1;
                        }
                    },
                    GearItemSets.HELMET.gearItem(),
                    GearItemSets.CHESTPLATE.gearItem(),
                    GearItemSets.LEGGINGS.gearItem(),
                    GearItemSets.BOOTS.gearItem(),
                    GearItemSets.ELYTRA.gearItem()
            );
        }

        @SubscribeEvent
        public static void registerAdditional(ModelEvent.RegisterStandalone event) {
            // FIXME
            //event.register(GearTridentModel.TRIDENT_ICON, StandaloneModelBaker.simpleModelWrapper());
        }
    }
}

