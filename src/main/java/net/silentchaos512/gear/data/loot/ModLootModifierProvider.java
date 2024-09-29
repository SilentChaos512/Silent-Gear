package net.silentchaos512.gear.data.loot;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
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

    @Override
    protected void start() {
        add("bonus_drops_trait", new BonusDropsTraitLootModifier(
                new LootItemCondition[]{}
        ));

        add(
                "magmatic_smelting",
                new MagmaticTraitLootModifier(
                        new LootItemCondition[]{
                                HasTraitCondition.builder(Const.Traits.MAGMATIC).build()
                        }
                )
        );

        add(
                "add_mob_drops/sinew",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                EntityType.COW.getDefaultLootTable(),
                                                EntityType.PIG.getDefaultLootTable(),
                                                BuiltInLootTables.SHEEP_BLACK,
                                                BuiltInLootTables.SHEEP_BLUE,
                                                BuiltInLootTables.SHEEP_BROWN,
                                                BuiltInLootTables.SHEEP_CYAN,
                                                BuiltInLootTables.SHEEP_GRAY,
                                                BuiltInLootTables.SHEEP_GREEN,
                                                BuiltInLootTables.SHEEP_LIGHT_BLUE,
                                                BuiltInLootTables.SHEEP_LIGHT_GRAY,
                                                BuiltInLootTables.SHEEP_LIME,
                                                BuiltInLootTables.SHEEP_MAGENTA,
                                                BuiltInLootTables.SHEEP_ORANGE,
                                                BuiltInLootTables.SHEEP_PINK,
                                                BuiltInLootTables.SHEEP_PURPLE,
                                                BuiltInLootTables.SHEEP_RED,
                                                BuiltInLootTables.SHEEP_WHITE,
                                                BuiltInLootTables.SHEEP_YELLOW
                                        )
                                )
                        },
                        SgLoot.Tables.DROPS_SINEW
                )
        );

        add(
                "add_mob_drops/fine_silk_low",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                EntityType.SPIDER.getDefaultLootTable()
                                        )
                                )
                        },
                        SgLoot.Tables.DROPS_FINE_SILK_LOW
                )
        );

        add(
                "add_mob_drops/fine_silk_high",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                EntityType.CAVE_SPIDER.getDefaultLootTable()
                                        )
                                )
                        },
                        SgLoot.Tables.DROPS_FINE_SILK_HIGH
                )
        );

        add(
                "add_mob_drops/leather_scraps_low",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                EntityType.HUSK.getDefaultLootTable(),
                                                EntityType.ZOMBIE.getDefaultLootTable()
                                        )
                                )
                        },
                        SgLoot.Tables.DROPS_LEATHER_SCRAPS_LOW
                )
        );

        add(
                "add_mob_drops/leather_scraps_high",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                anyOfLootTables(
                                        List.of(
                                                EntityType.ZOMBIE_VILLAGER.getDefaultLootTable()
                                        )
                                )
                        },
                        SgLoot.Tables.DROPS_LEATHER_SCRAPS_HIGH
                )
        );
    }

    private static LootItemCondition anyOfLootTables(List<ResourceKey<LootTable>> lootTables) {
        var array = lootTables.stream()
                .map(ResourceKey::location)
                .map(LootTableIdCondition::builder)
                .toArray(LootTableIdCondition.Builder[]::new);
        return AnyOfCondition.anyOf(array).build();
    }
}
