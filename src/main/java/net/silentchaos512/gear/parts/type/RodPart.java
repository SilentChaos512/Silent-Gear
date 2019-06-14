package net.silentchaos512.gear.parts.type;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.parts.AbstractGearPart;

public final class RodPart extends AbstractGearPart {
    public RodPart(ResourceLocation name) {
        super(name);
    }

    @Override
    public PartType getType() {
        return PartType.ROD;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.ROD;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartType.ROD.getSerializer();
    }

    @Override
    public StatInstance.Operation getDefaultStatOperation(ItemStat stat) {
        if (stat == CommonItemStats.HARVEST_LEVEL)
            return StatInstance.Operation.MAX;
        if (stat == CommonItemStats.ATTACK_SPEED || stat == CommonItemStats.RARITY)
            return StatInstance.Operation.ADD;
        return StatInstance.Operation.MUL2;
    }
}
