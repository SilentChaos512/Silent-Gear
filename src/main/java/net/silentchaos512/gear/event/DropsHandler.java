package net.silentchaos512.gear.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.item.CraftingItems;

public class DropsHandler {

    public static final DropsHandler INSTANCE = new DropsHandler();

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (entity == null)
            return;

        // Sinew drops
        if (Config.sinewAnimals.matches(entity)) {
            float chance = Config.sinewDropRate * (1 + 0.3f * event.getLootingLevel());
            if (SilentGear.RANDOM.nextFloat() < chance) {
                EntityItem e = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, CraftingItems.SINEW.getStack());
                event.getDrops().add(e);
            }
        }
    }
}
