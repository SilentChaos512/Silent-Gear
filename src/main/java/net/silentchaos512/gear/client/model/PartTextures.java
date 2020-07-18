package net.silentchaos512.gear.client.model;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.MaterialLayer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PartTextures {
    HIGHLIGHT("_highlight", GearType.TOOL, true),
    MAIN_GENERIC_LC("main_generic_lc", GearType.TOOL, true),
    MAIN_GENERIC_HC("main_generic_hc", GearType.TOOL, true),
    ROD_GENERIC_LC("rod_generic_lc", GearType.TOOL, true),
    ROD_GENERIC_HC("rod_generic_hc", GearType.TOOL, true),
    TIP_IRON("tip_iron", GearType.TOOL, true),
    BINDING_GENERIC("binding_generic", GearType.TOOL, true),
    GRIP_WOOL("grip_wool", GearType.TOOL, true),
    BOWSTRING_STRING("bowstring_string", GearType.RANGED_WEAPON, true),
    ARROW("arrow", GearType.RANGED_WEAPON, true),
    CHARGED_ARROW("charged_arrow", GearType.CROSSBOW, false),
    CHARGED_FIREWORK("charged_firework", GearType.CROSSBOW, false),
    FLETCHING_GENERIC("fletching_generic", GearType.NONE, true);

    private final ResourceLocation texture;
    private final GearType gearType;
    private final boolean animated;

    PartTextures(String path, GearType gearType, boolean animated) {
        this.texture = SilentGear.getId(path);
        this.gearType = gearType;
        this.animated = animated;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public boolean isAnimated() {
        return animated;
    }

    public MaterialLayer getLayer(int color) {
        return new MaterialLayer(this, color);
    }

    public static List<PartTextures> getTextures(GearType gearType) {
        return Arrays.stream(values())
                .filter(t -> gearType.matches(t.gearType))
                .collect(Collectors.toList());
    }

    @Nullable
    public static PartTextures byTextureId(ResourceLocation tex) {
        for (PartTextures type : values()) {
            if (type.texture.equals(tex)) {
                return type;
            }
        }
        return null;
    }
}
