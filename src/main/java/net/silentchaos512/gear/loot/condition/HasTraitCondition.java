package net.silentchaos512.gear.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.setup.SgLoot;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.SimpleIntRange;
import net.silentchaos512.gear.util.TraitHelper;

public class HasTraitCondition extends GearLootCondition {
    public static final Codec<HasTraitCondition> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    DataResource.TRAIT_CODEC.fieldOf("trait").forGetter(c -> c.trait),
                    SimpleIntRange.CODEC.fieldOf("level").forGetter(c -> c.level)
            ).apply(instance, HasTraitCondition::new)
    );

    private final DataResource<ITrait> trait;
    private final SimpleIntRange level;

    public HasTraitCondition(DataResource<ITrait> trait, SimpleIntRange level) {
        this.trait = trait;
        this.level = level;
    }

    @Override
    public boolean test(LootContext context) {
        ItemStack tool = getItemUsed(context);
        if (!GearHelper.isGear(tool)) return false;
        int traitLevel = TraitHelper.getTraitLevel(tool, trait);
        return this.level.test(traitLevel);
    }

    public static LootItemCondition.Builder builder(DataResource<ITrait> trait) {
        return builder(trait, 1, Integer.MAX_VALUE);
    }

    public static LootItemCondition.Builder builder(DataResource<ITrait> trait, int minLevel) {
        return builder(trait, minLevel, Integer.MAX_VALUE);
    }

    public static LootItemCondition.Builder builder(DataResource<ITrait> trait, int minLevel, int maxLevel) {
        return () -> new HasTraitCondition(trait, new SimpleIntRange(minLevel, maxLevel));
    }

    @Override
    public LootItemConditionType getType() {
        return SgLoot.HAS_TRAIT.get();
    }
}
