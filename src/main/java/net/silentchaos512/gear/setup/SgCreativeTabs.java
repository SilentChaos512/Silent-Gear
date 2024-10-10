package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.core.BuiltinMaterials;
import net.silentchaos512.gear.item.ItemWithSubItems;

public class SgCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SilentGear.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_TABS.register("main", () ->
            CreativeModeTab.builder()
                    .icon(() -> SgItems.BLUEPRINT_PACKAGE.get().getStack())
                    .title(Component.translatable("itemGroup.silentgear"))
                    .displayItems((itemDisplayParameters, output) ->
                            SgItems.ITEMS.getEntries()
                                    .stream()
                                    .filter(item -> !(item instanceof GearItem))
                                    .forEach(item -> addSubItems(item.get(), output))
                    )
                    .build()
    );
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GEAR = CREATIVE_TABS.register("gear", () ->
            CreativeModeTab.builder()
                    .icon(() -> GearItemSets.PICKAXE.constructBasicItem(BuiltinMaterials.CRIMSON_STEEL))
                    .title(Component.translatable("itemGroup.silentgear.gear"))
                    .displayItems((itemDisplayParameters, output) ->
                            GearItemSets.getIterator().forEachRemaining(set -> {
                                for (var builtinMaterial : BuiltinMaterials.EXAMPLE_SUB_ITEM_MATERIALS) {
                                    output.accept(set.constructBasicItem(builtinMaterial));
                                }
                            })
                    )
                    .build()
    );

    private static void addSubItems(Item item, CreativeModeTab.Output output) {
        if (item instanceof ItemWithSubItems itemWithSubItems) {
            itemWithSubItems.addSubItems(output);
        } else {
            output.accept(item);
        }
    }
}
