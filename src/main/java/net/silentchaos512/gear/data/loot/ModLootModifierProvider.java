package net.silentchaos512.gear.data.loot;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.modifier.BonusDropsTraitLootModifier;
import net.silentchaos512.gear.loot.modifier.MagmaticTraitLootModifier;
import net.silentchaos512.gear.util.Const;

public class ModLootModifierProvider extends GlobalLootModifierProvider {
    public ModLootModifierProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), SilentGear.MOD_ID);
    }

    @Override
    protected void start() {
        add("bonus_drops_trait", new BonusDropsTraitLootModifier(
                new LootItemCondition[]{}
        ));

        add("magmatic_smelting", new MagmaticTraitLootModifier(
                new LootItemCondition[]{
                        HasTraitCondition.builder(Const.Traits.MAGMATIC).build()
                }
        ));
    }
}
