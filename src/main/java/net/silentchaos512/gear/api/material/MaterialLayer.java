package net.silentchaos512.gear.api.material;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.utils.Color;

public class MaterialLayer {
    private final ResourceLocation texture;
    private final int color;

    public MaterialLayer(ResourceLocation texture, int color) {
        this.texture = texture;
        this.color = color;
    }

    public ResourceLocation getTexture(GearType gearType) {
        String path = "item/" + gearType.getName() + "/" + this.texture.getPath();
        return new ResourceLocation(this.texture.getNamespace(), path);
    }

    public int getColor() {
        return color;
    }

    public static MaterialLayer deserialize(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject jo = json.getAsJsonObject();
            ResourceLocation texture = new ResourceLocation(JSONUtils.getString(jo, "texture"));
            int color = Color.from(jo, "color", Color.VALUE_WHITE).getColor();
            return new MaterialLayer(texture, color);
        }

        ResourceLocation texture = new ResourceLocation(json.getAsString());
        return new MaterialLayer(texture, Color.VALUE_WHITE);
    }

    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("texture", this.texture.toString());
        if ((this.color & 0xFFFFFF) != (Color.VALUE_WHITE & 0xFFFFFF)) {
            json.addProperty("color", Color.format(this.color));
        }
        return json;
    }

    public static MaterialLayer read(PacketBuffer buffer) {
        ResourceLocation texture = buffer.readResourceLocation();
        int color = buffer.readVarInt();
        return new MaterialLayer(texture, color);
    }

    public void write(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.texture);
        buffer.writeVarInt(this.color);
    }
}
