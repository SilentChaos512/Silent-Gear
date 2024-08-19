package net.silentchaos512.gear.gear.material;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.TextureType;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.RepairContext;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class MaterialInstance implements GearComponentInstance<Material> {
    public static final Codec<MaterialInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    DataResource.MATERIAL_CODEC.fieldOf("material").forGetter(m -> m.material),
                    ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(m -> m.item)
            ).apply(instance, MaterialInstance::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialInstance> STREAM_CODEC = StreamCodec.composite(
            DataResource.MATERIAL_STREAM_CODEC, m -> m.material,
            ItemStack.STREAM_CODEC, m -> m.item,
            MaterialInstance::new
    );

    private static final Map<ResourceLocation, MaterialInstance> QUICK_CACHE = new HashMap<>();

    private final DataResource<Material> material;
    private final ItemStack item;
    private ImmutableList<IMaterialModifier> modifiers = ImmutableList.of(); // Start empty, build when needed

    private MaterialInstance(DataResource<Material> material) {
        this(material, material.isPresent() ? material.get().getDisplayItem(PartTypes.MAIN.get(), 0) : ItemStack.EMPTY);
    }

    private MaterialInstance(DataResource<Material> material, ItemStack craftingItem) {
        this.material = material;
        this.item = craftingItem.copy();
        if (!this.item.isEmpty()) {
            this.item.setCount(1);
        }
    }

    public static MaterialInstance of(DataResource<Material> material) {
        return QUICK_CACHE.computeIfAbsent(material.getId(), id -> new MaterialInstance(material));
    }

    public static MaterialInstance of(Material material) {
        return of(DataResource.material(SgRegistries.MATERIAL.getKey(material)));
    }

    public static MaterialInstance of(DataResource<Material> material, ItemStack craftingItem) {
        return new MaterialInstance(material, craftingItem);
    }

    public static MaterialInstance of(Material material, ItemStack craftingItem) {
        return of(DataResource.material(SgRegistries.MATERIAL.getKey(material)), craftingItem);
    }

    @Nullable
    public static MaterialInstance from(ItemStack stack) {
        Material material = SgRegistries.MATERIAL.fromItem(stack);
        if (material != null) {
            return of(material, stack);
        }
        return null;
    }

    public ResourceLocation getId() {
        return material.getId();
    }

    @Nonnull
    public Material get() {
        return material.get();
    }

    @Nullable
    public Material getNullable() {
        return material.getNullable();
    }

    public Collection<IMaterialModifier> getModifiers() {
        if (modifiers.isEmpty()) {
            modifiers = ImmutableList.copyOf(MaterialModifiers.readFromMaterial(this));
        }
        return modifiers;
    }

    @Nullable
    public <T extends IMaterialModifier> T getModifier(IMaterialModifierType<T> modifierType) {
        for (var modifier : getModifiers()) {
            if (modifier.getType() == modifierType) {
                //noinspection unchecked
                return (T) modifier;
            }
        }
        return null;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    public Collection<IMaterialCategory> getCategories() {
        var mat = getNullable();
        return mat != null ? mat.getCategories(this) : Collections.emptySet();
    }

    public Ingredient getIngredient() {
        var mat = getNullable();
        return mat != null ? mat.getIngredient() : Ingredient.EMPTY;
    }

    public boolean canRepair(ItemStack gear) {
        if (!material.isPresent() || !material.get().isAllowedInPart(this, PartTypes.MAIN.get())) {
            return false;
        }

        var gearConstructionData = GearData.getConstruction(gear);
        var primaryPart = gearConstructionData.getPrimaryPart();
        if (primaryPart != null) {
            var partMaterial = primaryPart.getPrimaryMaterial();
            if (partMaterial != null) {
                return this.material.get().canRepair(partMaterial);
            }
        }

        return false;
    }

    public int getRepairValue(ItemStack gear) {
        return this.getRepairValue(gear, RepairContext.Type.QUICK);
    }

    public int getRepairValue(ItemStack gear, RepairContext.Type type) {
        if (this.canRepair(gear)) {
            float durability = getProperty(PartTypes.MAIN.get(), GearHelper.getDurabilityProperty(gear));
            float repairValueMulti = getProperty(PartTypes.MAIN.get(), GearProperties.REPAIR_VALUE.get());
            float itemRepairModifier = GearHelper.getRepairModifier(gear);
            float typeBonus = 1f + type.getBonusEfficiency();
            return Math.round(durability * repairValueMulti * itemRepairModifier * typeBonus) + 1;
        }
        return 0;
    }

    public int getColor(GearType gearType, PartType partType) {
        var mat = getNullable();
        return mat != null ? mat.getColor(this, partType, gearType) : Color.VALUE_WHITE;
    }

    public TextureType getMainTextureType() {
        var mat = getNullable();
        return mat != null ? mat.getMainTextureType(this) : TextureType.LOW_CONTRAST;
    }

    @Override
    public Component getDisplayName(PartType partType, ItemStack gear) {
        var mat = getNullable();
        return mat != null ? mat.getDisplayName(this, partType) : Component.literal(getId().toString());
    }

    public String getModelKey() {
        var mat = getNullable();
        return mat != null ? mat.getModelKey(this) : "null";
    }

    @Override
    public int getNameColor(PartType partType, GearType gearType) {
        var mat = getNullable();
        return mat != null ? mat.getNameColor(this, partType, gearType) : Color.VALUE_WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialInstance that = (MaterialInstance) o;
        return this.getId().equals(that.getId()) &&
                ItemStack.isSameItemSameComponents(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, item);
    }

    public boolean hasAnyCategory(Collection<IMaterialCategory> others) {
        for (IMaterialCategory cat1 : this.getCategories()) {
            for (IMaterialCategory cat2 : others) {
                if (cat1.matches(cat2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSimple() {
        Material mat = getNullable();
        return mat != null && mat.isSimple();
    }

    @Override
    public <T, V extends GearPropertyValue<T>> T getProperty(PartType partType, PropertyKey<T, V> key) {
        Material material = getNullable();
        if (material != null) {
            return material.getProperty(this, partType, key);
        }
        return key.property().getDefaultValue();
    }

    public Set<PartType> getPartTypes() {
        Material material = getNullable();
        if (material != null) {
            return material.getPartTypes(this);
        }
        return Collections.emptySet();
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(PartType partType, PropertyKey<T, V> key) {
        Material material = getNullable();
        if (material == null) {
            return Collections.emptyList();
        }
        return material.getPropertyModifiers(this, partType, key);
    }

    @Override
    public Collection<TraitInstance> getTraits(PartGearKey key) {
        Material material = getNullable();
        if (material == null) {
            return Collections.emptyList();
        }
        return material.getTraits(this, key);
    }

    public MutableComponent getDisplayNameWithModifiers(PartType partType, ItemStack gear) {
        MutableComponent name = getDisplayName(partType, gear).copy();
        for (IMaterialModifier modifier : getModifiers()) {
            name = modifier.modifyMaterialName(name);
        }
        return name;
    }

    public Component getDisplayNamePrefix(PartType partType) {
        var material = getNullable();
        return material != null ? material.getDisplayNamePrefix(partType) : Component.empty();
    }

    public boolean allowedInPart(PartType partType) {
        Material material = getNullable();
        return material != null && material.isAllowedInPart(this, partType);
    }

    public boolean isCraftingAllowed(PartType partType, GearType gearType) {
        Material material = getNullable();
        return material != null && material.isCraftingAllowed(this, partType, gearType);
    }

    public MaterialInstance onSalvage() {
        Material material = getNullable();
        return material != null ? material.onSalvage(this) : this;
    }
}
