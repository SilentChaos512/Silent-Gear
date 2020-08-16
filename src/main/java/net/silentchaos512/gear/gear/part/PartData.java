package net.silentchaos512.gear.gear.part;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.util.DataResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class PartData implements IPartData {
    private static final Map<ResourceLocation, PartData> CACHE_UNGRADED_PARTS = new HashMap<>();
    public static final String NBT_ID = "ID";

    private final IGearPart part;
    private final ItemStack craftingItem;

    private PartData(IGearPart part) {
        this(part, ItemStack.EMPTY);
    }

    private PartData(IGearPart part, ItemStack craftingItem) {
        this.part = part;
        this.craftingItem = craftingItem.copy();
        if (!this.craftingItem.isEmpty()) {
            this.craftingItem.setCount(1);
        }
    }

    public static PartData of(IGearPart part) {
        ResourceLocation name = part.getId();
        if (CACHE_UNGRADED_PARTS.containsKey(name)) {
            return CACHE_UNGRADED_PARTS.get(name);
        }

        PartData inst = new PartData(part);
        CACHE_UNGRADED_PARTS.put(name, inst);
        return inst;
    }

    public static PartData of(IGearPart part, ItemStack craftingItem) {
        return new PartData(part, craftingItem);
    }

    @Deprecated
    public static PartData of(IGearPart part, MaterialGrade grade) {
        return new PartData(part);
    }

    @Deprecated
    public static PartData of(IGearPart part, MaterialGrade grade, ItemStack craftingItem) {
        return new PartData(part, craftingItem);
    }

    @Nullable
    public static PartData ofNullable(@Nullable IGearPart part) {
        if (part == null) return null;
        return of(part);
    }

    @Nullable
    public static PartData from(ItemStack craftingItem) {
        IGearPart part = PartManager.from(craftingItem);
        if (part == null) {
            return fromMaterialSubstitute(craftingItem);
        }
        return of(part, craftingItem);
    }

    @Nullable
    private static PartData fromMaterialSubstitute(ItemStack stack) {
        for (IMaterial material : MaterialManager.getValues()) {
            if (material.hasPartSubstitutes()) {
                for (PartType partType : PartType.getValues()) {
                    Optional<Ingredient> ingredient = material.getPartSubstitute(partType);

                    if (ingredient.isPresent() && ingredient.get().test(stack)) {
                        Optional<? extends CompoundPartItem> item = partType.getCompoundPartItem(GearType.PART);

                        if (item.isPresent()) {
                            ItemStack result = item.get().create(MaterialInstance.of(material));
                            return PartData.from(result);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Deprecated
    @Nullable
    public static PartData fromStackFast(ItemStack craftingItem) {
        return ofNullable(PartManager.from(craftingItem));
    }

    @Nullable
    public static PartData fromId(ResourceLocation partId) {
        return ofNullable(PartManager.get(partId));
    }

    @Nullable
    public static PartData read(CompoundNBT tags) {
        String key = tags.getString(NBT_ID);
        IGearPart part = PartManager.get(new ResourceLocation(key));
        if (part == null) return null;

        MaterialGrade grade = MaterialGrade.fromString(tags.getString("Grade"));
        ItemStack craftingItem = ItemStack.read(tags.getCompound("Item"));
        return of(part, grade, craftingItem);
    }

    @Nullable
    public static PartData readFast(CompoundNBT tags) {
        String key = tags.getString(NBT_ID);
        if (key.isEmpty()) return null;
        IGearPart part = PartManager.get(new ResourceLocation(key));
        if (part == null) return null;
        return of(part);
    }

    @Override
    public CompoundNBT write(@Nonnull CompoundNBT tags) {
        tags.putString("ID", part.getId().toString());

        CompoundNBT itemTag = new CompoundNBT();
        this.craftingItem.write(itemTag);
        tags.put("Item", itemTag);
        return tags;
    }

    @Override
    public ResourceLocation getPartId() {
        return part.getId();
    }

    @Nonnull
    @Override
    public IGearPart getPart() {
        return part;
    }

    @Override
    public ItemStack getCraftingItem() {
        return craftingItem;
    }

    @Override
    public int getTier() {
        return part.getTier(this);
    }

    @Nonnull
    @Override
    public PartType getType() {
        return part.getType();
    }

    @Override
    public GearType getGearType() {
        return part.getGearType();
    }

    public float computeStat(ItemStat stat) {
        return part.computeStatValue(stat, this);
    }

    public Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat) {
        return part.getStatModifiers(gear, stat, this);
    }

    @Override
    public List<TraitInstance> getTraits() {
        return getTraits(ItemStack.EMPTY);
    }

    public List<TraitInstance> getTraits(ItemStack gear) {
        return part.getTraits(gear, this);
    }

    public boolean isCraftingAllowed(@Nullable GearType gearType) {
        return isCraftingAllowed(gearType, null);
    }

    public boolean isCraftingAllowed(@Nullable GearType gearType, @Nullable CraftingInventory inventory) {
        return part.isCraftingAllowed(this, gearType, inventory);
    }

    public boolean containsMaterial(DataResource<IMaterial> material) {
        if (this.part instanceof CompoundPart) {
            Optional<MaterialInstance> mat = material.map(MaterialInstance::of);
            if (mat.isPresent()) {
                return CompoundPart.getMaterials(this).contains(mat.get());
            }
        }

        return false;
    }

    public ITextComponent getDisplayName(ItemStack gear) {
        return part.getDisplayName(this, gear);
    }

    public ITextComponent getMaterialName(ItemStack gear) {
        return part.getMaterialName(this, gear);
    }

    public float getRepairAmount(ItemStack gear, RepairContext.Type type) {
        return part.getRepairAmount(new RepairContext(type, gear, this));
    }

    public String getModelKey() {
        return part.getModelKey(this);
    }

    public int getColor(ItemStack gear, int layer, int animationFrame) {
        return part.getColor(this, gear, layer, animationFrame);
    }

    @Override
    public void onAddToGear(ItemStack gear) {
        this.part.onAddToGear(gear, this);
    }

    public void onRemoveFromGear(ItemStack gear) {
        this.part.onRemoveFromGear(gear, this);
    }

    @Override
    public String toString() {
        return "PartData{" +
                this.part +
                "}";
    }
}
