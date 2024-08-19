package net.silentchaos512.gear.item.blueprint;

public enum BlueprintType {
    BLUEPRINT, TEMPLATE;

    public enum ConfigOption {
        BOTH, BLUEPRINT, TEMPLATE;

        public boolean allowBlueprint() {
            return this != TEMPLATE;
        }

        public boolean allowTemplate() {
            return this != BLUEPRINT;
        }
    }
}
