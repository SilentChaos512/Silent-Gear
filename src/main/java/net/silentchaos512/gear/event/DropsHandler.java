package net.silentchaos512.gear.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.utils.MathUtils;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class DropsHandler {
    private DropsHandler() {}

    @SubscribeEvent
    public static void onEntityDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity == null) return;

        // Sinew drops
        if (Config.GENERAL.isSinewAnimal(entity)) {
            double chance = Config.GENERAL.sinewDropRate.get() * (1 + 0.2 * event.getLootingLevel());
            if (MathUtils.tryPercentage(SilentGear.random, chance)) {
                ItemStack stack = new ItemStack(CraftingItems.SINEW);
                event.getDrops().add(new ItemEntity(entity.world, entity.posX, entity.posY + entity.getHeight() / 2, entity.posZ, stack));
            }
        }
    }
}
