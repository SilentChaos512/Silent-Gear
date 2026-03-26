package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;

import java.util.Optional;
import java.util.stream.Collectors;

public final class ShapedGearRecipe extends ExtendedShapedRecipe implements IGearRecipe {
    private final GearItem item;
    private final Lazy<ItemStack> exampleOutput;

    public ShapedGearRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo, pattern, result);

        if (!(result.item().value() instanceof GearItem gearItem)) {
            throw new JsonParseException("result is not a gear item: " + result);
        }
        this.item = gearItem;

        this.exampleOutput = Lazy.of(() -> {
            // Create an example item, so we're not just showing a broken item
            var ingredients = this.pattern.ingredients().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            ItemStack exampleItem = item.construct(GearHelper.getExamplePartsFromRecipe(this.item.getGearType(), ingredients));
            GearData.setExampleTag(exampleItem, true);
            return exampleItem;
        });
    }

    @Override
    public RecipeSerializer<? extends ShapedGearRecipe> getSerializer() {
        return SgRecipes.SHAPED_GEAR.get();
    }

    @Override
    public boolean matches(CraftingInput input, Level worldIn) {
        if (!super.matches(input, worldIn)) return false;

        GearType gearType = item.getGearType();
        for (PartInstance part : getParts(input)) {
            if (!part.isCraftingAllowed(gearType, input)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return item.construct(getParts(input));
    }

    @Override
    public GearItem getOutputItem() {
        return item;
    }

    @Override
    public ItemStackTemplate getResultForDisplay() {
        return ItemStackTemplate.fromNonEmptyStack(exampleOutput.get());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
