package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IPartMaterial;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.item.PartItem;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ShapelessCompoundPartRecipe extends ExtendedShapelessRecipe {
    public static final ResourceLocation NAME = SilentGear.getId("compound_part");
    public static final Serializer<ShapelessCompoundPartRecipe> SERIALIZER = Serializer.basic(ShapelessCompoundPartRecipe::new);

    private final PartItem item;

    private ShapelessCompoundPartRecipe(ShapelessRecipe recipe) {
        super(recipe);

        ItemStack output = recipe.getRecipeOutput();
        if (!(output.getItem() instanceof PartItem)) {
            throw new JsonParseException("result is not a compound part item: " + output);
        }
        this.item = (PartItem) output.getItem();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if (!this.getBaseRecipe().matches(inv, worldIn)) return false;

        return getMaterials(inv).stream().allMatch(mat -> mat.getMaterial().isCraftingAllowed(item.getPartType()));
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack result = item.create(getMaterials(inv));
        result.setCount(getBaseRecipe().getRecipeOutput().getCount());
        return result;
    }

    private static Collection<MaterialInstance> getMaterials(IInventory inv) {
        return StackList.from(inv).stream()
                .map(stack -> stack.copy().split(1))
                .map(MaterialInstance::from)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public ItemStack getRecipeOutput() {
        // Create an example item, so we're not just showing a broken item
        IPartMaterial material = MaterialManager.get(SilentGear.getId("example"));
        assert material != null;
        ItemStack result = item.create(Collections.singleton(MaterialInstance.of(material)));
        result.setCount(getBaseRecipe().getRecipeOutput().getCount());
//        GearData.setExampleTag(result, true);
        return result;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
