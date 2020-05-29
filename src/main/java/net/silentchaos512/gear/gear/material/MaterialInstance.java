package net.silentchaos512.gear.gear.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.IPartMaterial;
import net.silentchaos512.gear.api.parts.PartType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MaterialInstance implements IMaterialInstance {
    private static final Map<ResourceLocation, MaterialInstance> QUICK_CACHE = new HashMap<>();

    private final IPartMaterial material;
    private final ItemStack item;

    private MaterialInstance(IPartMaterial material) {
        this(material, material.getDisplayItem(PartType.MAIN, 0));
    }

    private MaterialInstance(IPartMaterial material, ItemStack craftingItem) {
        this.material = material;
        this.item = craftingItem;
    }

    public static MaterialInstance of(IPartMaterial material) {
        return QUICK_CACHE.computeIfAbsent(material.getId(), id -> new MaterialInstance(material));
    }

    public static MaterialInstance of(IPartMaterial material, ItemStack craftingItem) {
        return new MaterialInstance(material, craftingItem);
    }

    @Override
    public ResourceLocation getMaterialId() {
        return material.getId();
    }

    @Nonnull
    @Override
    public IPartMaterial getMaterial() {
        return material;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public static MaterialInstance read(CompoundNBT nbt) {
        ResourceLocation id = ResourceLocation.tryCreate(nbt.getString("ID"));
        IPartMaterial material = MaterialManager.get(id);
        if (material == null) return null;

        ItemStack stack = ItemStack.read(nbt.getCompound("Item"));
        return of(material, stack);
    }

    @Nullable
    public static MaterialInstance readFast(CompoundNBT nbt) {
        ResourceLocation id = ResourceLocation.tryCreate(nbt.getString("ID"));
        IPartMaterial material = MaterialManager.get(id);
        if (material == null) return null;

        return of(material);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putString("ID", material.getId().toString());
        nbt.put("Item", item.write(new CompoundNBT()));
        return nbt;
    }
}
