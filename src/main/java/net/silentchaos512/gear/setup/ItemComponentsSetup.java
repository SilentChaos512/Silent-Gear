package net.silentchaos512.gear.setup;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootPredicates;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConditionalValue;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.loot.provider.ItemMaxDurability;

/**
 * Compostable and fuel values. These were data maps before 26.3, and are now item components that reference
 * {@link ContextIntProvider}s in a datapack registry.
 */
@EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ItemComponentsSetup {
    public static final ResourceKey<ContextIntProvider> COOKING_TIME_NETHERWOOD_CHARCOAL = key("cooking/time_netherwood_charcoal");
    public static final ResourceKey<ContextIntProvider> COOKING_TIME_NETHERWOOD_CHARCOAL_BLOCK = key("cooking/time_netherwood_charcoal_block");
    /**
     * Burn time of gear with the flammable trait: max durability times {@link #FLAMMABLE_GEAR_TICKS_PER_DURABILITY}
     */
    public static final ResourceKey<ContextIntProvider> COOKING_TIME_FLAMMABLE_GEAR = key("cooking/time_flammable_gear");
    public static final int FLAMMABLE_GEAR_TICKS_PER_DURABILITY = 4;

    private ItemComponentsSetup() {}

    private static ResourceKey<ContextIntProvider> key(String path) {
        return ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, SilentGear.getId(path));
    }

    /**
     * Data generator bootstrap for the providers above. Burn times are halved in fast furnaces (smokers and blast
     * furnaces), the same as vanilla fuels.
     */
    public static void bootstrapContextIntProviders(BootstrapContext<ContextIntProvider> context) {
        HolderGetter<LootItemCondition> predicates = context.lookup(Registries.PREDICATE);
        HolderGetter<ContextIntProvider> providers = context.lookup(Registries.CONTEXT_INT_PROVIDER);
        context.register(COOKING_TIME_NETHERWOOD_CHARCOAL, cooking(predicates, providers, 2400));
        context.register(COOKING_TIME_NETHERWOOD_CHARCOAL_BLOCK, cooking(predicates, providers, 24_000));
        context.register(COOKING_TIME_FLAMMABLE_GEAR, cooking(predicates, providers, ContextIntProviders.mul(
                Holder.direct(ItemMaxDurability.INSTANCE),
                ContextIntProviders.exactly(FLAMMABLE_GEAR_TICKS_PER_DURABILITY)
        )));
    }

    private static ContextIntProvider cooking(HolderGetter<LootItemCondition> predicates, HolderGetter<ContextIntProvider> providers, int ticks) {
        return cooking(predicates, providers, ContextIntProviders.exactly(ticks));
    }

    private static ContextIntProvider cooking(HolderGetter<LootItemCondition> predicates, HolderGetter<ContextIntProvider> providers, Holder<ContextIntProvider> ticks) {
        Holder<ContextIntProvider> divisor = Holder.direct(new ConditionalValue(
                predicates.getOrThrow(LootPredicates.FAST_FURNACE),
                providers.getOrThrow(ContextIntProviders.COOKING_FAST_BURN_TIME_REDUCTION_FACTOR),
                providers.getOrThrow(ContextIntProviders.COOKING_NORMAL_BURN_TIME_REDUCTION_FACTOR)
        ));
        return ContextIntProviders.div(ticks, divisor).value();
    }

    @SubscribeEvent
    public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        compostable(event, SgItems.FLAX_SEEDS, ContextIntProviders.COMPOSTABLE_LOW);
        compostable(event, SgItems.FLUFFY_SEEDS, ContextIntProviders.COMPOSTABLE_LOW);
        compostable(event, CraftingItems.FLAX_FIBER, ContextIntProviders.COMPOSTABLE_LOW_MEDIUM);
        compostable(event, CraftingItems.FLAX_FLOWERS, ContextIntProviders.COMPOSTABLE_LOW_MEDIUM);
        compostable(event, CraftingItems.FLUFFY_PUFF, ContextIntProviders.COMPOSTABLE_LOW_MEDIUM);
        compostable(event, SgBlocks.NETHERWOOD_SAPLING, ContextIntProviders.COMPOSTABLE_LOW);
        compostable(event, SgItems.NETHER_BANANA, ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH);
        compostable(event, SgItems.GOLDEN_NETHER_BANANA, ContextIntProviders.COMPOSTABLE_ALWAYS_ADD_ONE);

        fuel(event, CraftingItems.NETHERWOOD_STICK, ContextIntProviders.COOKING_TIME_WOOD_SLABS);
        fuel(event, SgItems.NETHERWOOD_CHARCOAL, COOKING_TIME_NETHERWOOD_CHARCOAL);
        fuel(event, SgBlocks.NETHERWOOD_CHARCOAL_BLOCK, COOKING_TIME_NETHERWOOD_CHARCOAL_BLOCK);
    }

    private static void compostable(ModifyDefaultComponentsEvent event, ItemLike item, ResourceKey<ContextIntProvider> layers) {
        event.modify(item, (components, context, i) -> components.set(DataComponents.COMPOSTABLE, new Compostable(layers)));
    }

    private static void fuel(ModifyDefaultComponentsEvent event, ItemLike item, ResourceKey<ContextIntProvider> burnTime) {
        event.modify(item, (components, context, i) -> components.set(DataComponents.COOKING_FUEL,
                new CookingFuel(burnTime, ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)));
    }
}
