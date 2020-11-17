package net.silentchaos512.gear.gear.part;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class FakePartData implements IPartData {
    private static final ResourceLocation FAKE_ID = SilentGear.getId("fake");
    private static final Map<PartType, FakePartData> CACHE = new HashMap<>();

    private final PartType type;

    public static FakePartData of(PartType type) {
        return CACHE.computeIfAbsent(type, FakePartData::new);
    }

    private FakePartData(PartType type) {
        this.type = type;
    }

    @Override
    public PartType getType() {
        return type;
    }

    @Override
    public GearType getGearType() {
        return GearType.ALL;
    }

    @Override
    public ResourceLocation getPartId() {
        return FAKE_ID;
    }

    @Nullable
    @Override
    public IGearPart getPart() {
        return null;
    }

    @Override
    public ItemStack getCraftingItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        return nbt;
    }

    @Override
    public String getModelKey() {
        return "fake_" + type.getName();
    }
}
