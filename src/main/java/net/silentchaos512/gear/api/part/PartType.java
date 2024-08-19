package net.silentchaos512.gear.api.part;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public record PartType(
        boolean isRemovable,
        boolean isUpgrade,
        int maxPerItem,
        @Nullable Function<GearType, Optional<CompoundPartItem>> compoundParts
) {
    public static Codec<PartType> CODEC = CodecUtils.byModNameCodec(SgRegistries.PART_TYPE);
    public static StreamCodec<RegistryFriendlyByteBuf, PartType> STREAM_CODEC = ByteBufCodecs.registry(SgRegistries.PART_TYPE_KEY);

    private static final Map<PartGearKey, Optional<CompoundPartItem>> ITEM_CACHE = new HashMap<>();

    public PartType(Builder builder) {
        this(
                builder.isRemovable,
                builder.isUpgrade,
                builder.maxPerItem,
                builder.compoundPartItem
        );
    }

    public MutableComponent getDisplayName() {
        var name = SgRegistries.PART_TYPE.getKey(this);
        if (name == null) {
            return Component.literal("Unknown Part Type");
        }
        return Component.translatable("part." + name.getNamespace() + ".type." + name.getPath());
    }

    @SuppressWarnings("WeakerAccess")
    public ResourceLocation getCompoundPartId(GearType gearType) {
        return getCompoundPartItem(gearType)
                .map(NameUtils::fromItem)
                .orElseGet(() -> SilentGear.getId("invalid"));
    }

    public Optional<? extends CompoundPartItem> getCompoundPartItem(GearType gearType) {
        if (compoundParts == null) {
            return Optional.empty();
        }
        PartGearKey key = PartGearKey.of(gearType, this);
        return ITEM_CACHE.computeIfAbsent(key, gt -> compoundParts.apply(gearType));
    }

    public Optional<? extends PartInstance> makeCompoundPart(GearType gearType, DataResource<Material> material) {
        return makeCompoundPart(gearType, MaterialInstance.of(material));
    }

    public Optional<? extends PartInstance> makeCompoundPart(GearType gearType, MaterialInstance materials) {
        return getCompoundPartItem(gearType)
                .map(item -> {
                    ItemStack stack = item.create(materials);
                    return PartInstance.of(DataResource.part(this.getCompoundPartId(gearType)), stack);
                });
    }

    public static Optional<CompoundPartItem> getToolHeadItem(GearType gearType) {
        for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
            if (item instanceof MainPartItem) {
                var itemGearType = ((MainPartItem) item).getGearType();
                if (itemGearType.matches(gearType)) {
                    return Optional.of((CompoundPartItem) item);
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        private boolean isRemovable = false;
        private boolean isUpgrade = false;
        @Nullable private Function<GearType, Optional<CompoundPartItem>> compoundPartItem;
        private int maxPerItem = 1;

        private Builder() {}

        public static Builder builder() {
            return new Builder();
        }

        public Builder isRemovable(boolean value) {
            this.isRemovable = value;
            return this;
        }

        public Builder isUpgrade(boolean value) {
            this.isUpgrade = value;
            return this;
        }

        public Builder compoundPartItem(Supplier<CompoundPartItem> item) {
            return this.compoundPartItem(gt -> Optional.ofNullable(item.get()));
        }

        public Builder compoundPartItem(Function<GearType, Optional<CompoundPartItem>> itemGetter) {
            this.compoundPartItem = itemGetter;
            return this;
        }

        public Builder maxPerItem(int maxPerItem) {
            this.maxPerItem = maxPerItem;
            return this;
        }
    }
}
