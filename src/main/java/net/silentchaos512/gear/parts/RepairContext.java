package net.silentchaos512.gear.parts;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.config.Config;

public class RepairContext {
    public enum Type {
        QUICK,
        ANVIL;

        public float getEfficiency() {
            return this == QUICK
                    ? Config.Server.repairFactorQuick.get().floatValue()
                    : Config.Server.repairFactorAnvil.get().floatValue();
        }
    }

    private final Type type;
    private final ItemStack gear;
    private final PartData material;

    public RepairContext(Type type, ItemStack gear, PartData material) {
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

    public PartData getMaterial() {
        return material;
    }
}
