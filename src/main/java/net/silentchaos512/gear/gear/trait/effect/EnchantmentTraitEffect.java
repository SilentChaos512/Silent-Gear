package net.silentchaos512.gear.gear.trait.effect;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.core.component.TraitAddedEnchantments;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;

import java.util.*;

public class EnchantmentTraitEffect extends TraitEffect {
    private static final Codec<List<Integer>> LEVEL_LIST_CODEC = Codec.withAlternative(
            Codec.INT.listOf(),
            Codec.INT.xmap(List::of, List::getFirst)
    );
    public static final MapCodec<EnchantmentTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Codec.unboundedMap(ResourceKey.codec(Registries.ENCHANTMENT), LEVEL_LIST_CODEC)
                            .fieldOf("enchantments")
                            .forGetter(e -> e.enchantments)
            ).apply(i, EnchantmentTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ResourceKey.streamCodec(Registries.ENCHANTMENT),
                    ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()),
                    256
            ),
            e -> e.enchantments,
            EnchantmentTraitEffect::new
    );

    private final Map<ResourceKey<Enchantment>, List<Integer>> enchantments;

    public EnchantmentTraitEffect(Map<ResourceKey<Enchantment>, List<Integer>> enchantments) {
        this.enchantments = ImmutableMap.copyOf(enchantments);
    }

    public static EnchantmentTraitEffect single(ResourceKey<Enchantment> enchantment, Integer... levelsByTraitLevel) {
        return new EnchantmentTraitEffect(ImmutableMap.of(enchantment, Arrays.asList(levelsByTraitLevel)));
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.ENCHANTMENT.get();
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        return List.of();
    }

    @Override
    public void onRecalculatePost(ItemStack gear, int traitLevel) {
        TraitAddedEnchantments.Mutable traitEnchantments = gear.getOrDefault(SgDataComponents.TRAIT_ENCHANTMENTS, TraitAddedEnchantments.EMPTY).toMutable();
        enchantments.forEach((enchantmentKey, levelList) -> {
            var enchantmentLevel = getEnchantmentLevel(enchantmentKey, traitLevel);
            traitEnchantments.set(enchantmentKey, enchantmentLevel);
        });
        gear.set(SgDataComponents.TRAIT_ENCHANTMENTS, traitEnchantments.toImmutable());
    }

    private int getEnchantmentLevel(ResourceKey<Enchantment> key, int traitLevel) {
        var list = this.enchantments.getOrDefault(key, List.of());
        if (list.isEmpty()) {
            return 0;
        }
        return list.get(Mth.clamp(traitLevel - 1, 0, list.size() - 1));
    }

    @EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onGetEnchantmentLevels(GetEnchantmentLevelEvent event) {
            var stack = event.getStack();
            var traitEnchantments = stack.getOrDefault(SgDataComponents.TRAIT_ENCHANTMENTS, TraitAddedEnchantments.EMPTY);
            traitEnchantments.enchantments().forEach((key, level) -> {
                if (event.isTargetting(key)) {
                    event.getHolder(key).ifPresent(holder -> event.getEnchantments().set(holder, level));
                }
            });
        }
    }
}
