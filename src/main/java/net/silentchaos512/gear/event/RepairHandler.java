package net.silentchaos512.gear.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.RepairContext;
import net.silentchaos512.gear.gear.part.UpgradePart;
import net.silentchaos512.gear.util.GearData;

import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class RepairHandler {
    private RepairHandler() {}

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() instanceof ICoreItem) {
            MaterialInstance material = MaterialInstance.from(event.getRight());
            PartData part = PartData.from(event.getRight());

            if (material != null) {
                handleGearRepair(event, material);
            } else if (part != null && part.get() instanceof UpgradePart) {
                handleUpgradeApplication(event, part);
            }
        }
    }

    private static void handleUpgradeApplication(AnvilUpdateEvent event, PartData part) {
        ItemStack result = event.getLeft().copy();
        applyName(event, result);

        GearData.addUpgradePart(result, part);
        GearData.recalculateStats(result, null);

        event.setOutput(result);
        // TODO: Upgrade cost?
        event.setCost(3);
    }

    private static void handleGearRepair(AnvilUpdateEvent event, MaterialInstance material) {
        ItemStack result = event.getLeft().copy();
        applyName(event, result);

        float repairValue = material.getRepairValue(result, RepairContext.Type.ANVIL);
        float gearRepairEfficiency = GearData.getStat(result, ItemStats.REPAIR_EFFICIENCY);
        float anvilEfficiency = Config.Common.repairFactorAnvil.get().floatValue();
        float amount = repairValue * gearRepairEfficiency * anvilEfficiency;

        // How many of materials to use?
        int materialCount = 1;
        float repaired = amount;
        while (materialCount < event.getRight().getCount() && repaired < result.getDamageValue()) {
            ++materialCount;
            repaired += amount;
        }

        if (amount > 0) {
            result.hurt(-Math.round(amount * materialCount), SilentGear.RANDOM_SOURCE, null);
            GearData.recalculateStats(result, null);
            event.setOutput(result);
            event.setCost(materialCount);
            event.setMaterialCost(materialCount);
        }
    }

    private static void applyName(AnvilUpdateEvent event, ItemStack stack) {
        if (!event.getName().isEmpty()) {
            stack.setHoverName(Component.literal(event.getName()));
        }
    }

    /*
    TODO: Uncomment when https://github.com/MinecraftForge/MinecraftForge/pull/5831 is merged
    @SubscribeEvent
    public static void onGrindstoneUpdate(GrindstoneUpdateEvent event) {
        ItemStack first = event.getFirst();
        ItemStack second = event.getSecond();
        if (!canRepairTogether(first, second)) {
            event.setCanceled(true);
            return;
        }

        int durability1 = first.getMaxDamage() - first.getDamage();
        int durability2 = second.getMaxDamage() - second.getDamage();
        int newDurability = durability1 + durability2 + first.getMaxDamage() * 5 / 100;
        int newDamage = Math.max(first.getMaxDamage() - newDurability, 0);
        ItemStack result = copyCurses(first, second);
        event.setOutput(createGrindstoneResult(result, newDamage));
    }
    */

    private static boolean canRepairTogether(ItemStack first, ItemStack second) {
        return first.getItem() == second.getItem()
                && first.getItem() instanceof ICoreItem
                && GearData.getTier(first) <= GearData.getTier(second);
    }

    private static ItemStack copyCurses(ItemStack first, ItemStack second) {
        // Copy of GrindstoneContainer#mergeEnchants
        ItemStack itemstack = first.copy();
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(second);

        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (!enchantment.isCurse() || itemstack.getEnchantmentLevel(enchantment) == 0) {
                itemstack.enchant(enchantment, entry.getValue());
            }
        }

        return itemstack;
    }

    private static ItemStack createGrindstoneResult(ItemStack stack, int newDamage) {
        // Copy of GrindstoneContainer#removeNonCurses
        ItemStack itemstack = stack.copy();
        itemstack.removeTagKey("Enchantments");
        itemstack.removeTagKey("StoredEnchantments");
        if (newDamage > 0) {
            itemstack.setDamageValue(newDamage);
        } else {
            itemstack.removeTagKey("Damage");
        }

        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack).entrySet().stream()
                .filter((p_217012_0_) -> p_217012_0_.getKey().isCurse())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.setEnchantments(map, itemstack);
        itemstack.setRepairCost(0);
        if (itemstack.getItem() == Items.ENCHANTED_BOOK && map.isEmpty()) {
            itemstack = new ItemStack(Items.BOOK);
            if (stack.hasCustomHoverName()) {
                itemstack.setHoverName(stack.getHoverName());
            }
        }

        for (int i = 0; i < map.size(); ++i) {
            itemstack.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(itemstack.getBaseRepairCost()));
        }

        return itemstack;
    }
}
