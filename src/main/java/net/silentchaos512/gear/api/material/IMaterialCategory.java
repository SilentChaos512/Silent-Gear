package net.silentchaos512.gear.api.material;

@FunctionalInterface
public interface IMaterialCategory {
    String getName();

    default boolean matches(IMaterialCategory other) {
        return this.getName().equalsIgnoreCase(other.getName());
    }
}
