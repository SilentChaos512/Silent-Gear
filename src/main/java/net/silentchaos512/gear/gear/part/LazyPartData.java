package net.silentchaos512.gear.gear.part;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.item.CompoundPartItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A "lazy" version of {@link PartData}. Since {@link IGearPart}s may not exist when certain things
 * like loot tables are loaded, {@code LazyPartData} can be used to represent a future part.
 */
public class LazyPartData implements IPartData {
    private final ResourceLocation partId;
    private final ItemStack craftingItem;

    public LazyPartData(ResourceLocation partId) {
        this(partId, ItemStack.EMPTY);
    }

    public LazyPartData(ResourceLocation partId, ItemStack craftingItem) {
        this.partId = partId;
        this.craftingItem = craftingItem;
    }

    public static LazyPartData of(ResourceLocation partId) {
        return new LazyPartData(partId);
    }

    public static LazyPartData of(ResourceLocation partId, ItemStack craftingItem) {
        return new LazyPartData(partId, craftingItem);
    }

    public static LazyPartData of(DataResource<IGearPart> part, ItemStack craftingItem) {
        return new LazyPartData(part.getId(), craftingItem);
    }

    public static LazyPartData of(DataResource<IGearPart> part, CompoundPartItem partItem, List<LazyMaterialInstance> materials) {
        return new LazyPartData(part.getId(), partItem.create(materials));
    }

    public static LazyPartData of(DataResource<IGearPart> part, CompoundPartItem partItem, LazyMaterialInstance material) {
        return of(part, partItem, Collections.singletonList(material));
    }

    public static LazyPartData of(DataResource<IGearPart> part, CompoundPartItem partItem, DataResource<IMaterial> material) {
        return of(part, partItem, LazyMaterialInstance.of(material));
    }

    @Override
    public ResourceLocation getId() {
        return partId;
    }

    @Nullable
    @Override
    public IGearPart get() {
        return PartManager.get(partId);
    }

    @Override
    public ItemStack getItem() {
        if (!this.craftingItem.isEmpty()) {
            return this.craftingItem;
        }
        IGearPart part = get();
        if (part == null) {
            return ItemStack.EMPTY;
        }
        return PartData.of(part).getItem();
    }

    @Override
    public Component getDisplayName(PartType type, ItemStack gear) {
        IGearPart part = get();
        return part != null ? part.getDisplayName(this, type, gear) : Component.literal("INVALID");
    }

    @Override
    public CompoundTag write(CompoundTag tags) {
        tags.putString("ID", partId.toString());
        if (!this.craftingItem.isEmpty()) {
            tags.put("Item", this.craftingItem.save(new CompoundTag()));
        }
        return tags;
    }

    @Override
    public String getModelKey() {
        return SilentGear.shortenId(this.partId);
    }

    public boolean isValid() {
        return get() != null;
    }

    public static LazyPartData deserialize(JsonElement json) {
        if (json.isJsonPrimitive()) {
            String key = json.getAsString();
            return new LazyPartData(new ResourceLocation(key));
        }

        JsonObject jo = json.getAsJsonObject();
        ResourceLocation partId = new ResourceLocation(GsonHelper.getAsString(jo, "part"));

        if (!jo.has("item")) {
            return LazyPartData.of(partId);
        }

        Item item = GsonHelper.getAsItem(jo, "item");
        if (!(item instanceof CompoundPartItem)) {
            throw new JsonSyntaxException("Item " + item + " is not a compound part item. Try using \"part\" instead.");
        }

        if (!jo.has("materials") || !jo.get("materials").isJsonArray()) {
            throw new JsonSyntaxException("\"materials\" is either missing or not a JSON array");
        }
        List<LazyMaterialInstance> materials = deserializeMaterials(GsonHelper.getAsJsonArray(jo, "materials"));

        return LazyPartData.of(DataResource.part(partId), (CompoundPartItem) item, materials);

    }

    private static List<LazyMaterialInstance> deserializeMaterials(JsonArray json) {
        List<LazyMaterialInstance> materials = Lists.newArrayList();
        for (JsonElement je : json) {
            materials.add(LazyMaterialInstance.deserialize(je));
        }
        return materials;
    }

    public static List<PartData> createPartList(Collection<LazyPartData> parts) {
        List<PartData> list = new ArrayList<>();
        for (LazyPartData lazy : parts) {
            IGearPart gearPart = lazy.get();
            if (gearPart != null) {
                PartData part = PartData.of(gearPart, lazy.craftingItem);
                list.add(part);
            }
        }
        return list;
    }
}
