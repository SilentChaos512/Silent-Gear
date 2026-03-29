package net.silentchaos512.gear.core.component;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;

public record TraitAddedEnchantments(
        Map<ResourceKey<Enchantment>, Integer> enchantments
) {
    public static final TraitAddedEnchantments EMPTY = new TraitAddedEnchantments(ImmutableMap.of());

    public static final Codec<TraitAddedEnchantments> CODEC =
            Codec.unboundedMap(ResourceKey.codec(Registries.ENCHANTMENT), Codec.INT)
                    .xmap(TraitAddedEnchantments::new, d -> d.enchantments);

    public static final StreamCodec<RegistryFriendlyByteBuf, TraitAddedEnchantments> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ResourceKey.streamCodec(Registries.ENCHANTMENT),
                    ByteBufCodecs.VAR_INT,
                    256
            ),
            e -> e.enchantments,
            TraitAddedEnchantments::new
    );

    public TraitAddedEnchantments(Map<ResourceKey<Enchantment>, Integer> enchantments) {
        this.enchantments = ImmutableMap.copyOf(enchantments);
    }

    public Mutable toMutable() {
        return new Mutable(this.enchantments);
    }

    public record Mutable(
            Map<ResourceKey<Enchantment>, Integer> enchantments
    ) {
        public Mutable(Map<ResourceKey<Enchantment>, Integer> enchantments) {
            this.enchantments = new HashMap<>(enchantments);
        }

        public Mutable set(ResourceKey<Enchantment> enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public TraitAddedEnchantments toImmutable() {
            return new TraitAddedEnchantments(this.enchantments);
        }
    }
}
