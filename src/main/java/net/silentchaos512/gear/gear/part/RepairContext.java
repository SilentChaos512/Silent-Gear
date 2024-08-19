package net.silentchaos512.gear.gear.part;

import net.minecraft.world.item.ItemStack;

public class RepairContext {
    public enum Type {
        QUICK,
        ANVIL;

        public float getBonusEfficiency() {
            return this == ANVIL ? 0.1f : 0f;
        }
    }

    private final Type type;
    private final ItemStack gear;
    private final PartInstance material;

    public RepairContext(Type type, ItemStack gear, PartInstance material) {
        this.type = type;
        this.gear = gear;
        this.material = material;
    }

    public Type getRepairType() {
        return type;
    }

    public ItemStack getGear() {
        return gear;
    }

    public PartInstance getMaterial() {
        return material;
    }
}
