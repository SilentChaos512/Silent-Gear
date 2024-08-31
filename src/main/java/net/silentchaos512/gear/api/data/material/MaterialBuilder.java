package net.silentchaos512.gear.api.data.material;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.*;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.CustomCompoundMaterial;
import net.silentchaos512.gear.gear.material.ProcessedMaterial;
import net.silentchaos512.gear.gear.material.SimpleMaterial;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.util.Color;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings({"WeakerAccess", "OverlyComplexClass"})
public class MaterialBuilder<M extends Material> {
    private final ResourceLocation id;
    private final MaterialFactory<M> factory;
    private DataResource<Material> parent = DataResource.empty();
    private MaterialCraftingData crafting = null;
    private MaterialDisplayData display;
    private final Map<PartType, GearPropertyMap> properties = new LinkedHashMap<>();
    private final Map<PartType, List<TraitInstance>> traits = new LinkedHashMap<>();

    public MaterialBuilder(ResourceLocation id, MaterialFactory<M> factory) {
        this.id = id;
        this.factory = factory;
        this.display = new MaterialDisplayData(
                getDefaultTranslatedName(),
                Component.empty(),
                Color.WHITE,
                TextureType.HIGH_CONTRAST
        );
    }

    private MutableComponent getDefaultTranslatedName() {
        return Component.translatable(String.format("material.%s.%s",
                this.id.getNamespace(),
                this.id.getPath().replace("/", ".")));
    }

    public static MaterialBuilder<SimpleMaterial> simple(DataResource<Material> material) {
        return new MaterialBuilder<>(material.getId(), SimpleMaterial::new);
    }

    public static MaterialBuilder<CustomCompoundMaterial> customCompound(DataResource<Material> material) {
        return new MaterialBuilder<>(material.getId(), CustomCompoundMaterial::new);
    }

    public static MaterialBuilder<ProcessedMaterial> processed(DataResource<Material> material) {
        return new MaterialBuilder<>(material.getId(), ProcessedMaterial::new);
    }

    public ResourceLocation getId() {
        return id;
    }

    public MaterialBuilder<M> parent(DataResource<Material> parent) {
        this.parent = parent;
        return this;
    }

    public MaterialBuilder<M> crafting(ItemLike craftingItem, IMaterialCategory... categories) {
        return crafting(new MaterialCraftingData(
                Ingredient.of(craftingItem),
                Lists.newArrayList(categories),
                Collections.emptyList(),
                Collections.emptyMap(),
                true
        ));
    }

    public MaterialBuilder<M> crafting(TagKey<Item> craftingItem, IMaterialCategory... categories) {
        return crafting(new MaterialCraftingData(
                Ingredient.of(craftingItem),
                Lists.newArrayList(categories),
                Collections.emptyList(),
                Collections.emptyMap(),
                true
        ));
    }

    public MaterialBuilder<M> crafting(Ingredient ingredient, IMaterialCategory... categories) {
        return crafting(new MaterialCraftingData(
                ingredient,
                Lists.newArrayList(categories),
                Collections.emptyList(),
                Collections.emptyMap(),
                true
        ));
    }

    public MaterialBuilder<M> crafting(MaterialCraftingData crafting) {
        this.crafting = crafting;
        return this;
    }

    public MaterialBuilder<M> displayWithDefaultName(int color) {
        return displayWithDefaultName(Component.empty(), color, TextureType.HIGH_CONTRAST);
    }

    public MaterialBuilder<M> displayWithDefaultName(int color, TextureType textureType) {
        return displayWithDefaultName(Component.empty(), color, textureType);
    }

    public MaterialBuilder<M> displayWithDefaultName(Component namePrefix, int color, TextureType textureType) {
        return display(getDefaultTranslatedName(), namePrefix, color, textureType);
    }

    public MaterialBuilder<M> display(Component name, int color) {
        return display(name, Component.empty(), color, TextureType.HIGH_CONTRAST);
    }
    public MaterialBuilder<M> display(Component name, Component namePrefix, int color) {
        return display(name, namePrefix, color, TextureType.HIGH_CONTRAST);
    }

    public MaterialBuilder<M> display(Component name, int color, TextureType textureType) {
        return display(name, Component.empty(), color, textureType);
    }

    public MaterialBuilder<M> display(Component name, Component namePrefix, int color, TextureType textureType) {
        return display(new MaterialDisplayData(
                name,
                namePrefix,
                new Color(color),
                textureType
        ));
    }

    public MaterialBuilder<M> display(MaterialDisplayData display) {
        this.display = display;
        return this;
    }

    public MaterialBuilder<M> noProperties(Supplier<PartType> partType) {
        // Put an empty map for the part type, because the part type can only be supported if in the properties object
        properties.computeIfAbsent(partType.get(), pt -> new GearPropertyMap());
        return this;
    }

    public <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> MaterialBuilder<M> stat(Supplier<PartType> partType, Supplier<P> property, V value) {
        return stat(PartGearKey.ofAll(partType), property, value);
    }

    public <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> MaterialBuilder<M> stat(PartGearKey key, Supplier<P> property, V value) {
        GearPropertyMap map = this.properties.computeIfAbsent(key.partType(), pt -> new GearPropertyMap());
        map.put(property.get(), key.gearType(), value);
        return this;
    }

    public MaterialBuilder<M> stat(Supplier<PartType> partType, Supplier<NumberProperty> property, float value) {
        return stat(partType, property, NumberPropertyValue.average(value));
    }

    public MaterialBuilder<M> stat(Supplier<PartType> partType, Supplier<NumberProperty> property, float value, NumberProperty.Operation operation) {
        return stat(partType, property, new NumberPropertyValue(value, operation));
    }

    public MaterialBuilder<M> stat(PartGearKey key, Supplier<NumberProperty> property, float value) {
        return stat(key, property, NumberPropertyValue.average(value));
    }

    public MaterialBuilder<M> stat(PartGearKey key, Supplier<NumberProperty> property, float value, NumberProperty.Operation operation) {
        return stat(key, property, new NumberPropertyValue(value, operation));
    }

    @Deprecated
    public MaterialBuilder<M> mainStatsCommon(float toolDurability, float armorDurability, float enchantmentValue, float rarity) {
        stat(PartTypes.MAIN, GearProperties.DURABILITY, NumberPropertyValue.average(toolDurability));
        stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, NumberPropertyValue.average(armorDurability));
        stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, NumberPropertyValue.average(enchantmentValue));
        stat(PartTypes.MAIN, GearProperties.RARITY, NumberPropertyValue.average(rarity));
        return this;
    }

    public MaterialBuilder<M> mainStatsCommon(float toolDurability, float armorDurability, float enchantmentValue, float rarity, float chargeValue) {
        stat(PartTypes.MAIN, GearProperties.DURABILITY, NumberPropertyValue.average(toolDurability));
        stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, NumberPropertyValue.average(armorDurability));
        stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, NumberPropertyValue.average(enchantmentValue));
        stat(PartTypes.MAIN, GearProperties.RARITY, NumberPropertyValue.average(rarity));
        stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, NumberPropertyValue.average(chargeValue));
        return this;
    }

    public MaterialBuilder<M> mainStatsHarvest(Tier harvestTier, float harvestSpeed) {
        stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, new TierPropertyValue(harvestTier));
        stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, NumberPropertyValue.average(harvestSpeed));
        return this;
    }

    public MaterialBuilder<M> mainStatsMelee(float attackDamage, float magicDamage, float attackSpeed) {
        stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, NumberPropertyValue.average(attackDamage));
        if (magicDamage > 0) {
            stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, NumberPropertyValue.average(magicDamage));
        }
        stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, NumberPropertyValue.average(attackSpeed));
        return this;
    }

    public MaterialBuilder<M> mainStatsRanged(float rangedDamage, float rangedSpeed) {
        stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, NumberPropertyValue.average(rangedDamage));
        stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, NumberPropertyValue.average(rangedSpeed));
        return this;
    }

    public MaterialBuilder<M> mainStatsRanged(float rangedDamage, float rangedSpeed, float projectileSpeed, float projectileAccuracy) {
        stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, NumberPropertyValue.average(rangedDamage));
        stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, NumberPropertyValue.average(rangedSpeed));
        stat(PartTypes.MAIN, GearProperties.PROJECTILE_SPEED, NumberPropertyValue.average(projectileSpeed));
        stat(PartTypes.MAIN, GearProperties.PROJECTILE_ACCURACY, NumberPropertyValue.average(projectileAccuracy));
        return this;
    }

    public MaterialBuilder<M> mainStatsProjectile(float projectileSpeed, float projectileAccuracy) {
        stat(PartTypes.MAIN, GearProperties.PROJECTILE_SPEED, NumberPropertyValue.average(projectileSpeed));
        stat(PartTypes.MAIN, GearProperties.PROJECTILE_ACCURACY, NumberPropertyValue.average(projectileAccuracy));
        return this;
    }

    @Deprecated
    public MaterialBuilder<M> mainStatsArmor(float armor, float toughness, float magicArmor) {
        stat(PartTypes.MAIN, GearProperties.ARMOR, NumberPropertyValue.average(armor));
        stat(PartTypes.MAIN, GearProperties.ARMOR_TOUGHNESS, NumberPropertyValue.average(toughness));
        stat(PartTypes.MAIN, GearProperties.MAGIC_ARMOR, NumberPropertyValue.average(magicArmor));
        return this;
    }

    @SuppressWarnings({"MethodWithTooManyParameters", "OverlyComplexMethod"})
    public MaterialBuilder<M> mainStatsArmor(float head, float chest, float legs, float feet, float toughness, float magicArmor) {
        if (this.properties.get(PartTypes.MAIN.get()).containsKey(PropertyKey.of(GearProperties.ARMOR, GearTypes.ALL))) {
            throw new IllegalStateException("Called mainStatsArmor when armor stat is already defined");
        }

        if (head > 0 && chest > 0 && legs > 0 && feet > 0) {
            float sum = head + chest + legs + feet;
            stat(PartTypes.MAIN, GearProperties.ARMOR, NumberPropertyValue.average(sum));
        }

        if (head > 0)
            stat(PartGearKey.ofMain(GearTypes.HELMET), GearProperties.ARMOR, NumberPropertyValue.average(head));
        if (chest > 0)
            stat(PartGearKey.ofMain(GearTypes.CHESTPLATE), GearProperties.ARMOR, NumberPropertyValue.average(chest));
        if (legs > 0)
            stat(PartGearKey.ofMain(GearTypes.LEGGINGS), GearProperties.ARMOR, NumberPropertyValue.average(legs));
        if (feet > 0)
            stat(PartGearKey.ofMain(GearTypes.BOOTS), GearProperties.ARMOR, NumberPropertyValue.average(feet));
        if (toughness > 0)
            stat(PartTypes.MAIN, GearProperties.ARMOR_TOUGHNESS, NumberPropertyValue.average(toughness));
        if (magicArmor > 0)
            stat(PartTypes.MAIN, GearProperties.MAGIC_ARMOR, NumberPropertyValue.average(magicArmor));

        return this;
    }

    public MaterialBuilder<M> trait(Supplier<PartType> partType, DataResource<Trait> trait, int level, ITraitCondition... conditions) {
        TraitInstance inst = TraitInstance.of(trait, level, conditions);
        List<TraitInstance> list = traits.computeIfAbsent(partType.get(), pt -> new ArrayList<>());
        list.add(inst);
        return this;
    }

    private void validate() {
        if (this.crafting == null) {
            throw new IllegalStateException("Material has no crafting information: " + this.id);
        }
    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public JsonObject serialize() {
        SilentGear.LOGGER.info("Trying to serialize material \"{}\"", this.id);
        validate();

        this.traits.forEach(((partType, traitInstances) -> {
            var gearPropertyMap = this.properties.computeIfAbsent(partType, pt -> new GearPropertyMap());
            gearPropertyMap.put(GearProperties.TRAITS.get(), GearTypes.ALL.get(), new TraitListPropertyValue(traitInstances));
        }));

        M material = this.factory.create(
                this.parent,
                this.crafting,
                this.display,
                this.properties
        );

        //noinspection unchecked
        var codec = (MapCodec<M>) material.getSerializer().codec();
        var jsonElementDataResult = codec.codec().encodeStart(JsonOps.INSTANCE, material);
        if (jsonElementDataResult.isError()) {
            SilentGear.LOGGER.error("Something went wrong when serializing material \"{}\"", this.id);
        }

        var json = jsonElementDataResult.getOrThrow().getAsJsonObject();
        var serializerId = Objects.requireNonNull(SgRegistries.MATERIAL_SERIALIZER.getKey(material.getSerializer()));
        json.addProperty("type", serializerId.toString());
        return json;
    }

    public interface MaterialFactory<M extends Material> {
        M create(DataResource<Material> parent, MaterialCraftingData crafting, MaterialDisplayData display, Map<PartType, GearPropertyMap> properties);
    }
}
