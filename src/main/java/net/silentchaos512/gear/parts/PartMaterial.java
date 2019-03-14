package net.silentchaos512.gear.parts;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IPartMaterial;

import javax.annotation.Nullable;

/**
 * Represents the items that an {@link IGearPart} can be crafted from.
 */
public class PartMaterial implements IPartMaterial {
    IItemProvider item;
    IItemProvider itemSmall;
    Tag<Item> tag;
    Tag<Item> tagSmall;

    PartMaterial() {}

    @Override
    public boolean matches(IItemProvider input) {
        return item != null && item == input
                || tag != null && tag.contains(input.asItem());
    }

    // TODO: Should getters return optionals instead? Or are the getters needed?

    @Override
    @Nullable
    public IItemProvider getItem() {
        return item;
    }

    @Override
    @Nullable
    public IItemProvider getSmallItem() {
        return itemSmall;
    }

    @Override
    @Nullable
    public Tag<Item> getTag() {
        return tag;
    }

    @Override
    @Nullable
    public Tag<Item> getSmallTag() {
        return tagSmall;
    }

    ItemStack getItemStack() {
        IItemProvider normal = item;
        return normal != null ? new ItemStack(normal) : ItemStack.EMPTY;
    }

    ItemStack getSmallItemStack() {
        IItemProvider small = itemSmall;
        return small != null ? new ItemStack(small) : ItemStack.EMPTY;
    }

    public static PartMaterial read(PacketBuffer buf) {
        PartMaterial material = new PartMaterial();
        material.item = readItem(buf);
        material.itemSmall = readItem(buf);
        material.tag = readTag(buf);
        material.tagSmall = readTag(buf);
        return material;
    }

    @SuppressWarnings("ConstantConditions")
    public void write(PacketBuffer buf) {
        buf.writeString(this.item != null ? this.item.asItem().getRegistryName().toString() : "");
        buf.writeString(this.itemSmall != null ? this.itemSmall.asItem().getRegistryName().toString() : "");
        buf.writeString(this.tag != null ? this.tag.getId().toString() : "");
        buf.writeString(this.tagSmall != null ? this.tagSmall.getId().toString() : "");
    }

    @Nullable
    private static IItemProvider readItem(PacketBuffer buf) {
        String str = buf.readString(32767);
        if (str.isEmpty()) {
            // No value, this is acceptable
            return null;
        }

        ResourceLocation id = ResourceLocation.makeResourceLocation(str);
        if (id == null) {
            // Strange value
            SilentGear.LOGGER.warn("Received weird part material item string '{}' from server", str);
            return null;
        }

        return ForgeRegistries.ITEMS.getValue(id);
    }

    @Nullable
    private static Tag<Item> readTag(PacketBuffer buf) {
        String str = buf.readString(32767);
        if (str.isEmpty()) {
            // No value, this is acceptable
            return null;
        }

        ResourceLocation id = ResourceLocation.makeResourceLocation(str);
        if (id == null) {
            // Strange value
            SilentGear.LOGGER.warn("Received weird part material tag string '{}' from server", str);
            return null;
        }

        return ItemTags.getCollection().getOrCreate(id);
    }
}
