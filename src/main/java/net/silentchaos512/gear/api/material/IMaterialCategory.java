package net.silentchaos512.gear.api.material;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

@FunctionalInterface
public interface IMaterialCategory {
    String getName();

    default ITextComponent getDisplayName() {
        return new StringTextComponent(getName()); // TODO: make translatable
    }

    default boolean matches(IMaterialCategory other) {
        return this.getName().equalsIgnoreCase(other.getName());
    }
}
