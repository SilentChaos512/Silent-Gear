package net.silentchaos512.gear.api.property;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.CompoundMaterial;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Intended to hold a collection of variables needed for computing {@link GearProperty} values and reducing value lists.
 * The main class is abstract, with several subclasses that can be instantiated via static helper methods.
 * <p>
 * The {@link Gear} context contains the possibly unfinished gear {@link ItemStack} and is used when the gear item's
 * final property values are being calculated.
 * <p>
 * The {@link Part} context contains a {@link PartInstance} and is used when part properties are calculating.
 * <p>
 * The {@link Material} context contains a {@link MaterialInstance} and is used when working with materials. A
 * {@link PartType} must also be provided.
 * <p>
 * The {@link Empty} context contains no information and should only be used in rare cases.
 */
public abstract class ComputeContext {
    private final List<? extends GearComponentInstance<?>> components;
    PartType partType = PartTypes.NONE.get();
    GearType gearType = GearTypes.ALL.get();

    protected ComputeContext(List<? extends GearComponentInstance<?>> components) {
        this.components = ImmutableList.copyOf(components);
    }

    public static ComputeContext.Gear gear(ItemStack gear, List<PartInstance> parts) {
        var result = new ComputeContext.Gear(gear, parts);
        result.gearType = GearHelper.getType(gear);
        return result;
    }

    public static ComputeContext.Part part(PartInstance part, List<MaterialInstance> materials) {
        var result = new ComputeContext.Part(part, materials);
        result.partType = part.getType();
        result.gearType = part.getGearType();
        return result;
    }

    public static ComputeContext.Part part(PartInstance part) {
        return part(part, part.getMaterials());
    }

    public static ComputeContext.Material material(MaterialInstance material, PartType partType) {
        List<MaterialInstance> subMaterials = new ArrayList<>();
        if (material.isValid() && material.get() instanceof CompoundMaterial compoundMaterial) {
            subMaterials.addAll(compoundMaterial.getSubMaterials(material));
        }
        var result = new Material(material, subMaterials);
        result.partType = partType;
        return result;
    }

    public static ComputeContext from(GearComponentInstance<?> component, PartType partType) {
        if (component instanceof PartInstance partInstance) {
            return ComputeContext.part(partInstance);
        } else if (component instanceof MaterialInstance materialInstance) {
            return ComputeContext.material(materialInstance, partType);
        } else {
            throw new IllegalArgumentException("Unknown GearComponentInstanceType: " + component);
        }
    }

    public static ComputeContext.Empty empty() {
        return Empty.INSTANCE;
    }

    public List<? extends GearComponentInstance<?>> components() {
        return this.components;
    }

    public PartType partType() {
        return this.partType;
    }

    public GearType gearType() {
        return this.gearType;
    }

    public PartGearKey partGearKey() {
        return PartGearKey.of(this.gearType, this.partType);
    }

    public static class Gear extends ComputeContext {
        private final ItemStack gear;

        Gear(ItemStack gear, List<? extends GearComponentInstance<?>> components) {
            super(components);
            this.gear = gear;
        }

        public ItemStack gear() {
            return this.gear;
        }
    }

    public static class Part extends ComputeContext {
        private final PartInstance part;

        Part(PartInstance part, List<? extends GearComponentInstance<?>> components) {
            super(components);
            this.part = part;
        }

        public PartInstance part() {
            return this.part;
        }
    }

    public static class Material extends ComputeContext {
        private final MaterialInstance material;

        Material(MaterialInstance material, List<? extends GearComponentInstance<?>> components) {
            super(components);
            this.material = material;
        }

        public MaterialInstance material() {
            return this.material;
        }
    }

    public static class Empty extends ComputeContext {
        static final Empty INSTANCE = new Empty(List.of());

        Empty(List<? extends GearComponentInstance<?>> components) {
            super(components);
        }
    }
}
