package net.silentchaos512.gear.event;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

public class VanillaGearHandler implements IPhasedInitializer {

    public static final VanillaGearHandler INSTANCE = new VanillaGearHandler();

    private VanillaGearHandler() {
    }

    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        // TODO: Configs!
        // Reduce durability of vanilla tools and armor to discourage use
        for (String name : Config.nerfedGear) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
            if (item != null) {
                @SuppressWarnings("deprecation") int oldMax = item.getMaxDamage();
                int newMax = (int) (oldMax * Config.nerfedGearMulti);
                SilentGear.log.debug("Try set durability of {} ({} -> {})", name, oldMax, newMax);
                item.setMaxDamage(newMax);
            } else {
                SilentGear.log.error("Could not find item \"{}\". Did you enter the name correctly?", name);
            }
        }
    }
}
