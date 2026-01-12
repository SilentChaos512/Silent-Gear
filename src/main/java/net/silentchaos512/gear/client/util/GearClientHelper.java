package net.silentchaos512.gear.client.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.Color;

public final class GearClientHelper {
    private GearClientHelper() {
    }

    public static int getColor(ItemStack stack, PartType layer) {
        PartInstance part = GearData.getPartOfType(stack, layer);
        if (part != null) {
            return part.getColor(stack, 0, 0);
        }
        return Color.VALUE_WHITE;
    }

    private static Iterable<GearProperty<?, ?>> getDisplayProperties(ItemStack stack, GearTooltipFlag flag) {
        if (flag.isAdvanced() && SilentGear.isDevBuild()) {
            return SgRegistries.GEAR_PROPERTY;
        }
        return GearPropertyGroups.getSortedRelevantProperties(GearHelper.getType(stack).relevantPropertyGroups());
    }

    private static MutableComponent misc(String key, Object... formatArgs) {
        return Component.translatable("misc.silentgear." + key, formatArgs);
    }

    public static boolean hasEffect(ItemStack stack) {
        return Config.Client.allowEnchantedEffect.get() && stack.isEnchanted();
    }

    public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItemSameComponents(oldStack, newStack);
    }
}
