package net.silentchaos512.gear.api.data.part;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartCraftingData;
import net.silentchaos512.gear.api.part.PartDisplayData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.*;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public class PartBuilder {
    protected final ResourceLocation id;
    protected final GearType gearType;
    protected final PartType partType;
    protected PartCraftingData crafting;
    protected PartDisplayData display;
    protected final GearPropertyMap properties = new GearPropertyMap();

    public PartBuilder(ResourceLocation id, Supplier<GearType> gearType, Supplier<PartType> partType) {
        this.id = id;
        this.gearType = gearType.get();
        this.partType = partType.get();

        if (!this.gearType.isGear()) {
            throw new IllegalArgumentException("Part gear type must extend GearType.ALL");
        }
    }

    public ResourceLocation getId() {
        return id;
    }

    public GearType getGearType() {
        return this.gearType;
    }

    public PartBuilder crafting(PartCraftingData crafting) {
        this.crafting = crafting;
        return this;
    }

    public PartBuilder crafting(Ingredient ingredient) {
        return crafting(new PartCraftingData(ingredient, Collections.emptyList(), true));
    }

    public PartBuilder crafting(TagKey<Item> tag) {
        return crafting(new PartCraftingData(Ingredient.of(tag), Collections.emptyList(), true));
    }

    public PartBuilder crafting(ItemLike item) {
        return crafting(new PartCraftingData(Ingredient.of(item), Collections.emptyList(), true));
    }

    public PartBuilder display(PartDisplayData display) {
        this.display = display;
        return this;
    }

    public PartBuilder display(Component name) {
        return display(new PartDisplayData(name, Component.empty()));
    }

    public <T, V extends GearPropertyValue<T>> PartBuilder property(PropertyKey<T, V> key, V value) {
        this.properties.put(key, value);
        return this;
    }

    public <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> PartBuilder property(Supplier<P> property, V value) {
        return property(PropertyKey.of(property, GearTypes.ALL), value);
    }

    public PartBuilder numberProperty(Supplier<NumberProperty> property, float value) {
        return numberProperty(property, GearTypes.ALL, value, NumberProperty.Operation.AVERAGE);
    }

    public PartBuilder numberProperty(Supplier<NumberProperty> property, float value, NumberProperty.Operation operation) {
        return numberProperty(property, GearTypes.ALL, value, operation);
    }

    public PartBuilder numberProperty(Supplier<NumberProperty> property, Supplier<GearType> gearType, float value, NumberProperty.Operation operation) {
        return property(PropertyKey.of(property, gearType), new NumberPropertyValue(value, operation));
    }

    public PartBuilder traits(List<TraitInstance> traits) {
        TraitListPropertyValue value = new TraitListPropertyValue(traits);
        return property(PropertyKey.of(GearProperties.TRAITS, GearTypes.ALL), value);
    }

    public JsonElement serialize() {
        CoreGearPart part = new CoreGearPart(
                this.gearType,
                this.partType,
                Objects.requireNonNull(this.crafting),
                Objects.requireNonNull(this.display),
                this.properties
        );

        var serializer = PartSerializers.CORE.get();
        var json = serializer.codec().codec().encodeStart(JsonOps.INSTANCE, part).getOrThrow();
        var serializerId = Objects.requireNonNull(SgRegistries.PART_SERIALIZER.getKey(serializer));
        json.getAsJsonObject().addProperty("type", serializerId.toString());

        return json;
    }
}
