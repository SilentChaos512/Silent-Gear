package net.silentchaos512.gear.gear.part;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.lib.util.InventoryUtils;

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

    public static IPartData of(DataResource<IGearPart> part, ItemStack craftingItem) {
        if (part.isPresent())
            return of(part.get(), craftingItem);
        return LazyPartData.of(part, craftingItem);
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

    @Nullable
    public static PartData read(CompoundNBT tags) {
        ResourceLocation id = SilentGear.getIdWithDefaultNamespace(tags.getString(NBT_ID));
        if (id == null) return null;

        IGearPart part = PartManager.get(id);
        if (part == null) return null;

        ItemStack craftingItem = ItemStack.read(tags.getCompound("Item"));
        return of(part, craftingItem);
    }

    @Override
    public CompoundNBT write(@Nonnull CompoundNBT tags) {
        tags.putString(NBT_ID, part.getId().toString());

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

    public Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat) {
        return part.getStatModifiers(stat, this, this.getGearType(), gear);
    }

    public Collection<StatInstance> getStatModifiers(ItemStack gear, GearType gearType, ItemStat stat) {
        return part.getStatModifiers(stat, this, gearType, gear);
    }

    @Override
    public List<TraitInstance> getTraits() {
        return getTraits(ItemStack.EMPTY);
    }

    public List<TraitInstance> getTraits(ItemStack gear) {
        return part.getTraits(this, gear);
    }

    public List<MaterialInstance> getMaterials() {
        return this.part.getMaterials(this);
    }

    public boolean containsMaterial(DataResource<IMaterial> materialIn) {
        if (materialIn.isPresent()) {
            for (MaterialInstance mat : this.getMaterials()) {
                if (mat.getMaterial().equals(materialIn.get())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCraftingAllowed(GearType gearType) {
        return isCraftingAllowed(gearType, null);
    }

    public boolean isCraftingAllowed(GearType gearType, @Nullable CraftingInventory inventory) {
        return part.isCraftingAllowed(this, gearType, inventory);
    }

    public ITextComponent getDisplayName(ItemStack gear) {
        return part.getDisplayName(this, gear);
    }

    public ITextComponent getMaterialName(ItemStack gear) {
        return part.getMaterialName(this, gear);
    }

    @Deprecated
    public float getRepairAmount(ItemStack gear, RepairContext.Type type) {
        return part.getRepairAmount(new RepairContext(type, gear, this));
    }

    @Override
    public String getModelKey() {
        return part.getModelKey(this);
    }

    public int getColor(ItemStack gear) {
        return getColor(gear, 0, 0);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartData partData = (PartData) o;
        return part.equals(partData.part) &&
                InventoryUtils.canItemsStack(craftingItem, partData.craftingItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(part, craftingItem);
    }
}
