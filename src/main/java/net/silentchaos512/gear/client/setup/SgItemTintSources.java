package net.silentchaos512.gear.client.setup;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.color.item.GearPartColor;
import net.silentchaos512.gear.client.color.item.MaterialColor;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SgItemTintSources {
    @SubscribeEvent
    public static void onItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(SilentGear.getId("material"), MaterialColor.CODEC);
        event.register(SilentGear.getId("gear_part"), GearPartColor.CODEC);
    }

    public static MaterialColor blendedMaterialColor() {
        return MaterialColor.INSTANCE;
    }

    public static GearPartColor gearPartColor(Supplier<PartType> partType) {
        return new GearPartColor(SgRegistries.PART_TYPE.wrapAsHolder(partType.get()));
    }
}
