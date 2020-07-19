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
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.loot.function.SelectGearTierLootFunction;
import net.silentchaos512.gear.loot.function.SetPartsFunction;
import net.silentchaos512.gear.parts.LazyPartData;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.BiConsumer;

public class ModGiftLootTables extends GiftLootTables {
    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
        p_accept_1_.accept(ModItems.BLUEPRINT_PACKAGE.get().getDefaultLootTable(), LootTable.builder()
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(ModItems.ROD_BLUEPRINT)))
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(ModItems.PICKAXE_BLUEPRINT)))
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(ModItems.SHOVEL_BLUEPRINT)))
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(ModItems.AXE_BLUEPRINT)))
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(ModItems.SWORD_BLUEPRINT)
                                .weight(11))
                        .addEntry(ItemLootEntry.builder(ModItems.KATANA_BLUEPRINT)
                                .weight(5))
                        .addEntry(ItemLootEntry.builder(ModItems.MACHETE_BLUEPRINT)
                                .weight(7))
                        .addEntry(ItemLootEntry.builder(ModItems.SPEAR_BLUEPRINT)
                                .weight(8)))
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(ModItems.SHIELD_BLUEPRINT))));

        for (Item item : Registration.getItems(item -> item instanceof ICoreItem)) {
            p_accept_1_.accept(SilentGear.getId("random_gear/" + NameUtils.from(item).getPath()), LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .addEntry(ItemLootEntry.builder(item)
                                    .weight(3)
                                    .acceptFunction(SelectGearTierLootFunction.builder(1)))
                            .addEntry(ItemLootEntry.builder(item)
                                    .weight(5)
                                    .acceptFunction(SelectGearTierLootFunction.builder(2)))
                            .addEntry(ItemLootEntry.builder(item)
                                    .weight(2)
                                    .acceptFunction(SelectGearTierLootFunction.builder(3)))));
        }

        p_accept_1_.accept(SilentGear.getId("test/ldf_mallet"), LootTable.builder()
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(ModItems.HAMMER)
                                .acceptFunction(SetPartsFunction.builder(ImmutableList.of(
                                        new LazyPartData(SilentGear.getId("main/diamond"), MaterialGrade.S),
                                        new LazyPartData(SilentGear.getId("main/diamond"), MaterialGrade.S),
                                        new LazyPartData(SilentGear.getId("main/emerald"), MaterialGrade.S),
                                        new LazyPartData(SilentGear.getId("rod/blaze")),
                                        new LazyPartData(SilentGear.getId("tip/redstone")))))
                                .acceptFunction(() -> setName(new StringTextComponent("Loliberty Defense Force Mallet")))
                                .acceptFunction(() -> setLore(ImmutableList.of(
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
