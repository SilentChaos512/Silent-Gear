package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
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

    private final Collection<Effect> effects = new ArrayList<>();

    public CancelEffectsTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    private static void deserialize(CancelEffectsTrait trait, JsonObject json) {
        JsonArray array = json.getAsJsonArray("effects");
        for (JsonElement element : array) {
            Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(element.getAsString()));
            if (effect != null) {
                trait.effects.add(effect);
            }
        }
    }

    private static void decode(CancelEffectsTrait trait, PacketBuffer buffer) {
        int count = buffer.readByte();
        for (int i = 0; i < count; ++i) {
            Effect effect = ForgeRegistries.POTIONS.getValue(buffer.readResourceLocation());
            if (effect != null) {
                trait.effects.add(effect);
            }
        }
    }

    private static void encode(CancelEffectsTrait trait, PacketBuffer buffer) {
        buffer.writeByte(trait.effects.size());
        trait.effects.forEach(effect -> buffer.writeResourceLocation(NameUtils.from(effect)));
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        if (isEquipped) {
            PlayerEntity player = context.getPlayer();
            if (player != null) {
                for (Effect effect : this.effects) {
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
