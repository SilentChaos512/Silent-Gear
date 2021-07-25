package net.silentchaos512.gear.gear.part;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.lib.util.InventoryUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class PartData implements IPartData { // TODO: move to api.part package
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
        return from(craftingItem, true);
    }

    @Nullable
    public static PartData from(ItemStack craftingItem, boolean checkSubstitutes) {
        IGearPart part = PartManager.from(craftingItem);
        if (part == null) {
            if (checkSubstitutes) {
                return fromMaterialSubstitute(craftingItem);
            } else {
                return null;
            }
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
                            return PartData.from(result, false);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static PartData read(CompoundTag tags) {
        ResourceLocation id = SilentGear.getIdWithDefaultNamespace(tags.getString(NBT_ID));
        if (id == null) return null;

        IGearPart part = PartManager.get(id);
        if (part == null) return null;

        ItemStack craftingItem = ItemStack.of(tags.getCompound("Item"));
        return of(part, craftingItem);
    }

    @Override
    public CompoundTag write(@Nonnull CompoundTag tags) {
        tags.putString(NBT_ID, part.getId().toString());

        CompoundTag itemTag = new CompoundTag();
        this.craftingItem.save(itemTag);
        tags.put("Item", itemTag);
        return tags;
    }

    @Override
    public ResourceLocation getId() {
        return part.getId();
    }

    @Nonnull
    @Override
    public IGearPart get() {
        return part;
    }

    @Override
    public ItemStack getItem() {
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

    @Override
    public Collection<StatInstance> getStatModifiers(PartType partType, StatGearKey key, ItemStack gear) {
        return part.getStatModifiers(this, this.getType(), key, gear);
    }

    public boolean isCraftingAllowed(GearType gearType, @Nullable CraftingContainer inventory) {
        return part.isCraftingAllowed(this, this.getType(), gearType, inventory);
    }

    @Override
    public Component getDisplayName(PartType type, ItemStack gear) {
        return part.getDisplayName(this, type, gear);
    }

    public Component getDisplayName(ItemStack gear) {
        return part.getDisplayName(this, gear);
    }

    public Component getMaterialName(ItemStack gear) {
        return part.getMaterialName(this, gear);
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
