package net.silentchaos512.gear.gear.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
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
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class PartInstance implements GearComponentInstance<GearPart> {
    public static final Codec<PartInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    DataResource.PART_CODEC.fieldOf("part").forGetter(p -> p.part),
                    ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(p -> p.craftingItem)
            ).apply(instance, PartInstance::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PartInstance> STREAM_CODEC = StreamCodec.composite(
            DataResource.PART_STREAM_CODEC, p -> p.part,
            ItemStack.STREAM_CODEC, p -> p.craftingItem,
            PartInstance::new
    );

    private static final Map<ResourceLocation, PartInstance> CACHE_UNGRADED_PARTS = new HashMap<>();

    private final DataResource<GearPart> part;
    private final ItemStack craftingItem;

    private PartInstance(DataResource<GearPart> part) {
        this(part, ItemStack.EMPTY);
    }

    private PartInstance(DataResource<GearPart> part, ItemStack craftingItem) {
        this.part = part;
        this.craftingItem = craftingItem.copy();
        if (!this.craftingItem.isEmpty()) {
            this.craftingItem.setCount(1);
        }
    }

    public static PartInstance of(DataResource<GearPart> part) {
        return new PartInstance(part);
    }

    public static PartInstance of(GearPart part) {
        return new PartInstance(DataResource.part(SgRegistries.PART.getKey(part)));
    }

    public static PartInstance of(DataResource<GearPart> part, ItemStack craftingItem) {
        return new PartInstance(part, craftingItem);
    }

    public static PartInstance of(GearPart part, ItemStack craftingItem) {
        return new PartInstance(DataResource.part(SgRegistries.PART.getKey(part)), craftingItem);
    }

    public static PartInstance create(DataResource<GearPart> part, CompoundPartItem item, DataResource<Material> material) {
        return create(part, item, List.of(MaterialInstance.of(material)));
    }

    public static PartInstance create(DataResource<GearPart> part, CompoundPartItem item, List<MaterialInstance> materials) {
        ItemStack partStack = new ItemStack(item);
        partStack.set(SgDataComponents.MATERIAL_LIST, materials);
        return new PartInstance(part, partStack);
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
        return of(part, craftingItem);
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

    @Override
    public ResourceLocation getId() {
        return part.getId();
    }

    @Nonnull
    @Override
    public GearPart get() {
        return part.get();
    }

    @Nullable
    public GearPart getNullable() {
        return part.getNullable();
    }

    @Override
    public ItemStack getItem() {
        return craftingItem;
    }

    public PartType getType() {
        return part.map(GearPart::getType).orElse(PartTypes.NONE.get());
    }

    public GearType getGearType() {
        return part.map(GearPart::getGearType).orElse(GearTypes.NONE.get());
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
    public <T, V extends GearPropertyValue<T>> T getProperty(PartType partType, PropertyKey<T, V> key) {
        return key.property().compute(key.property().getBaseValue(), getPropertyModifiers(partType, key));
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(PartType partType, PropertyKey<T, V> key) {
        var part = getNullable();
        return part != null ? part.getPropertyModifiers(this, partType, key) : Collections.emptyList();
    }

    @Override
    public Collection<TraitInstance> getTraits(PartGearKey key) {
        var part = getNullable();
        return part != null ? part.getTraits(this, key) : Collections.emptyList();
    }

    public boolean isCraftingAllowed(GearType gearType, @Nullable CraftingContainer inventory) {
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

    @Override
    public Component getDisplayName(PartType type, ItemStack gear) {
        var part = getNullable();
        return part != null ? part.getDisplayName(this, type) : Component.empty();
    }

    public String getModelKey() {
        var part = getNullable();
        return part != null ? part.getModelKey(this) : "null";
    }

    public int getColor(ItemStack gear) {
        return getColor(gear, 0, 0);
    }

    public int getColor(ItemStack gear, int layer, int animationFrame) {
        var part = getNullable();
        if (part != null) {
            var gearType = GearHelper.getType(gear);
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

    @Override
    public String toString() {
        return "PartData{" +
                this.part +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartInstance partData = (PartInstance) o;
        return part.equals(partData.part) &&
                ItemStack.isSameItemSameComponents(craftingItem, partData.craftingItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(part, craftingItem);
    }
}
