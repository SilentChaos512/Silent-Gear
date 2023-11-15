package net.silentchaos512.gear.setup;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.function.SelectGearTierLootFunction;
import net.silentchaos512.gear.loot.function.SetPartsFunction;
import net.silentchaos512.gear.loot.modifier.BonusDropsTraitLootModifier;
import net.silentchaos512.gear.loot.modifier.MagmaticTraitLootModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class SgLoot {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, SilentGear.MOD_ID);
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, SilentGear.MOD_ID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SilentGear.MOD_ID);

    // Conditions
    public static final RegistryObject<LootItemConditionType> HAS_TRAIT =
            registerCondition("has_trait", () -> new LootItemConditionType(HasTraitCondition.SERIALIZER));

    // Functions
    public static final RegistryObject<LootItemFunctionType> SELECT_TIER =
            registerFunction("select_tier", () -> new LootItemFunctionType(SelectGearTierLootFunction.SERIALIZER));
    public static final RegistryObject<LootItemFunctionType> SET_PARTS =
            registerFunction("set_parts", () -> new LootItemFunctionType(SetPartsFunction.SERIALIZER));

    // Modifiers
    public static final RegistryObject<Codec<BonusDropsTraitLootModifier>> BONUS_DROPS_TRAIT =
            registerModifier("bonus_drops_trait", BonusDropsTraitLootModifier.CODEC);
    public static final RegistryObject<Codec<MagmaticTraitLootModifier>> MAGMATIC_SMELTING =
            registerModifier("magmatic_smelting", MagmaticTraitLootModifier.CODEC);

    private SgLoot() {
    }

    private static <T extends LootItemConditionType> RegistryObject<T> registerCondition(String name, Supplier<T> condition) {
        return LOOT_CONDITIONS.register(name, condition);
    }

    private static <T extends LootItemFunctionType> RegistryObject<T> registerFunction(String name, Supplier<T> condition) {
        return LOOT_FUNCTIONS.register(name, condition);
    }

    private static <T extends IGlobalLootModifier> RegistryObject<Codec<T>> registerModifier(String name, Supplier<Codec<T>> codec) {
        return LOOT_MODIFIERS.register(name, codec);
    }

    @Mod.EventBusSubscriber
    public static final class Injector {
        public static final class Tables {
            private static final Map<ResourceLocation, ResourceLocation> MAP = new HashMap<>();

            // Chests
            public static final ResourceLocation NETHER_BRIDGE = inject(BuiltInLootTables.NETHER_BRIDGE);
            public static final ResourceLocation BASTION_TREASURE = inject(BuiltInLootTables.BASTION_TREASURE);
            public static final ResourceLocation BASTION_OTHER = inject(BuiltInLootTables.BASTION_OTHER);
            public static final ResourceLocation BASTION_BRIDGE = inject(BuiltInLootTables.BASTION_BRIDGE);
            public static final ResourceLocation RUINED_PORTAL = inject(BuiltInLootTables.RUINED_PORTAL);

            // Entities
            public static final ResourceLocation CAVE_SPIDER = inject(EntityType.CAVE_SPIDER.getDefaultLootTable());
            public static final ResourceLocation SPIDER = inject(EntityType.SPIDER.getDefaultLootTable());
            public static final ResourceLocation ZOMBIE_VILLAGER = inject(EntityType.ZOMBIE_VILLAGER.getDefaultLootTable());

            public static Optional<ResourceLocation> get(ResourceLocation lootTable) {
                return Optional.ofNullable(MAP.get(lootTable));
            }

            private static ResourceLocation inject(ResourceLocation lootTable) {
                ResourceLocation ret = SilentGear.getId("inject/" + lootTable.getNamespace() + "/" + lootTable.getPath());
                MAP.put(lootTable, ret);
                return ret;
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
                                .add(LootTableReference.lootTableReference(injectorName))
                                .build()
                );
            });
        }
    }
}
