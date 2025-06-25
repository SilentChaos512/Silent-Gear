package net.silentchaos512.gear.loot.modifier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.Optional;

public abstract class EnchantedDropsTraitLootModifier extends LootModifier {
    protected final int traitLevel;

    public EnchantedDropsTraitLootModifier(int traitLevel, LootItemCondition[] conditionsIn) {
        super(conditionsIn);
        this.traitLevel = traitLevel;
    }

    protected abstract void addEnchantments(HolderLookup.RegistryLookup<Enchantment> registry, ItemEnchantments.Mutable enchantments, int traitLevel);

    private ItemStack createEnchantedCopy(ItemStack tool, ServerLevel level) {
        var copy = tool.copy();
        copy.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        EnchantmentHelper.updateEnchantments(copy, enchantments -> addEnchantments(registry, enchantments, this.traitLevel));
        return copy;
    }

    private LootContext createEnchantedLootContext(LootContext original, ItemStack enchantedTool) {
        var builder = new LootParams.Builder(original.getLevel())
                .withLuck(original.getLuck())
                .withParameter(LootContextParams.BLOCK_STATE, original.getParam(LootContextParams.BLOCK_STATE))
                .withParameter(LootContextParams.ORIGIN, original.getParam(LootContextParams.ORIGIN))
                .withParameter(LootContextParams.TOOL, enchantedTool);
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
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.hasParam(LootContextParams.TOOL)) return generatedLoot;

        var tool = context.getParam(LootContextParams.TOOL);
        var enchantedTool = createEnchantedCopy(tool, context.getLevel());
        var newContext = createEnchantedLootContext(context, enchantedTool);

        var lootTableLookup = context.getLevel().getServer().reloadableRegistries().lookup().lookup(Registries.LOOT_TABLE).orElseThrow();
        var lootTableOptional = lootTableLookup.get(ResourceKey.create(Registries.LOOT_TABLE, context.getQueriedLootTableId()));
        if (lootTableOptional.isEmpty()) {
            return generatedLoot;
        } else {
            var lootTable = lootTableOptional.value();

            generatedLoot.clear();
            //noinspection deprecation
            lootTable.getRandomItemsRaw(newContext, generatedLoot::add);
            return generatedLoot;
        }
    }
}
