package net.silentchaos512.gear.init;

import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtra;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtraConfig;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ModPlacement {
    private static final Collection<Placement<?>> TO_REGISTER = new ArrayList<>();

    public static final Placement<NetherFloorWithExtraConfig> NETHER_FLOOR_WITH_EXTRA = register("nether_floor_with_extra", new NetherFloorWithExtra(NetherFloorWithExtraConfig::deserialize));

    private static <T extends IPlacementConfig> Placement<T> register(String key, Placement<T> value) {
        value.setRegistryName(SilentGear.getId(key));
        TO_REGISTER.add(value);
        return value;
    }

    public static void registerAll(RegistryEvent.Register<Placement<?>> event) {
        TO_REGISTER.forEach(p -> event.getRegistry().register(p));
        TO_REGISTER.clear();
    }
}
