package net.silentchaos512.gear.gear.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.ComputeContext;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.ItemHelper;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.*;

public final class PartInstance implements GearComponentInstance<GearPart> {
    public static final Codec<PartInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    DataResource.PART_CODEC.fieldOf("part").forGetter(p -> p.part),
                    ItemStackTemplate.CODEC.optionalFieldOf("item").forGetter(p -> Optional.ofNullable(p.craftingItem))
            ).apply(instance, PartInstance::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PartInstance> STREAM_CODEC = StreamCodec.composite(
            DataResource.PART_STREAM_CODEC, p -> p.part,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), p -> Optional.ofNullable(p.craftingItem),
            PartInstance::new
    );

    private final DataResource<GearPart> part;
    @Nullable private final ItemStackTemplate craftingItem;

    private PartInstance(DataResource<GearPart> part) {
        this(part, (ItemStackTemplate) null);
    }

    private PartInstance(DataResource<GearPart> part, @Nullable ItemStackTemplate craftingItem) {
        this.part = part;
        this.craftingItem = craftingItem != null ? craftingItem.withCount(1) : null;
    }

    /** @noinspection OptionalUsedAsFieldOrParameterType*/
    private PartInstance(DataResource<GearPart> part, Optional<ItemStackTemplate> craftingItem) {
        this(part, craftingItem.orElse(null));
    }

    public static PartInstance of(DataResource<GearPart> part) {
        return new PartInstance(part);
    }

    public static PartInstance of(GearPart part) {
        return new PartInstance(DataResource.part(SgRegistries.PART.getKey(part)));
    }

    public static PartInstance of(DataResource<GearPart> part, @Nullable ItemInstance craftingItem) {
        return new PartInstance(part, ItemHelper.toTemplate(craftingItem));
    }

    public static PartInstance of(GearPart part, @Nullable ItemInstance craftingItem) {
        return new PartInstance(DataResource.part(SgRegistries.PART.getKey(part)), ItemHelper.toTemplate(craftingItem));
    }

    public static PartInstance create(DataResource<GearPart> part, CompoundPartItem item, DataResource<Material> material) {
        return create(part, item, List.of(MaterialInstance.of(material)));
    }

    public static PartInstance create(DataResource<GearPart> part, CompoundPartItem item, List<MaterialInstance> materials) {
        var patch = DataComponentPatch.builder().set(SgDataComponents.MATERIAL_LIST.get(), materials).build();
        var template = new ItemStackTemplate(item, patch);
        return new PartInstance(part, template);
    }

    @Nullable
    public static PartInstance from(ItemStack craftingItem) {
        return from(craftingItem, true);
    }

    @Nullable
    public static PartInstance from(ItemStack craftingItem, boolean checkSubstitutes) {
        GearPart part = SgRegistries.PART.fromItem(craftingItem);
        if (part == null) {
            if (checkSubstitutes) {
                return fromMaterialSubstitute(craftingItem);
            } else {
                return null;
            }
        }
        if (!craftingItem.isEmpty()) {
            return of(part, ItemStackTemplate.fromNonEmptyStack(craftingItem));
        }
        return of(part);
    }

    @Nullable
    private static PartInstance fromMaterialSubstitute(ItemStack stack) {
        for (Material material : SgRegistries.MATERIAL.getValues(true)) {
            if (material.hasPartSubstitutes()) {
                for (PartType partType : SgRegistries.PART_TYPE) {
                    Optional<Ingredient> ingredient = material.getPartSubstitute(partType);

                    if (ingredient.isPresent() && ingredient.get().test(stack)) {
                        Optional<? extends CompoundPartItem> item = partType.getCompoundPartItem(GearTypes.ALL.get());

                        if (item.isPresent()) {
                            ItemStack result = item.get().create(MaterialInstance.of(material));
                            return PartInstance.from(result, false);
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean isValid() {
        return this.part.isPresent();
    }

    @Override
    public Identifier getId() {
        return part.getId();
    }

    @Override
    public GearPart get() {
        return part.get();
    }

    @Nullable
    public GearPart getNullable() {
        return part.getNullable();
    }

    @Override
    public @Nullable ItemStackTemplate getItem() {
        return craftingItem;
    }

    public @Nullable ItemStackTemplate getSalvageItem() {
        return this.getSalvageItem(this.getType());
    }

    public PartType getType() {
        return part.map(GearPart::getType).orElse(PartTypes.NONE.get());
    }

    public GearType getGearType() {
        return part.map(GearPart::getGearType).orElse(GearTypes.NONE.get());
    }

    public PartGearKey getKey() {
        return PartGearKey.of(this.getGearType(), this.getType());
    }

    public List<MaterialInstance> getMaterials() {
        var part = getNullable();
        return part != null ? part.getMaterials(this) : List.of();
    }

    @Nullable
    public MaterialInstance getPrimaryMaterial() {
        var part = getNullable();
        return part != null ? part.getPrimaryMaterial(this) : null;
    }

    @Override
    public boolean is(DataResource<GearPart> resource) {
        return this.part.equals(resource);
    }

    @Override
    public <T, V extends GearPropertyValue<T>> T getProperty(PartType partType, PropertyKey<T, V> key) {
        return key.property().compute(ComputeContext.part(this, this.getMaterials()), key.property().getBaseValue(), getPropertyModifiers(partType, key));
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(PartType partType, PropertyKey<T, V> key) {
        var part = getNullable();
        return part != null ? part.getPropertyModifiers(this, partType, key) : Collections.emptyList();
    }

    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(PropertyKey<T, V> key) {
        return this.getPropertyModifiers(this.getType(), key);
    }

    public boolean isCraftingAllowed(GearType gearType, @Nullable CraftingInput inventory) {
        var part = getNullable();
        return part != null && part.isCraftingAllowed(this, this.getType(), gearType, inventory);
    }

    public Component getMaterialName(ItemStack gear) {
        var part = getNullable();
        return part != null ? part.getMaterialName(this, gear) : Component.empty();
    }

    @Override
    public int getNameColor(PartType partType, GearType gearType) {
        return Color.VALUE_WHITE;
    }

    public Component getDisplayName() {
        return getDisplayName(this.getType());
    }

    @Override
    public Component getDisplayName(PartType type, ItemStack gear) {
        var part = getNullable();
        return part != null ? part.getDisplayName(this, type) : Component.empty();
    }

    public int getColor(ItemStack gear) {
        return getColor(gear, 0, 0);
    }

    public int getColor(ItemStack gear, int layer, int animationFrame) {
        return getColor(GearHelper.getType(gear), layer, animationFrame);
    }

    public int getColor(GearType gearType, int layer, int animationFrame) {
        var part = getNullable();
        if (this.craftingItem != null && this.craftingItem.has(SgDataComponents.PAINT_COLOR)) {
            return this.craftingItem.getOrDefault(SgDataComponents.PAINT_COLOR, 0);
        }
        if (part != null) {
            return part.getColor(this, gearType, layer, animationFrame);
        }
        return Color.VALUE_WHITE;
    }

    public void onAddToGear(ItemStack gear) {
        if (this.part.isPresent()) {
            this.part.get().onAddToGear(gear, this);
        }
    }

    public void onRemoveFromGear(ItemStack gear) {
        if (this.part.isPresent()) {
            this.part.get().onRemoveFromGear(gear, this);
        }
    }

    public void addInformation(ItemStack gear, List<Component> tooltip, TooltipFlag flag) {
        if (this.part.isPresent()) {
            this.part.get().addInformation(this, gear, tooltip, flag);
        }
    }

    @Override
    public String toString() {
        return "PartInstance{" +
                this.part +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartInstance otherPart = (PartInstance) o;
        return this.part.equals(otherPart.part) && ItemHelper.equals(craftingItem, otherPart.craftingItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(part, ItemHelper.hashItemAndComponents(craftingItem));
    }
}
