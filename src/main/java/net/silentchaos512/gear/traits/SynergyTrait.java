package net.silentchaos512.gear.traits;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;

public class SynergyTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("synergy");
    public static final ITraitSerializer<SynergyTrait> SERIALIZER = new Serializer<>(
            SERIALIZER_ID,
            SynergyTrait::new,
            SynergyTrait::deserialize,
            SynergyTrait::read,
            SynergyTrait::write
    );

    private float multi = 0f;
    private float rangeMin = 0f;
    private float rangeMax = Float.MAX_VALUE;

    public SynergyTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    public double apply(double synergy, int traitLevel) {
        if (synergy > rangeMin && synergy < rangeMax) {
            return synergy + traitLevel * multi;
        }
        return synergy;
    }

    private static void deserialize(SynergyTrait trait, JsonObject json) {
        trait.multi = JSONUtils.getFloat(json, "synergy_multi");
        if (json.has("applicable_range")) {
            JsonObject range = json.get("applicable_range").getAsJsonObject();
            trait.rangeMin = JSONUtils.getFloat(range, "min", trait.rangeMin);
            trait.rangeMax = JSONUtils.getFloat(range, "max", trait.rangeMax);
        }
    }

    private static void read(SynergyTrait trait, PacketBuffer buffer) {
        trait.multi = buffer.readFloat();
        trait.rangeMin = buffer.readFloat();
        trait.rangeMax = buffer.readFloat();
    }

    private static void write(SynergyTrait trait, PacketBuffer buffer) {
        buffer.writeFloat(trait.multi);
        buffer.writeFloat(trait.rangeMin);
        buffer.writeFloat(trait.rangeMax);
    }
}
