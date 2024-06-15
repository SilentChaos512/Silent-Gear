package net.silentchaos512.gear.gear.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    public static final Codec<LazyPartData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("part").forGetter(lp -> lp.partId),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(lp -> lp.craftingItem),
                    Codec.list(LazyMaterialInstance.CODEC).fieldOf("materials").forGetter(lp -> lp.materials)
            ).apply(instance, LazyPartData::new)
    );

    private final ResourceLocation partId;
    private final Item craftingItem;
    private final List<LazyMaterialInstance> materials;

    public LazyPartData(ResourceLocation partId) {
        this(partId, ItemStack.EMPTY);
    }

    public LazyPartData(ResourceLocation partId, ItemStack craftingItem) {
        this(partId, craftingItem.getItem(), Collections.emptyList());
    }

    public LazyPartData(ResourceLocation partId, Item craftingItem, List<LazyMaterialInstance> materials) {
        this.partId = partId;
        this.craftingItem = craftingItem;
        this.materials = materials.stream().toList();
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
        if (this.craftingItem instanceof CompoundPartItem compoundPartItem) {
            return compoundPartItem.create(this.materials);
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
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            tags.put("Item", stack.save(new CompoundTag()));
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

    public static List<PartData> createPartList(Collection<LazyPartData> parts) {
        List<PartData> list = new ArrayList<>();
        for (LazyPartData lazy : parts) {
            IGearPart gearPart = lazy.get();
            if (gearPart != null) {
                PartData part = PartData.of(gearPart, lazy.getItem());
                list.add(part);
            }
        }
        return list;
    }
}
