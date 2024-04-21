package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;

public class SgCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SilentGear.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register("tab", () ->
            CreativeModeTab.builder()
                    .icon(() -> SgItems.BLUEPRINT_PACKAGE.get().getStack())
                    .title(Component.translatable("itemGroup.silentgear"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // TODO: What about sub items?
                        SgItems.ITEMS.getEntries().forEach(ro -> output.accept(ro.get()));
                    })
                    .build());
}
