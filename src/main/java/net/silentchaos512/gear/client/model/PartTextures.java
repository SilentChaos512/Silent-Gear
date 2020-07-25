package net.silentchaos512.gear.client.model;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.parts.PartType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PartTextures {
    HIGHLIGHT("_highlight", PartType.MISC_UPGRADE, GearType.TOOL, true),
    MAIN_GENERIC_LC("main_generic_lc", PartType.MAIN, GearType.TOOL, true),
    MAIN_GENERIC_HC("main_generic_hc", PartType.MAIN, GearType.TOOL, true),
    ROD_GENERIC_LC("rod_generic_lc", PartType.ROD, GearType.TOOL, true),
    ROD_GENERIC_HC("rod_generic_hc", PartType.ROD, GearType.TOOL, true),
    TIP_SHARP("tip_sharp", PartType.TIP, GearType.TOOL, true),
    TIP_SMOOTH("tip_smooth", PartType.TIP, GearType.TOOL, true),
    BINDING_GENERIC("binding_generic", PartType.BINDING, GearType.TOOL, true),
    GRIP_WOOL("grip_wool", PartType.GRIP, GearType.TOOL, true),
    BOWSTRING_STRING("bowstring_string", PartType.BOWSTRING, GearType.RANGED_WEAPON, true),
    ARROW("arrow", PartType.MISC_UPGRADE, GearType.RANGED_WEAPON, true),
    CHARGED_ARROW("charged_arrow", PartType.MISC_UPGRADE, GearType.CROSSBOW, false),
    CHARGED_FIREWORK("charged_firework", PartType.MISC_UPGRADE, GearType.CROSSBOW, false),
    FLETCHING_GENERIC("fletching_generic", PartType.MISC_UPGRADE, GearType.NONE, true);

    private final ResourceLocation texture;
    private final PartType partType;
    private final GearType gearType;
    private final boolean animated;

    PartTextures(String path, PartType partType, GearType gearType, boolean animated) {
        this.texture = SilentGear.getId(path);
        this.partType = partType;
        this.gearType = gearType;
        this.animated = animated;
    }

    public PartType getPartType() {
        return partType;
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
