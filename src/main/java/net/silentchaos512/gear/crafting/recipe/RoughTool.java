/*
 * Silent Gear -- RoughTool
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.crafting.recipe;

public class RoughTool /*implements IRecipeFactory*/ {
    /*
    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
        ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

        CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
        primer.width = recipe.getRecipeWidth();
        primer.height = recipe.getRecipeHeight();
        primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
        primer.input = recipe.getIngredients();

        JsonObject resultObj = json.getAsJsonObject("result");
        Item item = Item.getByNameOrId(JsonUtils.getString(resultObj, "item", ""));

        if (item == null)
            throw new JsonSyntaxException("item does not exist");
        if (!(item instanceof ICoreItem))
            throw new JsonSyntaxException("item is not a gear item");

        ItemStack result = new ItemStack(item);
        ShapedOreRecipe baseRecipe = new ShapedOreRecipe(new ResourceLocation(SilentGear.MOD_ID, "rough_tools"), result, primer);
        return new Recipe(baseRecipe);
    }

    private static class Recipe extends RecipeBaseSL {
        private final ShapedOreRecipe baseRecipe;

        Recipe(ShapedOreRecipe baseRecipe) {
            this.baseRecipe = baseRecipe;
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            return baseRecipe.matches(inv, worldIn);
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            StackList list = StackList.fromInventory(inv);
            Collection<ItemPartData> parts = list.allMatches(s -> PartRegistry.get(s) != null)
                    .stream()
                    .map(ItemPartData::fromStack)
                    .collect(Collectors.toList());

            ICoreItem item = (ICoreItem) baseRecipe.getRecipeOutput().getItem();
            return item.construct(item.getItem(), parts);
        }

        @Override
        public ItemStack getRecipeOutput() {
            return baseRecipe.getRecipeOutput();
        }
    }
    */
}
