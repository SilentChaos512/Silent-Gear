package net.silentchaos512.gear.api.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

import javax.annotation.Nullable;

public enum TextureType {
    HIGH_CONTRAST("hc"),
    LOW_CONTRAST("lc");

    private final String alias;
    private final ResourceLocation armorTexture1;
    private final ResourceLocation armorTexture2;

    TextureType(String alias) {
        this.alias = alias;
        this.armorTexture1 = SilentGear.getId("textures/models/armor/main_generic_" + this.alias + "_layer_1.png");
        this.armorTexture2 = SilentGear.getId("textures/models/armor/main_generic_" + this.alias + "_layer_2.png");
    }

    public ResourceLocation getArmorTexture(boolean innerModel) {
        return innerModel ? this.armorTexture2 : this.armorTexture1;
    }

    @Nullable
    public static TextureType fromString(String str) {
        for (TextureType type : values()) {
            if (str.equalsIgnoreCase(type.name()) || str.equalsIgnoreCase(type.alias)) {
                return type;
            }
        }
        return null;
    }

    public static final Codec<TextureType> CODEC = Codec.STRING
            .comapFlatMap(
                    str -> {
                        var type = fromString(str);
                        return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown texture type: " + str);
                    },
                    TextureType::name
            );

    public static final StreamCodec<FriendlyByteBuf, TextureType> STREAM_CODEC = StreamCodec.of(
            (buf, val) -> buf.writeBoolean(val == HIGH_CONTRAST),
            buf -> buf.readBoolean() ? HIGH_CONTRAST : LOW_CONTRAST
    );
}
