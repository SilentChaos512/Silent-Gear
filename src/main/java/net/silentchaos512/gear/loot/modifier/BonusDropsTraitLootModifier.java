package net.silentchaos512.gear.loot.modifier;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BonusDropsTraitLootModifier extends LootModifier {
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

    public static class Serializer extends GlobalLootModifierSerializer<BonusDropsTraitLootModifier> {
        @Override
        public BonusDropsTraitLootModifier read(ResourceLocation name, JsonObject json, LootItemCondition[] conditionsIn) {
            return new BonusDropsTraitLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(BonusDropsTraitLootModifier instance) {
            return new JsonObject();
        }
    }
}
