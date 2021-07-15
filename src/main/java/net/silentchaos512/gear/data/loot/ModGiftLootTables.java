package net.silentchaos512.gear.data.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.loot.GiftLootTables;
import net.minecraft.item.Item;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.SetLore;
import net.minecraft.loot.functions.SetName;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.gear.part.LazyPartData;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.loot.function.SelectGearTierLootFunction;
import net.silentchaos512.gear.loot.function.SetPartsFunction;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.BiConsumer;

public class ModGiftLootTables extends GiftLootTables {
    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
        p_accept_1_.accept(ModItems.BLUEPRINT_PACKAGE.get().getDefaultLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(ModItems.ROD_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(ModItems.PICKAXE_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(ModItems.SHOVEL_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(ModItems.AXE_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(ModItems.KNIFE_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(ModItems.SWORD_BLUEPRINT)
                                .setWeight(11))
                        .add(ItemLootEntry.lootTableItem(ModItems.KATANA_BLUEPRINT)
                                .setWeight(5))
                        .add(ItemLootEntry.lootTableItem(ModItems.MACHETE_BLUEPRINT)
                                .setWeight(7))
                        .add(ItemLootEntry.lootTableItem(ModItems.SPEAR_BLUEPRINT)
                                .setWeight(8)))
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(ModItems.SHIELD_BLUEPRINT))));

        for (Item item : Registration.getItems(item -> item instanceof ICoreItem)) {
            p_accept_1_.accept(SilentGear.getId("random_gear/" + NameUtils.from(item).getPath()), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(ItemLootEntry.lootTableItem(item)
                                    .setWeight(3)
                                    .apply(SelectGearTierLootFunction.builder(1)))
                            .add(ItemLootEntry.lootTableItem(item)
                                    .setWeight(5)
                                    .apply(SelectGearTierLootFunction.builder(2)))
                            .add(ItemLootEntry.lootTableItem(item)
                                    .setWeight(2)
                                    .apply(SelectGearTierLootFunction.builder(3)))));
        }

        // FIXME
        p_accept_1_.accept(SilentGear.getId("test/ldf_mallet"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(ModItems.HAMMER)
                                .apply(SetPartsFunction.builder(ImmutableList.of(
                                        new LazyPartData(SilentGear.getId("main/diamond")),
                                        new LazyPartData(SilentGear.getId("main/diamond")),
                                        new LazyPartData(SilentGear.getId("main/emerald")),
                                        new LazyPartData(SilentGear.getId("rod/blaze")),
                                        new LazyPartData(SilentGear.getId("tip/redstone")))))
                                .apply(() -> setName(new StringTextComponent("Loliberty Defense Force Mallet")))
                                .apply(() -> setLore(ImmutableList.of(
                                        new StringTextComponent("Standard Issue"),
                                        new StringTextComponent("Protectors of Free Speech")))))));
    }

    @Nonnull
    private static SetName setName(ITextComponent text) {
        Constructor<SetName> constructor = ObfuscationReflectionHelper.findConstructor(SetName.class, ILootCondition[].class, ITextComponent.class, LootContext.EntityTarget.class);
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(new ILootCondition[0], text, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private static SetLore setLore(List<ITextComponent> lore) {
        return new SetLore(new ILootCondition[0], false, lore, null);
    }
}
