package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CustomMaterialItem;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class CustomAlloyIngredient extends Ingredient {
    public static final Codec<CustomAlloyIngredient> CODEC = RecordCodecBuilder.<CustomAlloyIngredient>create(instance -> instance.group(
            ResourceLocation.CODEC.flatXmap(
                    id -> Optional.of(BuiltInRegistries.ITEM.get(id))
                            .filter(item -> item instanceof CustomMaterialItem)
                            .map(item -> DataResult.success((CustomMaterialItem) item))
                            .orElseGet(() -> DataResult.error(() -> "Item is not a CustomMaterialItem: " + id)),
                    item -> DataResult.success(BuiltInRegistries.ITEM.getKey(item))
            ).fieldOf("item").forGetter(ing -> ing.item),
            ResourceLocation.CODEC.fieldOf("material").forGetter(ing -> ing.material)
    ).apply(instance, CustomAlloyIngredient::new));

    private final CustomMaterialItem item;
    private final ResourceLocation material;

    protected CustomAlloyIngredient(CustomMaterialItem item, ResourceLocation materialId) {
        super(Stream.of(new ItemValue(item.create(LazyMaterialInstance.of(materialId)))));
        this.item = item;
        this.material = materialId;
    }

    public static CustomAlloyIngredient of(CustomMaterialItem item, IMaterial material) {
        return of(item, material.getId());
    }

    public static CustomAlloyIngredient of(CustomMaterialItem item, ResourceLocation materialId) {
        return new CustomAlloyIngredient(item, materialId);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.getItem().equals(this.item)) return false;

        MaterialInstance material = CustomMaterialItem.getMaterial(stack);
        return material != null && material.getId().equals(this.material);
    }
}
