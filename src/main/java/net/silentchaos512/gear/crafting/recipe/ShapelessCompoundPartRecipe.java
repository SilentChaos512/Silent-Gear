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
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShapelessCompoundPartRecipe extends ExtendedShapelessRecipe {
    public static final ResourceLocation NAME = SilentGear.getId("compound_part");
    public static final Serializer<ShapelessCompoundPartRecipe> SERIALIZER = Serializer.basic(ShapelessCompoundPartRecipe::new);

    private final CompoundPartItem item;

    protected ShapelessCompoundPartRecipe(ShapelessRecipe recipe) {
        super(recipe);

        ItemStack output = recipe.getRecipeOutput();
        if (!(output.getItem() instanceof CompoundPartItem)) {
            throw new JsonParseException("result is not a compound part item: " + output);
        }
        this.item = (CompoundPartItem) output.getItem();
    }

    protected GearType getGearType() {
        return GearType.TOOL;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if (!this.getBaseRecipe().matches(inv, worldIn)) return false;

        return getMaterials(inv).stream().allMatch(mat ->
                mat.getMaterial().isCraftingAllowed(mat, item.getPartType(), this.getGearType()));
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        int craftedCount = getBaseRecipe().getRecipeOutput().getCount();
        return item.create(getMaterials(inv), craftedCount);
    }

    private static List<MaterialInstance> getMaterials(IInventory inv) {
        return StackList.from(inv).stream()
                .map(stack -> stack.copy().split(1))
                .map(MaterialInstance::from)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public ItemStack getRecipeOutput() {
        // Create an example item, so we're not just showing a broken item
        int craftedCount = getBaseRecipe().getRecipeOutput().getCount();
        return item.create(Collections.singletonList(new LazyMaterialInstance(SilentGear.getId("example"))), craftedCount);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
