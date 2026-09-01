package net.silentchaos512.gear.data.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.modifier.BonusDropsTraitLootModifier;
import net.silentchaos512.gear.loot.modifier.MagmaticTraitLootModifier;
import net.silentchaos512.gear.setup.SgLoot;
import net.silentchaos512.gear.util.Const;

import java.util.List;

public class ModLootModifierProvider extends GlobalLootModifierProvider {
    public ModLootModifierProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), SilentGear.MOD_ID);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    protected void start() {
        add("bonus_drops_trait", new BonusDropsTraitLootModifier(
                new LootItemCondition[]{},
                IGlobalLootModifier.DEFAULT_PRIORITY
        ));

        add(
                "magmatic_smelting",
                new MagmaticTraitLootModifier(
                        new LootItemCondition[]{
                                HasTraitCondition.builder(Const.Traits.MAGMATIC).build()
                        },
                        IGlobalLootModifier.DEFAULT_PRIORITY
                )
        );

        add(
                "add_mob_drops/sinew",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        Util.make(() -> {
                                            var builder = ImmutableList.<ResourceKey<LootTable>>builder()
                                                    .add(entityLootTable("cow"))
                                                    .add(entityLootTable("pig"));
                                            BuiltInLootTables.SHEEP.asList().forEach(builder::add);
                                            return builder.build();
                                        })
                                )
                        },
                        IGlobalLootModifier.DEFAULT_PRIORITY,
                        SgLoot.Tables.DROPS_SINEW
                )
        );

        add(
                "add_mob_drops/fine_silk_low",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                entityLootTable("spider")
                                        )
                                )
                        },
                        IGlobalLootModifier.DEFAULT_PRIORITY,
                        SgLoot.Tables.DROPS_FINE_SILK_LOW
                )
        );

        add(
                "add_mob_drops/fine_silk_high",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                entityLootTable("cave_spider")
                                        )
                                )
                        },
                        IGlobalLootModifier.DEFAULT_PRIORITY,
                        SgLoot.Tables.DROPS_FINE_SILK_HIGH
                )
        );

        add(
                "add_mob_drops/leather_scraps_low",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                entityLootTable("husk"),
                                                entityLootTable("zombie")
                                        )
                                )
                        },
                        IGlobalLootModifier.DEFAULT_PRIORITY,
                        SgLoot.Tables.DROPS_LEATHER_SCRAPS_LOW
                )
        );

        add(
                "add_mob_drops/leather_scraps_high",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                entityLootTable("zombie_villager")
                                        )
                                )
                        },
                        IGlobalLootModifier.DEFAULT_PRIORITY,
                        SgLoot.Tables.DROPS_LEATHER_SCRAPS_HIGH
                )
        );
    }

    private static ResourceKey<LootTable> entityLootTable(String name) {
        return BuiltInRegistries.ENTITY_TYPE.get(Identifier.withDefaultNamespace(name))
                .orElseThrow().value().getDefaultLootTable().orElseThrow();
    }

    private static LootItemCondition anyOfLootTables(List<ResourceKey<LootTable>> lootTables) {
        var array = lootTables.stream()
                .map(ResourceKey::identifier)
                .map(LootTableIdCondition::builder)
                .toArray(LootTableIdCondition.Builder[]::new);
        return AnyOfCondition.anyOf(array).build();
    }
}
