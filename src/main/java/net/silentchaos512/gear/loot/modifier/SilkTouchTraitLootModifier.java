package net.silentchaos512.gear.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.Optional;
import java.util.function.Supplier;

public class SilkTouchTraitLootModifier extends LootModifier {
    public static final Supplier<MapCodec<SilkTouchTraitLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst ->
                    codecStart(inst).apply(inst, SilkTouchTraitLootModifier::new)));

    public SilkTouchTraitLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        var lootTableLookup = context.getLevel().getServer().reloadableRegistries().lookup().lookup(Registries.LOOT_TABLE).orElseThrow();
        var lootTable = lootTableLookup.get(ResourceKey.create(Registries.LOOT_TABLE, context.getQueriedLootTableId())).orElseThrow().value();

        var tool = context.getParam(LootContextParams.TOOL);
        var silkTouchTool = createSilkTouchCopy(tool, context.getLevel());
        var newContext = createSilkTouchLootContext(context, silkTouchTool);

        generatedLoot.clear();
        //noinspection deprecation
        lootTable.getRandomItemsRaw(newContext, generatedLoot::add);
        return generatedLoot;
    }

    private static ItemStack createSilkTouchCopy(ItemStack tool, ServerLevel level) {
        var copy = tool.copy();
        copy.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        EnchantmentHelper.updateEnchantments(copy, enchants -> enchants.set(registry.getOrThrow(Enchantments.SILK_TOUCH), 1));
        return copy;
    }

    private static LootContext createSilkTouchLootContext(LootContext original, ItemStack silkTouchTool) {
        var builder = new LootParams.Builder(original.getLevel())
                .withLuck(original.getLuck())
                .withParameter(LootContextParams.BLOCK_STATE, original.getParam(LootContextParams.BLOCK_STATE))
                .withParameter(LootContextParams.ORIGIN, original.getParam(LootContextParams.ORIGIN))
                .withParameter(LootContextParams.TOOL, silkTouchTool);
        if (original.hasParam(LootContextParams.THIS_ENTITY)) {
            builder.withParameter(LootContextParams.THIS_ENTITY, original.getParam(LootContextParams.THIS_ENTITY));
        }
        if (original.hasParam(LootContextParams.BLOCK_ENTITY)) {
            builder.withParameter(LootContextParams.BLOCK_ENTITY, original.getParam(LootContextParams.BLOCK_ENTITY));
        }
        if (original.hasParam(LootContextParams.EXPLOSION_RADIUS)) {
            builder.withParameter(LootContextParams.EXPLOSION_RADIUS, original.getParam(LootContextParams.EXPLOSION_RADIUS));
        }
        var lootParams = builder.create(LootContextParamSets.BLOCK);
        return new LootContext.Builder(lootParams).create(Optional.empty());
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
