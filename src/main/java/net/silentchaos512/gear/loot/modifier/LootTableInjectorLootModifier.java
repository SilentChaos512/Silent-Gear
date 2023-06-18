package net.silentchaos512.gear.loot.modifier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.ModResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class LootTableInjectorLootModifier extends LootModifier {
    private static final Map<ResourceLocation, Float> SEED_CHANCE = ImmutableMap.<ResourceLocation, Float>builder()
            .put(BuiltInLootTables.ABANDONED_MINESHAFT, 1f / 3f)
            .put(BuiltInLootTables.BASTION_TREASURE, 2f / 3f)
            .put(BuiltInLootTables.BURIED_TREASURE, 1f / 3f)
            .put(BuiltInLootTables.END_CITY_TREASURE, 2f / 3f)
            .put(BuiltInLootTables.JUNGLE_TEMPLE, 1f / 3f)
            .put(BuiltInLootTables.SHIPWRECK_TREASURE, 1f / 3f)
            .put(BuiltInLootTables.STRONGHOLD_CORRIDOR, 2f / 3f)
            .put(BuiltInLootTables.STRONGHOLD_CROSSING, 3f / 5f)
            .build();

    public static final Supplier<Codec<LootTableInjectorLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                    codecStart(inst).apply(inst, LootTableInjectorLootModifier::new)
            )
    );

    public LootTableInjectorLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ObjectArrayList<ItemStack> ret = new ObjectArrayList<>(generatedLoot);
        ResourceLocation baseTableId = context.getQueriedLootTableId();
        ResourceLocation injectTableId = getInjectorTableId(baseTableId);
        LootTable lootTable = context.getLevel().getServer().getLootData().getLootTable(injectTableId);
        //noinspection deprecation -- using this version to avoid calling loot modifiers a second time
        lootTable.getRandomItems(context, ret::add);
        return ret;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    @NotNull
    public static ModResourceLocation getInjectorTableId(ResourceLocation baseTableId) {
        return SilentGear.getId("inject/" + baseTableId.getNamespace() + "/" + baseTableId.getPath());
    }
}

