package net.silentchaos512.gear.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Controls the smelting ability of the Magmatic trait. Pretty much a direct copy of the smelting
 * enchantment example provided by Forge.
 */
public class MagmaticTraitLootModifier extends LootModifier {
    public static final Supplier<Codec<MagmaticTraitLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                    codecStart(inst).apply(inst, MagmaticTraitLootModifier::new)));

    public MagmaticTraitLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ObjectArrayList<ItemStack> ret = new ObjectArrayList<>();
        generatedLoot.forEach(s -> ret.add(smelt(s, context)));
        return ret;
    }

    private static ItemStack smelt(ItemStack stack, LootContext context) {
        return context.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), context.getLevel())
                .map(r -> r.value().getResultItem(context.getLevel().registryAccess()))
                .filter(s -> !s.isEmpty())
                .map(s -> ItemHandlerHelper.copyStackWithSize(s, stack.getCount() * s.getCount()))
                .orElse(stack);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
