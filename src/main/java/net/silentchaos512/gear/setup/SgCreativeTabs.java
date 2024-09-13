package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.item.ItemWithSubItems;

public class SgCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SilentGear.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register("tab", () ->
            CreativeModeTab.builder()
                    .icon(() -> SgItems.BLUEPRINT_PACKAGE.get().getStack())
                    .title(Component.translatable("itemGroup.silentgear"))
                    .displayItems((itemDisplayParameters, output) ->
                            SgItems.ITEMS.getEntries().forEach(item ->
                                    addSubItems(item.get(), output)
                            )
                    )
                    .build());

    private static void addSubItems(Item item, CreativeModeTab.Output output) {
        if (item instanceof ItemWithSubItems itemWithSubItems) {
            itemWithSubItems.addSubItems(output);
        } else {
            output.accept(item);
        }
    }
}
