package net.silentchaos512.gear.compat.mineandslash;

import com.robertx22.mine_and_slash.config.compatible_items.ConfigItem;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.util.GearGenerator;

public class SGearConfigItem extends ConfigItem {
    @Override
    public ItemStack create(ItemStack stack, int level) {
        ItemStack result = super.create(stack, level);
        final int r1 = SilentGear.random.nextInt(PartManager.getHighestMainPartTier() + 1);
        final int r2 = SilentGear.random.nextInt(PartManager.getHighestMainPartTier() + 1);
        final int tier = (r1 + r2) / 2;
        return GearGenerator.randomizeParts(result, tier);
    }
}
