package net.silentchaos512.gear.init;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.ResourceLocation;
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

        public static final ResourceLocation CHESTS_NETHER_BRIDGE = inject(LootTables.CHESTS_NETHER_BRIDGE);
        public static final ResourceLocation CHESTS_BASTION_TREASURE = inject(LootTables.BASTION_TREASURE);
        public static final ResourceLocation CHESTS_BASTION_OTHER = inject(LootTables.BASTION_OTHER);
        public static final ResourceLocation CHESTS_BASTION_BRIDGE = inject(LootTables.BASTION_BRIDGE);
        public static final ResourceLocation CHESTS_RUINED_PORTAL = inject(LootTables.RUINED_PORTAL);

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
                    LootPool.builder()
                            .name("silentgear_injected")
                            .addEntry(TableLootEntry.builder(injectorName))
                            .build()
            );
        });
    }
}
