package net.silentchaos512.gear.setup;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.function.SetPartsFunction;
import net.silentchaos512.gear.loot.modifier.BonusDropsTraitLootModifier;
import net.silentchaos512.gear.loot.modifier.MagmaticTraitLootModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class SgLoot {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, SilentGear.MOD_ID);
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, SilentGear.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SilentGear.MOD_ID);

    // Conditions
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> HAS_TRAIT =
            registerCondition("has_trait", () -> new LootItemConditionType(HasTraitCondition.CODEC));

    // Functions
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<? extends LootItemConditionalFunction>> SET_PARTS =
            registerFunction("set_parts", () -> new LootItemFunctionType<>(SetPartsFunction.CODEC));

    // Modifiers
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BonusDropsTraitLootModifier>> BONUS_DROPS_TRAIT =
            registerModifier("bonus_drops_trait", BonusDropsTraitLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<MagmaticTraitLootModifier>> MAGMATIC_SMELTING =
            registerModifier("magmatic_smelting", MagmaticTraitLootModifier.CODEC);

    private SgLoot() {
    }

    private static <T extends LootItemConditionType> DeferredHolder<LootItemConditionType, T> registerCondition(String name, Supplier<T> condition) {
        return LOOT_CONDITIONS.register(name, condition);
    }

    private static <T extends LootItemFunctionType<? extends LootItemConditionalFunction>> DeferredHolder<LootItemFunctionType<?>, T> registerFunction(String name, Supplier<T> condition) {
        return LOOT_FUNCTIONS.register(name, condition);
    }

    private static <T extends IGlobalLootModifier> DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<T>> registerModifier(String name, Supplier<MapCodec<T>> codec) {
        return LOOT_MODIFIERS.register(name, codec);
    }

    @EventBusSubscriber
    public static final class Injector {
        public static final class Tables {
            private static final Map<ResourceLocation, ResourceKey<LootTable>> MAP = new HashMap<>();

            // Chests
            public static final ResourceLocation NETHER_BRIDGE = inject(BuiltInLootTables.NETHER_BRIDGE);
            public static final ResourceLocation BASTION_TREASURE = inject(BuiltInLootTables.BASTION_TREASURE);
            public static final ResourceLocation BASTION_OTHER = inject(BuiltInLootTables.BASTION_OTHER);
            public static final ResourceLocation BASTION_BRIDGE = inject(BuiltInLootTables.BASTION_BRIDGE);
            public static final ResourceLocation RUINED_PORTAL = inject(BuiltInLootTables.RUINED_PORTAL);

            // Entities
            public static final ResourceLocation CAVE_SPIDER = inject(EntityType.CAVE_SPIDER.getDefaultLootTable());
            public static final ResourceLocation COW = inject(EntityType.COW.getDefaultLootTable());
            public static final ResourceLocation PIG = inject(EntityType.PIG.getDefaultLootTable());
            public static final ResourceLocation SHEEP = inject(EntityType.SHEEP.getDefaultLootTable());
            public static final ResourceLocation SPIDER = inject(EntityType.SPIDER.getDefaultLootTable());
            public static final ResourceLocation ZOMBIE_VILLAGER = inject(EntityType.ZOMBIE_VILLAGER.getDefaultLootTable());

            public static Optional<ResourceKey<LootTable>> get(ResourceLocation lootTable) {
                return Optional.ofNullable(MAP.get(lootTable));
            }

            private static ResourceLocation inject(ResourceKey<LootTable> lootTable) {
                var originalId = lootTable.location();
                var newId = SilentGear.getId("inject/" + originalId.getNamespace() + "/" + originalId.getPath());
                MAP.put(originalId, ResourceKey.create(Registries.LOOT_TABLE, newId));
                return newId;
            }
        }

        private Injector() {}

        @SubscribeEvent
        public static void onLootTableLoad(LootTableLoadEvent event) {
            Tables.get(event.getName()).ifPresent(injectorName -> {
                SilentGear.LOGGER.info("Injecting loot table '{}' into '{}'", injectorName, event.getName());
                event.getTable().addPool(
                        LootPool.lootPool()
                                .name("silentgear_injected")
                                .add(NestedLootTable.lootTableReference(injectorName))
                                .build()
                );
            });
        }
    }
}
