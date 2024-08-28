package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.lib.collection.StackList;

/**
 * This replaces vanilla's item repair recipe. That recipe deletes all NBT, so the results would be
 * disastrous on SGear items. This blocks {@link GearItem} from matching. For all others, this is
 * passed back to the vanilla version.
 *
 * @since 0.3.2
 */
public class RepairItemRecipeFix extends RepairItemRecipe {
    public RepairItemRecipeFix(CraftingBookCategory bookCategory) {
        super(bookCategory);
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        ItemStack gearStack = StackList.from(inv).firstMatch(s -> s.getItem() instanceof GearItem);
        return gearStack.isEmpty() && super.matches(inv, worldIn);
    }
}
