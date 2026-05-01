package net.silentchaos512.gear.crafting.recipe.modkit;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.ModKitItem;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

public class ModKitRemovePartRecipe extends ModKitRecipe {
    public static final ModKitRemovePartRecipe INSTANCE = new ModKitRemovePartRecipe();

    @Override
    public boolean isActionSupported(PartType partType) {
        return partType.isRemovable();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        StackList list = StackList.from(input);
        ItemStack gear = list.uniqueOfType(GearItem.class);
        ItemStack modKit = list.uniqueOfType(ModKitItem.class);
        if (gear.isEmpty() || modKit.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = gear.copy();
        PartType type = ModKitItem.getSelectedType(modKit);

        if (GearData.removeFirstPartOfType(result, type)) {
            GearData.recalculateGearData(result, CommonHooks.getCraftingPlayer());
        }
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
        ItemStack gear = StackList.from(inv).uniqueOfType(GearItem.class);
        ItemStack modKit = StackList.from(inv).uniqueOfType(ModKitItem.class);
        PartType type = ModKitItem.getSelectedType(modKit);
        PartInstance part = GearData.getConstruction(gear).getPartOfType(type);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof GearItem) {
                list.set(i, part != null ? part.copyItem() : ItemStack.EMPTY);
            } else {
                var craftingRemainder = stack.getCraftingRemainder();
                if (craftingRemainder != null) {
                    list.set(i, craftingRemainder.create());
                }
            }
        }

        return list;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SgRecipes.MOD_KIT_REMOVE_PART.get();
    }
}
