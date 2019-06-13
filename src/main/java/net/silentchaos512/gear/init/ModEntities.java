package net.silentchaos512.gear.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.renderer.entity.RenderSlingshotProjectile;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;
import net.silentchaos512.utils.Lazy;

import java.util.Locale;
import java.util.function.Supplier;

public enum ModEntities {
    SLINGSHOT_PROJECTILE(() -> EntityType.Builder.<SlingshotProjectile>create(SlingshotProjectile::new, EntityClassification.MISC));

    private final Lazy<EntityType<?>> entityType;

    ModEntities(Supplier<EntityType.Builder<?>> factory) {
        this.entityType = Lazy.of(() -> {
            ResourceLocation id = SilentGear.getId(this.getName());
            return factory.get().build(id.toString());
        });
    }

    public EntityType<?> type() {
        return this.entityType.get();
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static void registerAll(RegistryEvent.Register<EntityType<?>> event) {
        if (!event.getRegistry().getRegistryName().equals(ForgeRegistries.ENTITIES.getRegistryName())) return;

        for (ModEntities entity : values()) {
            EntityType<?> type = entity.type();
            type.setRegistryName(SilentGear.getId(entity.getName()));
            ForgeRegistries.ENTITIES.register(type);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(SlingshotProjectile.class, new RenderSlingshotProjectile.Factory());
    }
}
