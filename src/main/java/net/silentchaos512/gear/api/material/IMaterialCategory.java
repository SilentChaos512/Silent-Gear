package net.silentchaos512.gear.api.material;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

@FunctionalInterface
public interface IMaterialCategory {
    String getName();

    default Component getDisplayName() {
        return new TextComponent(getName()); // TODO: make translatable
    }

    default boolean matches(IMaterialCategory other) {
        return this.getName().equalsIgnoreCase(other.getName());
    }
}
