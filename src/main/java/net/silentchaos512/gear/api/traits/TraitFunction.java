package net.silentchaos512.gear.api.traits;

@FunctionalInterface
public interface TraitFunction {
    float apply(TraitInstance trait, float value);
}
