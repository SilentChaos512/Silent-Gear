package net.silentchaos512.gear.loot.modifier;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls the smelting ability of the Magmatic trait. Pretty much a direct copy of the smelting
 * enchantment example provided by Forge.
 */
public class MagmaticTraitLootModifier extends LootModifier {
    public MagmaticTraitLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        List<ItemStack> ret = new ArrayList<>();
        generatedLoot.forEach(s -> ret.add(smelt(s, context)));
        return ret;
    }

    private static ItemStack smelt(ItemStack stack, LootContext context) {
        return context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld())
                .map(FurnaceRecipe::getRecipeOutput)
                .filter(s -> !s.isEmpty())
                .map(s -> ItemHandlerHelper.copyStackWithSize(s, stack.getCount() * s.getCount()))
                .orElse(stack);
    }

    public static class Serializer extends GlobalLootModifierSerializer<MagmaticTraitLootModifier> {
        @Override
        public MagmaticTraitLootModifier read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn) {
            return new MagmaticTraitLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(MagmaticTraitLootModifier instance) {
            return new JsonObject();
        }
    }
}
