package net.silentchaos512.gear.gear.material;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.IMaterialSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.item.CraftedMaterialItem;
import net.silentchaos512.gear.util.ModResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A material which has been modified in some way, such as pressing metals into sheets
 */
public class CraftedMaterial extends AbstractMaterial {
    public CraftedMaterial(ResourceLocation materialId, String packName) {
        super(materialId, packName);
    }

    public static IMaterialInstance getBaseMaterial(IMaterialInstance material) {
        return CraftedMaterialItem.getMaterial(material.getItem());
    }

    @Override
    public IMaterialSerializer<?> getSerializer() {
        return MaterialSerializers.CRAFTED;
    }

    @Override
    public Collection<IMaterialCategory> getCategories(IMaterialInstance material) {
        Collection<IMaterialCategory> set = super.getCategories(material);
        IMaterialInstance base = getBaseMaterial(material);
        set.addAll(base.getCategories());
        return set;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Set<PartType> getPartTypes(IMaterialInstance material) {
        return Collections.singleton(PartType.MAIN);
    }

    @Override
    public Collection<StatInstance> getStatModifiers(IMaterialInstance material, PartType partType, StatGearKey key, ItemStack gear) {
        Collection<StatInstance> ret = super.getStatModifiers(material, partType, key, gear);
        IMaterialInstance base = getBaseMaterial(material);
        ret.addAll(base.getStatModifiers(partType, key, gear));
        return ret;
    }

    @Override
    public Collection<StatGearKey> getStatKeys(IMaterialInstance material, PartType type) {
        Collection<StatGearKey> ret = new LinkedHashSet<>(super.getStatKeys(material, type));
        IMaterialInstance base = getBaseMaterial(material);
        ret.addAll(base.getStatKeys(type));
        return ret;
    }

    @Nullable
    @Override
    public IMaterialDisplay getDisplayOverride(IMaterialInstance material) {
        IMaterialInstance base = getBaseMaterial(material);
        return MaterialDisplayManager.get(base);
    }

    @Override
    public Collection<TraitInstance> getTraits(IMaterialInstance material, PartGearKey partKey, ItemStack gear) {
        Collection<TraitInstance> ret = super.getTraits(material, partKey, gear);
        IMaterialInstance base = getBaseMaterial(material);
        ret.addAll(base.getTraits(partKey, gear));
        return ret;
    }

    public static class Serializer extends AbstractMaterial.Serializer<CraftedMaterial> {
        public Serializer(ModResourceLocation id) {
            super(id, CraftedMaterial::new);
        }
    }

    @Override
    public ITextComponent getDisplayName(@Nullable IMaterialInstance material, PartType type, ItemStack gear) {
        if (material != null) {
            IMaterialInstance base = getBaseMaterial(material);
            if (!gear.isEmpty()) {
                return base.getDisplayName(type).plainCopy();
            } else {
                return material.getItem().getHoverName().plainCopy();
            }
        }
        return super.getDisplayName(null, type, gear);
    }

    @Override
    public int getNameColor(IMaterialInstance material, PartType partType, GearType gearType) {
        IMaterialInstance base = getBaseMaterial(material);
        return base.getNameColor(partType, gearType);
    }

    @Override
    public String getModelKey(IMaterialInstance material) {
        IMaterialInstance base = getBaseMaterial(material);
        return super.getModelKey(material) + "[" + base.getModelKey() + "]";
    }
}
