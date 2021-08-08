package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.lib.util.NameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class CancelEffectsTrait extends SimpleTrait {
    public static final ITraitSerializer<CancelEffectsTrait> SERIALIZER = new Serializer<>(
            SilentGear.getId("cancel_effects"),
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
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(element.getAsString()));
            if (effect != null) {
                trait.effects.add(effect);
            }
        }
    }

    private static void decode(CancelEffectsTrait trait, FriendlyByteBuf buffer) {
        int count = buffer.readByte();
        for (int i = 0; i < count; ++i) {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(buffer.readResourceLocation());
            if (effect != null) {
                trait.effects.add(effect);
            }
        }
    }

    private static void encode(CancelEffectsTrait trait, FriendlyByteBuf buffer) {
        buffer.writeByte(trait.effects.size());
        trait.effects.forEach(effect -> buffer.writeResourceLocation(NameUtils.from(effect)));
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        if (isEquipped) {
            Player player = context.getPlayer();
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
                        .map(e -> "`" + NameUtils.from(e) + "`")
                        .collect(Collectors.joining(", "))
        );
        return ret;
    }
}
