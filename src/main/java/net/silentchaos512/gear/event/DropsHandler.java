package net.silentchaos512.gear.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class DropsHandler {
    private DropsHandler() {}

    @SubscribeEvent
    public static void onEntityDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (entity == null)
            return;

        // Sinew drops
        /*
        if (Config.sinewAnimals.matches(entity)) {
            float chance = Config.sinewDropRate * (1 + 0.3f * event.getLootingLevel());
            if (SilentGear.random.nextFloat() < chance) {
                ItemStack stack = new ItemStack(CraftingItems.SINEW);
                EntityItem e = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack);
                event.getDrops().add(e);
            }
        }
        */
    }
}
