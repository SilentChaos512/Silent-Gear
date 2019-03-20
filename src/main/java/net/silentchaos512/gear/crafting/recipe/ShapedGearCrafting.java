package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ShapedGearCrafting extends ExtendedShapedRecipe {
    public static final ExtendedShapedRecipe.Serializer<ShapedGearCrafting> SERIALIZER = Serializer.basic(
            SilentGear.getId("shaped_gear_crafting"),
            ShapedGearCrafting::new
    );

    private final ICoreItem item;

    private ShapedGearCrafting(ShapedRecipe recipe) {
        super(recipe);

        ItemStack output = recipe.getRecipeOutput();
        if (!(output.getItem() instanceof ICoreItem)) {
            throw new JsonParseException("result is not a gear item: " + output);
        }
        this.item = (ICoreItem) output.getItem();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return this.getBaseRecipe().matches(inv, worldIn);
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        Collection<PartData> parts = StackList.from(inv).stream()
                .map(PartData::from)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return item.construct(parts);
    }
}
