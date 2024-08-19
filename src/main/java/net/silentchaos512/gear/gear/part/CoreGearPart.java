package net.silentchaos512.gear.gear.part;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetPropertyModifiersEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.property.NumberPropertyValue;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.part.PartCraftingData;
import net.silentchaos512.gear.api.part.PartDisplayData;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CoreGearPart extends AbstractGearPart {
    protected final GearType gearType;
    protected final PartType partType;

    public CoreGearPart(
            GearType gearType,
            PartType partType,
            PartCraftingData crafting,
            PartDisplayData display,
            GearPropertyMap properties
    ) {
        super(crafting, display, properties);
        this.gearType = gearType;
        this.partType = partType;
    }

    @Override
    public PartType getType() {
        return this.partType;
    }

    @Override
    public GearType getGearType() {
        return gearType;
    }

    @Override
    public PartSerializer<?> getSerializer() {
        return PartSerializers.CORE.get();
    }

    @Override
    public List<MaterialInstance> getMaterials(PartInstance part) {
        return part.getItem().getOrDefault(SgDataComponents.MATERIAL_LIST, List.of());
    }

    @Nullable
    public MaterialInstance getPrimaryMaterial(PartInstance part) {
        var materials = getMaterials(part);
        return materials.isEmpty() ? null : materials.getFirst();
    }

    @Override
    public int getColor(PartInstance part, GearType gearType, int layer, int animationFrame) {
        MaterialInstance primaryMaterial = getPrimaryMaterial(part);
        return primaryMaterial != null ? primaryMaterial.getColor(gearType, partType) : Color.VALUE_WHITE;
    }

    @Override
    public Component getDisplayName(@Nullable PartInstance part) {
        if (part != null) {
            return part.getItem().getHoverName();
        }
        return super.getDisplayName(null);
    }

    @Override
    public Component getDisplayNamePrefix(@Nullable PartInstance part, ItemStack gear) {
        if (part != null) {
            MaterialInstance material = getPrimaryMaterial(part);
            if (material != null) {
                return material.getDisplayNamePrefix(partType);
            }
        }
        return super.getDisplayNamePrefix(part, gear);
    }

    @Override
    public Component getMaterialName(@Nullable PartInstance part, ItemStack gear) {
        if (part != null) {
            MaterialInstance material = getPrimaryMaterial(part);
            if (material != null) {
                return material.getDisplayName(partType, gear);
            }
        }
        return super.getMaterialName(null, gear);
    }

    @Override
    public String getModelKey(PartInstance part) {
        var primaryMaterial = getPrimaryMaterial(part);
        String str = "{" + (primaryMaterial != null ? primaryMaterial.getModelKey() : "null") + "}";
        return super.getModelKey(part) + str;
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(PartInstance part, PartType partType, PropertyKey<T, V> key) {
        var materials = getMaterials(part);
        List<V> mods = materials.stream()
                .flatMap(m -> m.getPropertyModifiers(partType, key).stream())
                .collect(Collectors.toList());

        // Get any base modifiers for this part (could be none)
        mods.addAll(this.properties.getValues(key));

        if (mods.isEmpty()) {
            // No modifiers for this stat, so doing anything else is pointless
            return mods;
        }

        GetPropertyModifiersEvent<T, V> event = new GetPropertyModifiersEvent<>(part, key, mods);
        NeoForge.EVENT_BUS.post(event);

        List<V> ret = new ArrayList<>(event.getModifiers());

        if (key.property() instanceof NumberProperty) {
            // Average together all number property modifiers of the same op
            for (NumberProperty.Operation op : NumberProperty.Operation.values()) {
                Collection<NumberPropertyValue> modsForOp = ret.stream()
                        .filter(val -> val instanceof NumberPropertyValue)
                        .map(val -> (NumberPropertyValue) val)
                        .filter(val -> val.operation() == op)
                        .collect(Collectors.toList());
                if (modsForOp.size() > 1) {
                    NumberPropertyValue mod = compressModifiers(modsForOp, op);
                    ret.removeIf(val -> ((NumberPropertyValue) val).operation() == op);
                    //noinspection unchecked
                    ret.add((V) mod);
                }
            }
        }

        return ret;
    }

    private static NumberPropertyValue compressModifiers(Collection<NumberPropertyValue> mods, NumberProperty.Operation operation) {
        // We do NOT want to average together max modifiers...
        if (operation == NumberProperty.Operation.MAX) {
            return mods.stream()
                    .max((o1, o2) -> Float.compare(o1.value(), o2.value()))
                    .orElse(new NumberPropertyValue(0, operation));
        }

        var weightedAverage = NumberProperty.getWeightedAverage(mods, operation);
        return new NumberPropertyValue(weightedAverage, operation);
    }

    @Override
    public Collection<TraitInstance> getTraits(PartInstance part, PartGearKey partKey) {
        var list = new ArrayList<>(super.getTraits(part, partKey));
        var materials = this.getMaterials(part);
        for (MaterialInstance material : materials) {
            list.addAll(
                    material.getTraits(partKey)
                            .stream()
                            .filter(traitInstance -> traitInstance.conditionsMatch(partKey, ItemStack.EMPTY, List.of(part)))
                            .toList()
            );
        }
        return list;
    }

    @Override
    public PartInstance randomizeData(GearType gearType, int tier) {
        for (ItemStack stack : this.getIngredient().getItems()) {
            if (stack.getItem() instanceof CompoundPartItem) {
                var material = getRandomMaterial(gearType);
                ItemStack craftingItem = ((CompoundPartItem) stack.getItem()).create(material);
                return PartInstance.of(this, craftingItem);
            }
        }
        return super.randomizeData(gearType, tier);
    }

    @Override
    public boolean canAddToGear(ItemStack gear, PartInstance part) {
        GearType type = GearHelper.getType(gear);
        return type.matches(this.gearType);
    }

    private MaterialInstance getRandomMaterial(GearType gearType) {
        // Excludes children, will select a random child material (if appropriate) below
        List<Material> allParentMaterials = SgRegistries.MATERIAL.getValues(false).stream()
                .map(MaterialInstance::of)
                .filter(m -> m.allowedInPart(this.partType) && m.isCraftingAllowed(this.partType, gearType))
                .map(MaterialInstance::get)
                .toList();

        if (!allParentMaterials.isEmpty()) {
            Material material = allParentMaterials.get(SilentGear.RANDOM.nextInt(allParentMaterials.size()));
            return getRandomChildMaterial(material);
        }

        // Something went wrong...
        return MaterialInstance.of(Const.Materials.EXAMPLE);
    }

    private static MaterialInstance getRandomChildMaterial(Material material) {
        // Selects a random child of the given material, or the material itself if it doesn't have any
        List<Material> children = SgRegistries.MATERIAL.getChildren(material);
        if (children.isEmpty()) {
            return MaterialInstance.of(material);
        }
        return MaterialInstance.of(children.get(SilentGear.RANDOM.nextInt(children.size())));
    }

    @Override
    public String toString() {
        return "CompoundPart{" +
                "id=" + SgRegistries.PART.getKey(this) +
                ", partType=" + partType +
                '}';
    }

    public static class Serializer extends PartSerializer<CoreGearPart> {
        private static final MapCodec<CoreGearPart> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        GearType.CODEC.fieldOf("gear_type").forGetter(p -> p.gearType),
                        PartType.CODEC.fieldOf("part_type").forGetter(p -> p.partType),
                        PartCraftingData.CODEC.fieldOf("crafting").forGetter(p -> p.crafting),
                        PartDisplayData.CODEC.fieldOf("display").forGetter(p -> p.display),
                        GearPropertyMap.CODEC.fieldOf("properties").forGetter(p -> p.properties)
                ).apply(instance, CoreGearPart::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, CoreGearPart> STREAM_CODEC = StreamCodec.composite(
                GearType.STREAM_CODEC, p -> p.gearType,
                PartType.STREAM_CODEC, p -> p.partType,
                PartCraftingData.STREAM_CODEC, p -> p.crafting,
                PartDisplayData.STREAM_CODEC, p -> p.display,
                GearPropertyMap.STREAM_CODEC, p -> p.properties,
                CoreGearPart::new
        );

        public Serializer() {
            super(CODEC, STREAM_CODEC);
        }
    }
}
