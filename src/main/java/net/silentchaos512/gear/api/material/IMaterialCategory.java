package net.silentchaos512.gear.api.material;

import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.util.TextUtil;

@FunctionalInterface
public interface IMaterialCategory {
    String getName();

    default Component getDisplayName() {
        return TextUtil.translate("material.category", getName());
    }

    default boolean matches(IMaterialCategory other) {
        return this.getName().equalsIgnoreCase(other.getName());
    }
}
