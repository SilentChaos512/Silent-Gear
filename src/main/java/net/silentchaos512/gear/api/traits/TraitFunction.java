package net.silentchaos512.gear.api.traits;

@FunctionalInterface
public interface TraitFunction<T> {
    T apply(TraitInstance trait, T value);
}
