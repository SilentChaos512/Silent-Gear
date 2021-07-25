package net.silentchaos512.gear.loot.modifier;

import com.google.gson.JsonObject;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
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
    public MagmaticTraitLootModifier(LootItemCondition[] conditionsIn) {
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
        return context.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), context.getLevel())
                .map(SmeltingRecipe::getResultItem)
                .filter(s -> !s.isEmpty())
                .map(s -> ItemHandlerHelper.copyStackWithSize(s, stack.getCount() * s.getCount()))
                .orElse(stack);
    }

    public static class Serializer extends GlobalLootModifierSerializer<MagmaticTraitLootModifier> {
        @Override
        public MagmaticTraitLootModifier read(ResourceLocation name, JsonObject json, LootItemCondition[] conditionsIn) {
            return new MagmaticTraitLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(MagmaticTraitLootModifier instance) {
            return new JsonObject();
        }
    }
}
