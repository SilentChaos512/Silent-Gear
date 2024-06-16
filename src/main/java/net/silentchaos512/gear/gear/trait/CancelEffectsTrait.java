package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class CancelEffectsTrait extends SimpleTrait {
    public static final ITraitSerializer<CancelEffectsTrait> SERIALIZER = new Serializer<>(
            ApiConst.CANCEL_EFFECTS_TRAIT_ID,
            CancelEffectsTrait::new,
            CancelEffectsTrait::deserialize,
            CancelEffectsTrait::decode,
            CancelEffectsTrait::encode
    );

    private final Collection<MobEffect> effects = new ArrayList<>();

    public CancelEffectsTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    private static void deserialize(CancelEffectsTrait trait, JsonObject json) {
        JsonArray array = json.getAsJsonArray("effects");
        for (JsonElement element : array) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(element.getAsString()));
            if (effect != null) {
                trait.effects.add(effect);
            }
        }
    }

    private static void decode(CancelEffectsTrait trait, FriendlyByteBuf buffer) {
        int count = buffer.readByte();
        for (int i = 0; i < count; ++i) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(buffer.readResourceLocation());
            if (effect != null) {
                trait.effects.add(effect);
            }
        }
    }

    private static void encode(CancelEffectsTrait trait, FriendlyByteBuf buffer) {
        buffer.writeByte(trait.effects.size());
        trait.effects.forEach(effect -> buffer.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getKey(effect))));
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        if (isEquipped) {
            Player player = context.player();
            if (player != null) {
                for (MobEffect effect : this.effects) {
                    player.removeEffect(effect);
                }
            }
        }
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();
        ret.add("  - Cancels these effects: " +
                this.effects.stream()
                        .map(e -> "`" + BuiltInRegistries.MOB_EFFECT.getKey(e) + "`")
                        .collect(Collectors.joining(", "))
        );
        return ret;
    }
}
