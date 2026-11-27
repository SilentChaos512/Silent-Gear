package net.silentchaos512.gear.loot.provider;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.neoforged.neoforge.common.loot.NeoForgeLootContextParams;

/**
 * Provides the max durability of the item in the context (NeoForge's "queried stack"), such as the fuel item in a
 * furnace. Returns zero if the context has no item, or the item has no durability.
 * <p>
 * Used for the burn time of flammable gear, so fast furnaces (smokers, blast furnaces) can apply the same burn time
 * reduction as vanilla fuels. See {@code silentgear:cooking/time_flammable_gear}.
 */
public record ItemMaxDurability() implements ContextIntProvider {
    public static final ItemMaxDurability INSTANCE = new ItemMaxDurability();
    public static final MapCodec<ItemMaxDurability> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int getIntUnsafe(LootContext context) {
        var stack = context.getOptional(NeoForgeLootContextParams.QUERIED_STACK);
        if (stack == null) {
            return 0;
        }
        Integer maxDamage = stack.get(DataComponents.MAX_DAMAGE);
        return maxDamage != null ? maxDamage : 0;
    }

    @Override
    public MapCodec<ItemMaxDurability> codec() {
        return MAP_CODEC;
    }

    @Override
    public void validate(ValidationContext context) {
    }
}
