package net.silentchaos512.gear.api.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class GearTypeMatcher implements Predicate<GearType> {
    public static final GearTypeMatcher ALL = new GearTypeMatcher(true, GearType.ALL);

    private final List<GearType> types = new ArrayList<>();
    private final boolean matchParents;

    public GearTypeMatcher(boolean matchParents, GearType... typesIn) {
        this.matchParents = matchParents;
        this.types.addAll(Arrays.asList(typesIn));
    }

    @Override
    public boolean test(GearType gearType) {
        for (GearType type : this.types) {
            if (this.matchParents) {
                if (gearType.matches(type)) {
                    return true;
                }
            } else if (gearType == type) {
                return true;
            }
        }
        return false;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        JsonArray typesArray = new JsonArray();
        this.types.forEach(t -> typesArray.add(t.getName()));
        json.add("types", typesArray);
        json.addProperty("match_parents", this.matchParents);
        return json;
    }

    public static GearTypeMatcher deserialize(JsonObject json) {
        boolean matchParents = JSONUtils.getBoolean(json, "match_parents");
        GearTypeMatcher result = new GearTypeMatcher(matchParents);
        JsonArray array = json.getAsJsonArray("types");
        array.forEach(e -> {
            GearType type = GearType.get(e.getAsString());
            if (type != GearType.NONE) {
                result.types.add(type);
            }
        });
        return result;
    }

    public static GearTypeMatcher read(PacketBuffer buffer) {
        boolean matchParents = buffer.readBoolean();
        GearTypeMatcher result = new GearTypeMatcher(matchParents);
        int count = buffer.readByte();
        for (int i = 0; i < count; ++i) {
            result.types.add(GearType.get(buffer.readString()));
        }
        return result;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeBoolean(this.matchParents);
        buffer.writeByte(this.types.size());
        this.types.forEach(t -> buffer.writeString(t.getName()));
    }
}
