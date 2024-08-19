package net.silentchaos512.gear.gear.material;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.NeoForge;
import net.silentchaos512.gear.api.event.GetMaterialPropertiesEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.item.CompoundMaterialItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.SynergyUtils;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CompoundMaterial extends AbstractMaterial {
    public CompoundMaterial(DataResource<Material> parent, MaterialCraftingData crafting, MaterialDisplayData display) {
        super(parent, crafting, display, Collections.emptyMap());
    }

    public List<MaterialInstance> getSubMaterials(MaterialInstance material) {
        return CompoundMaterialItem.getSubMaterials(material.getItem());
    }

    @Override
    public MaterialSerializer<?> getSerializer() {
        return MaterialSerializers.COMPOUND.get();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Nullable
    @Override
    public Material getParent() {
        return null;
    }

    @Override
    public Collection<IMaterialCategory> getCategories(MaterialInstance material) {
        Set<IMaterialCategory> set = new HashSet<>(this.crafting.categories());
        for (MaterialInstance mat : getSubMaterials(material)) {
            set.addAll(mat.getCategories());
        }
        return set;
    }

    @Override
    public Optional<Ingredient> getPartSubstitute(PartType partType) {
        return Optional.empty();
    }

    @Override
    public boolean hasPartSubstitutes() {
        return false;
    }

    @Override
    public boolean canSalvage() {
        return false;
    }

    @Override
    public Set<PartType> getPartTypes(MaterialInstance material) {
        List<MaterialInstance> subMaterials = getSubMaterials(material);
        if (subMaterials.isEmpty()) {
            return Collections.emptySet();
        } else if (subMaterials.size() == 1) {
            return subMaterials.getFirst().getPartTypes();
        }

        Set<PartType> set = new LinkedHashSet<>(subMaterials.getFirst().getPartTypes());
        for (int i = 1; i < subMaterials.size(); ++i) {
            Set<PartType> set1 = subMaterials.get(i).getPartTypes();
            Set<PartType> toRemove = new HashSet<>();
            for (PartType type : set) {
                if (!set1.contains(type)) {
                    toRemove.add(type);
                }
            }
            set.removeAll(toRemove);
        }

        return set;
    }

    @Override
    public boolean isAllowedInPart(MaterialInstance material, PartType partType) {
        return getPartTypes(material).contains(partType);
    }

    @Override
    public Collection<PropertyKey<?, ?>> getPropertyKeys(MaterialInstance material, PartType type) {
        return getSubMaterials(material).stream()
                .flatMap(mat -> mat.get().getPropertyKeys(mat, type).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(MaterialInstance material, PartType partType, PropertyKey<T, V> key) {
        // Get the materials and all the stat modifiers they provide for this stat
        var subMaterials = getSubMaterials(material);
        var propertyMods = subMaterials.stream()
                .map(AbstractMaterial::removeEnhancements)
                .flatMap(m -> m.getPropertyModifiers(partType, key).stream())
                .collect(Collectors.toList());

        if (propertyMods.isEmpty()) {
            // No modifiers for this stat, so doing anything else is pointless
            return propertyMods;
        }

        //noinspection unchecked
        var castedPropertyMods = (Collection<GearPropertyValue<?>>) propertyMods;
        var event = new GetMaterialPropertiesEvent(material, partType, key.property(), castedPropertyMods);
        NeoForge.EVENT_BUS.post(event);

        // Average together all modifiers of the same op. This makes things like rods with varying
        // numbers of materials more "sane".
        //noinspection unchecked
        final var modifiersFromEvent = (List<V>) new ArrayList<>(event.getModifiers());
        final var partGearKey = PartGearKey.ofAll(partType);
        final var compressedModifiers = key.property().compressModifiers(modifiersFromEvent, partGearKey, subMaterials);

        // Synergy
        final var modifiersWithSynergy = new ArrayList<V>();
        if (key.property().isAffectedBySynergy()) {
            final float synergy = SynergyUtils.getSynergy(partType, new ArrayList<>(subMaterials), getTraits(material, partGearKey));
            if (!MathUtils.floatsEqual(synergy, 1.0f)) {
                for (var mod : compressedModifiers) {
                    modifiersWithSynergy.add(key.property().applySynergy(mod, synergy));
                }
            }
        }

        return modifiersWithSynergy;
    }

    @Override
    public Collection<TraitInstance> getTraits(MaterialInstance material, PartGearKey partKey) {
        List<MaterialInstance> materials = new ArrayList<>(getSubMaterials(material));
        List<TraitInstance> traits = TraitHelper.getTraitsFromComponents(materials, partKey, ItemStack.EMPTY);
        Collection<TraitInstance> ret = new ArrayList<>();

        for (TraitInstance inst : traits) {
            if (inst.conditionsMatch(partKey, ItemStack.EMPTY, materials)) {
                ret.add(inst);
            }
        }

        return ret;
    }

    @Override
    public boolean isCraftingAllowed(MaterialInstance material, PartType partType, GearType gearType, @Nullable Container inventory) {
        if (!isAllowedInPart(material, partType)) {
            return false;
        }

        if (partType == PartTypes.MAIN.get()) {
            var key = PropertyKey.of(gearType.durabilityStat().get(), gearType);
            return !getPropertyModifiers(material, partType, key).isEmpty()
                    && getPropertyUnclamped(material, partType, key) > 0;
        }

        return true;
    }

    @Override
    public Component getDisplayName(@Nullable MaterialInstance material, PartType type) {
        if (material != null) {
            return material.getItem().getHoverName();
        }
        return this.display.name().copy();
    }

    @Override
    public int getColor(MaterialInstance material, PartType partType, GearType gearType) {
        // TODO: Might need to cache the computed color value somewhere...
        return ColorUtils.getBlendedColorForCompoundMaterial(getSubMaterials(material));
    }

    @Override
    public int getNameColor(MaterialInstance material, PartType partType, GearType gearType) {
        var color = getColor(material, partType, gearType);
        return Color.blend(color, Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
    }

    @Override
    public String getModelKey(MaterialInstance material) {
        var commaSeparatedMaterialList = getSubMaterials(material).stream()
                .map(MaterialInstance::getModelKey)
                .collect(Collectors.joining(","));
        return super.getModelKey(material) + "[" + commaSeparatedMaterialList + "]";
    }

    @Override
    public String toString() {
        return "CompoundMaterial{" +
                SgRegistries.MATERIAL.getKey(this) +
                '}';
    }

    public static final class Serializer extends MaterialSerializer<CompoundMaterial> {
        public static final MapCodec<CompoundMaterial> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        DataResource.MATERIAL_CODEC.fieldOf("parent").forGetter(m -> m.parent),
                        MaterialCraftingData.CODEC.fieldOf("crafting").forGetter(m -> m.crafting),
                        MaterialDisplayData.CODEC.fieldOf("display").forGetter(m -> m.display)
                ).apply(instance, CompoundMaterial::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CompoundMaterial> STREAM_CODEC = StreamCodec.composite(
                DataResource.MATERIAL_STREAM_CODEC, m -> m.parent,
                MaterialCraftingData.STREAM_CODEC, m -> m.crafting,
                MaterialDisplayData.STREAM_CODEC, m -> m.display,
                CompoundMaterial::new
        );

        public Serializer() {
            super(CODEC, STREAM_CODEC);
        }
    }
}
