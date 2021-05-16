package net.silentchaos512.gear.loot.modifier;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BonusDropsTraitLootModifier extends LootModifier {
    public BonusDropsTraitLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        List<ItemStack> ret = new ArrayList<>(generatedLoot);
        ItemStack tool = context.get(LootParameters.TOOL);

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

    public static class Serializer extends GlobalLootModifierSerializer<BonusDropsTraitLootModifier> {
        @Override
        public BonusDropsTraitLootModifier read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn) {
            return new BonusDropsTraitLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(BonusDropsTraitLootModifier instance) {
            return new JsonObject();
        }
    }
}
