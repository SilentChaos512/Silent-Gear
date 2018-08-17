package net.silentchaos512.gear.event;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

public class VanillaGearHandler implements IPhasedInitializer {

    public static final VanillaGearHandler INSTANCE = new VanillaGearHandler();

    private VanillaGearHandler() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        // TODO: Configs!
        // Reduce durability of vanilla tools to discourage use
        String[] toolTypes = {"pickaxe", "shovel", "axe", "sword"};
        String[] toolMaterials = {"wooden", "stone", "iron", "golden", "diamond"};
        for (String type : toolTypes) {
            for (String mat : toolMaterials) {
                ResourceLocation key = new ResourceLocation("minecraft", mat + "_" + type);
                Item item = ForgeRegistries.ITEMS.getValue(key);
                if (item != null)
                    item.setMaxDamage(item.getMaxDamage() / 10);
                else
                    SilentGear.log.fatal("Could not find item " + key.toString());
            }
        }

        // And armor as well
        String[] armorTypes = {"helmet", "chestplate", "leggings", "boots"};
        String[] armorMaterials = {"leather", "chainmail", "iron", "diamond", "golden"};
        for (String type : armorTypes) {
            for (String mat : armorMaterials) {
                ResourceLocation key = new ResourceLocation("minecraft", mat + "_" + type);
                Item item = ForgeRegistries.ITEMS.getValue(key);
                if (item != null)
                    item.setMaxDamage(item.getMaxDamage() / 10);
                else
                    SilentGear.log.fatal("Could not find item " + key.toString());
            }
        }
    }
}
