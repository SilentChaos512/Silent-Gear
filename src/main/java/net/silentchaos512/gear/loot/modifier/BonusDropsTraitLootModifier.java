package net.silentchaos512.gear.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BonusDropsTraitLootModifier extends LootModifier {
    public static final Supplier<Codec<BonusDropsTraitLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                    codecStart(inst).apply(inst, BonusDropsTraitLootModifier::new)));

    public BonusDropsTraitLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ObjectArrayList<ItemStack> ret = new ObjectArrayList<>(generatedLoot);
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);

        if (tool != null && GearHelper.isGear(tool)) {
            //noinspection OverlyLongLambda
            TraitHelper.activateTraits(tool, 0, (trait, level, value) -> {
                generatedLoot.forEach(lootStack -> {
                    ItemStack stack = trait.addLootDrops(new TraitActionContext(null, level, tool), lootStack);
                    if (!stack.isEmpty()) {
                        ret.add(stack);
                    }
                });
                return 0;
            });
        }

        return ret;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
