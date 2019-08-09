package net.silentchaos512.gear.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;

import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class NerfedGear {
    public static final List<String> DEFAULT_ITEMS = ImmutableList.of(
            "diamond_axe", "iron_axe", "golden_axe", "stone_axe", "wooden_axe",
            "diamond_hoe", "iron_hoe", "golden_hoe", "stone_hoe", "wooden_hoe",
            "diamond_pickaxe", "iron_pickaxe", "golden_pickaxe", "stone_pickaxe", "wooden_pickaxe",
            "diamond_shovel", "iron_shovel", "golden_shovel", "stone_shovel", "wooden_shovel",
            "diamond_sword", "iron_sword", "golden_sword", "stone_sword", "wooden_sword"
    );

    private NerfedGear() {}

    public static void init() {
        Field itemDamageField;
        try {
            itemDamageField = ObfuscationReflectionHelper.findField(Item.class, "field_77699_b");
            itemDamageField.setAccessible(true);
        } catch (Exception ex) {
            SilentGear.LOGGER.error("Field to get Item damage field via reflection");
            SilentGear.LOGGER.catching(ex);
            return;
        }

        for (Item item : ForgeRegistries.ITEMS) {
            if (isNerfedItem(item)) {
                SilentGear.LOGGER.debug("Try nerf durability of {}", item.getRegistryName());
                try {
                    int maxDamage = (int) itemDamageField.get(item);
                    int newMax = MathHelper.clamp((int) (maxDamage * Config.GENERAL.nerfedItemDurabilityMulti.get()), 1, maxDamage);
                    itemDamageField.set(item, newMax);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isNerfedItem(Item item) {
        return item.isDamageable() && Config.GENERAL.isNerfedItem(item);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.getPlayer().getHeldItemMainhand();
        if (isNerfedItem(heldItem.getItem())) {
            float newSpeed = event.getNewSpeed() * Config.GENERAL.nerfedItemHarvestSpeedMulti.get().floatValue();
            event.setNewSpeed(Math.max(newSpeed, 1));
        }
    }
}
