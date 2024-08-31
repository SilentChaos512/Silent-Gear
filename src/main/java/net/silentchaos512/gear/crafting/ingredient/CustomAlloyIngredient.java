package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.setup.SgIngredientTypes;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class CustomAlloyIngredient implements ICustomIngredient {
    public static final MapCodec<CustomAlloyIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.flatXmap(
                    id -> Optional.of(BuiltInRegistries.ITEM.get(id))
                            .filter(item -> item instanceof CustomMaterialItem)
                            .map(item -> DataResult.success((CustomMaterialItem) item))
                            .orElseGet(() -> DataResult.error(() -> "Item is not a CustomMaterialItem: " + id)),
                    item -> DataResult.success(BuiltInRegistries.ITEM.getKey(item))
            ).fieldOf("item").forGetter(ing -> ing.item),
            DataResource.MATERIAL_CODEC.fieldOf("material").forGetter(ing -> ing.material)
    ).apply(instance, CustomAlloyIngredient::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CustomAlloyIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM), ingredient -> ingredient.item,
            DataResource.MATERIAL_STREAM_CODEC, ingredient -> ingredient.material,
            CustomAlloyIngredient::new
    );

    private final CustomMaterialItem item;
    private final DataResource<Material> material;

    private ItemStack[] itemStacks;

    protected CustomAlloyIngredient(Item item, DataResource<Material> material) {
        this.item = (CustomMaterialItem) item;
        this.material = material;
    }

    public static CustomAlloyIngredient of(CustomMaterialItem item, Material material) {
        return of(item, DataResource.material(material));
    }

    public static CustomAlloyIngredient of(CustomMaterialItem item, DataResource<Material> material) {
        return new CustomAlloyIngredient(item, material);
    }

    @Override
    public IngredientType<?> getType() {
        return SgIngredientTypes.CUSTOM_ALLOY.get();
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.getItem().equals(this.item)) return false;

        MaterialInstance material = CustomMaterialItem.getMaterial(stack);
        return material != null && material.getId().equals(this.material.getId());
    }

    private void dissolve() {
        if (this.itemStacks == null) {
            var itemValue = new Ingredient.ItemValue(item.create(MaterialInstance.of(this.material, ItemStack.EMPTY)));
            this.itemStacks = itemValue.getItems().toArray(new ItemStack[0]);
        }
    }

    @Override
    public Stream<ItemStack> getItems() {
        this.dissolve();
        return Arrays.stream(itemStacks);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
