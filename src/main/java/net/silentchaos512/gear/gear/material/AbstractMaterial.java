package net.silentchaos512.gear.gear.material;

import com.google.common.collect.Sets;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.*;

public abstract class AbstractMaterial implements Material {
    String packName = "UNKNOWN PACK";
    protected final DataResource<Material> parent;

    protected final MaterialCraftingData crafting;
    protected final MaterialDisplayData display;
    protected final Map<PartType, GearPropertyMap> properties = new LinkedHashMap<>();

    protected AbstractMaterial(
            DataResource<Material> parent,
            MaterialCraftingData crafting,
            MaterialDisplayData display,
            Map<PartType, GearPropertyMap> properties
    ) {
        this.parent = parent;
        this.crafting = crafting;
        this.display = display;
        this.properties.putAll(properties);
    }

    @Override
    public String getPackName() {
        return packName;
    }

    @Nullable
    @Override
    public Material getParent() {
        return this.parent.getNullable();
    }

    @Override
    public Collection<IMaterialCategory> getCategories(MaterialInstance material) {
        var categories = this.crafting.categories();
        if (categories.isEmpty() && getParent() != null) {
            return getParent().getCategories(material);
        }
        return new HashSet<>(categories);
    }

    @Override
    public boolean isInCategory(IMaterialCategory category) {
        return this.crafting.categories().contains(category);
    }

    @Override
    public Ingredient getIngredient() {
        return crafting.craftingItem();
    }

    @Override
    public Optional<Ingredient> getPartSubstitute(PartType partType) {
        return Optional.ofNullable(this.crafting.partSubstitutes().get(partType));
    }

    @Override
    public boolean hasPartSubstitutes() {
        return !this.crafting.partSubstitutes().isEmpty();
    }

    @Override
    public boolean canSalvage() {
        return this.crafting.canSalvage();
    }

    @Override
    public MaterialInstance onSalvage(MaterialInstance material) {
        return removeEnhancements(material);
    }

    public static MaterialInstance removeEnhancements(MaterialInstance material) {
        ItemStack stack = material.getItem().copy();
        for (IMaterialModifierType<?> modifierType : SgRegistries.MATERIAL_MODIFIER_TYPE) {
            modifierType.removeModifier(stack);
        }
        stack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        Material iMaterial = material.get();
        if (iMaterial != null) {
            return MaterialInstance.of(iMaterial, stack);
        } else {
            return material;
        }
    }

    @Override
    public Set<PartType> getPartTypes(MaterialInstance material) {
        // Grab the part types from this part and its parent(s)
        return Sets.union(properties.keySet(), getParentOptional()
                .<Set<PartType>>map(m -> new LinkedHashSet<>(m.getPartTypes(material))).orElse(Collections.emptySet()));
    }

    @Override
    public boolean isAllowedInPart(MaterialInstance material, PartType partType) {
        return getPartTypes(material).contains(partType);
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(MaterialInstance instance, PartType partType, PropertyKey<T, V> key) {
        Collection<V> ret = new ArrayList<>(properties.getOrDefault(partType, GearPropertyMap.EMPTY).getValues(key));
        if (ret.isEmpty() && getParent() != null) {
            ret.addAll(getParent().getPropertyModifiers(instance, partType, key));
        }
        return ret;
    }

    @Override
    public Collection<PropertyKey<?, ?>> getPropertyKeys(MaterialInstance material, PartType type) {
        GearPropertyMap map = this.properties.getOrDefault(type, GearPropertyMap.EMPTY);
        if (map.isEmpty() && getParent() != null) {
            return getParent().getPropertyKeys(material, type);
        }
        return map.keySet();
    }

    @Override
    public Collection<TraitInstance> getTraits(MaterialInstance material, PartGearKey partKey) {
        var list = new ArrayList<>(Material.super.getTraits(material, partKey));
        if (list.isEmpty() && getParent() != null) {
            list.addAll(getParent().getTraits(material, partKey));
        }
        return list;
    }

    @Override
    public boolean isCraftingAllowed(MaterialInstance material, PartType partType, GearType gearType, @Nullable CraftingInput craftingInput) {
        if (isGearTypeBlacklisted(gearType) || !isAllowedInPart(material, partType)) {
            return false;
        }

        if (properties.containsKey(partType) || (getParent() != null && getParent().isCraftingAllowed(material, partType, gearType, craftingInput))) {
            if (partType == PartTypes.MAIN.get()) {
                var durabilityProperty = gearType.durabilityStat().get();
                var durabilityKey = PropertyKey.of(durabilityProperty, gearType);
                return !getPropertyModifiers(material, partType, durabilityKey).isEmpty() && getPropertyUnclamped(material, partType, durabilityKey) > 0;
            }
            return true;
        }
        return false;
    }

    private boolean isGearTypeBlacklisted(GearType gearType) {
        for (GearType gt : this.crafting.gearTypeBlacklist()) {
            if (gearType.matches(gt)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Component getDisplayName(@Nullable MaterialInstance material, PartType type) {
        return display.name().copy();
    }

    @Override
    public Component getDisplayNamePrefix(PartType partType) {
        return display.namePrefix().copy();
    }

    @Override
    public TextureType getMainTextureType(MaterialInstance material) {
        return display.mainTextureType();
    }

    @Override
    public int getColor(MaterialInstance material, PartType partType, GearType gearType) {
        return display.color().getColor();
    }

    @Override
    public int getNameColor(MaterialInstance material, PartType partType, GearType gearType) {
        int color = getColor(material, partType, gearType);
        return Color.blend(color, Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
    }

    @Override
    public String toString() {
        return "AbstractMaterial{" +
                "id=" + SgRegistries.MATERIAL.getKey(this) +
                '}';
    }
}
