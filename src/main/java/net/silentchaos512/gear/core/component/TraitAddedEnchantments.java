package net.silentchaos512.gear.core.component;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public record TraitAddedEnchantments(
        List<Holder<Enchantment>> enchantments // FIXME: map of trait data resource to enchantments?
) {
    /*public static final Codec<TraitAddedEnchantments> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(BuiltInRegistries.ENCHANTMENT.holderByNameCodec()).fieldOf("enchantments").forGetter(d -> d.enchantments)
            ).apply(instance, TraitAddedEnchantments::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TraitAddedEnchantments> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT).apply(ByteBufCodecs.list()), d -> d.enchantments,
            TraitAddedEnchantments::new
    );

    public TraitAddedEnchantments(List<Holder<Enchantment>> enchantments) {
        this.enchantments = ImmutableList.copyOf(enchantments);
    }

    public static TraitAddedEnchantments empty() {
        return new TraitAddedEnchantments(Collections.emptyList());
    }

    public static class Mutable {
        private final List<Holder<Enchantment>> list = new ArrayList<>();

        public Mutable(TraitAddedEnchantments original) {
            this.list.addAll(original.enchantments);
        }

        public TraitAddedEnchantments toImmutable() {
            return new TraitAddedEnchantments(this.list);
        }
    }*/
}
