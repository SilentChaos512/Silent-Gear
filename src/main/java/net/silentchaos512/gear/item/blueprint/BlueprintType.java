package net.silentchaos512.gear.item.blueprint;

public enum BlueprintType {
    BOTH, BLUEPRINT, TEMPLATE;

    public boolean allowBlueprint() {
        return this != TEMPLATE;
    }

    public boolean allowTemplate() {
        return this != BLUEPRINT;
    }
}
