package net.silentchaos512.gear.gear.part;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
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
    public ResourceLocation getId() {
        return FAKE_ID;
    }

    @Nullable
    @Override
    public IGearPart get() {
        return null;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public Collection<TraitInstance> getTraits(PartGearKey partKey, ItemStack gear) {
        return Collections.emptyList();
    }

    @Override
    public Component getDisplayName(PartType type, ItemStack gear) {
        return Component.literal("fake part");
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        return nbt;
    }

    @Override
    public String getModelKey() {
        return "fake_" + type.getName();
    }
}
