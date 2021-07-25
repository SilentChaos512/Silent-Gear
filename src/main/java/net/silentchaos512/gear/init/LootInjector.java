package net.silentchaos512.gear.init;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class LootInjector {
    public static final class Tables {
        private static final Map<ResourceLocation, ResourceLocation> MAP = new HashMap<>();

        public static final ResourceLocation CHESTS_NETHER_BRIDGE = inject(BuiltInLootTables.NETHER_BRIDGE);
        public static final ResourceLocation CHESTS_BASTION_TREASURE = inject(BuiltInLootTables.BASTION_TREASURE);
        public static final ResourceLocation CHESTS_BASTION_OTHER = inject(BuiltInLootTables.BASTION_OTHER);
        public static final ResourceLocation CHESTS_BASTION_BRIDGE = inject(BuiltInLootTables.BASTION_BRIDGE);
        public static final ResourceLocation CHESTS_RUINED_PORTAL = inject(BuiltInLootTables.RUINED_PORTAL);

        public static final ResourceLocation ENTITIES_CAVE_SPIDER = inject(new ResourceLocation("entities/cave_spider"));
        public static final ResourceLocation ENTITIES_SPIDER = inject(new ResourceLocation("entities/spider"));

        private Tables() {}

        public static Collection<ResourceLocation> getValues() {
            return MAP.values();
        }

        public static Optional<ResourceLocation> get(ResourceLocation lootTable) {
            return Optional.ofNullable(MAP.get(lootTable));
        }

        private static ResourceLocation inject(ResourceLocation lootTable) {
            ResourceLocation ret = SilentGear.getId("inject/" + lootTable.getPath());
            MAP.put(lootTable, ret);
            return ret;
        }
    }

    private LootInjector() {}

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        Tables.get(event.getName()).ifPresent(injectorName -> {
            SilentGear.LOGGER.info("Injecting loot table '{}' into '{}'", injectorName, event.getName());
            event.getTable().addPool(
                    LootPool.lootPool()
                            .name("silentgear_injected")
                            .add(LootTableReference.lootTableReference(injectorName))
                            .build()
            );
        });
    }
}
