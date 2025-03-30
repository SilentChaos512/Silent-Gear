package net.silentchaos512.gear.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.function.Supplier;

public class FortuneTraitLootModifier extends EnchantedDropsTraitLootModifier {
    public static final Supplier<MapCodec<FortuneTraitLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst ->
                    inst.group(
                            Codec.INT.fieldOf("level").forGetter(glm -> glm.traitLevel),
                            IGlobalLootModifier.LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(glm -> glm.conditions)
                    ).apply(inst, FortuneTraitLootModifier::new)
            )
    );

    public FortuneTraitLootModifier(int traitLevel, LootItemCondition[] conditionsIn) {
        super(traitLevel, conditionsIn);
    }

    @Override
    protected void addEnchantments(HolderLookup.RegistryLookup<Enchantment> registry, ItemEnchantments.Mutable enchantments, int traitLevel) {
        var fortune = registry.getOrThrow(Enchantments.FORTUNE);
        enchantments.set(fortune, traitLevel);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
