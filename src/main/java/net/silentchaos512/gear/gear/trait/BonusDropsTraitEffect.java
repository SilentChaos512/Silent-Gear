package net.silentchaos512.gear.gear.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;

import java.util.ArrayList;
import java.util.Collection;

public final class BonusDropsTraitEffect extends TraitEffect {
    public static final Codec<BonusDropsTraitEffect> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("base_chance").forGetter(t -> t.baseChance),
                    Codec.FLOAT.fieldOf("bonus_multiplier").forGetter(t -> t.bonusMultiplier),
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(t -> t.ingredient),
                    Codec.STRING.optionalFieldOf("matched_items_text_for_wiki", "some items").forGetter(t -> t.matchedItemsText)
            ).apply(instance, BonusDropsTraitEffect::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BonusDropsTraitEffect> STREAM_CODEC = StreamCodec.of(
            (buf, effect) -> {
                buf.writeFloat(effect.baseChance);
                buf.writeFloat(effect.bonusMultiplier);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, effect.ingredient);
            },
            buf -> {
                var baseChance = buf.readFloat();
                var bonusMultiplier = buf.readFloat();
                var ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                return new BonusDropsTraitEffect(baseChance, bonusMultiplier, ingredient, "some items");
            }
    );

    private final float baseChance;
    private final float bonusMultiplier;
    private final Ingredient ingredient;
    private final String matchedItemsText;

    public BonusDropsTraitEffect(float baseChance, float bonusMultiplier, Ingredient ingredient, String matchedItemsText) {
        this.baseChance = baseChance;
        this.bonusMultiplier = bonusMultiplier;
        this.ingredient = ingredient;
        this.matchedItemsText = matchedItemsText;
    }

    @Override
    public Codec<? extends TraitEffect> codec() {
        return CODEC;
    }

    @Override
    public ItemStack addLootDrops(TraitActionContext context, ItemStack stack) {
        if (ingredient.test(stack) && SilentGear.RANDOM.nextFloat() < this.baseChance * context.traitLevel()) {
            ItemStack copy = stack.copy();
            copy.setCount(Math.round(stack.getCount() * this.bonusMultiplier));
            return copy;
        }
        return super.addLootDrops(context, stack);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        ret.add(String.format("  - %d%% chance per level of dropping %d%% more of %s",
                (int) (100 * this.baseChance),
                (int) (100 * this.bonusMultiplier),
                matchedItemsText));
        return ret;
    }
}
