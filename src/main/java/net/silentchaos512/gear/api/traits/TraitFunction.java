package net.silentchaos512.gear.api.traits;

@FunctionalInterface
public interface TraitFunction {
    float apply(ITrait trait, int level, float value);
}
