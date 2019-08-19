package net.silentchaos512.gear.compat.mineandslash;

import com.robertx22.mine_and_slash.config.compatible_items.ConfigItem;
import com.robertx22.mine_and_slash.saveclasses.item_classes.GearItemData;
import com.robertx22.mine_and_slash.uncommon.datasaving.Gear;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearGenerator;

public class SGearConfigItem extends ConfigItem {
    @Override
    public ItemStack create(ItemStack stack, int level) {
        ItemStack result = super.create(stack, level);

        if (GearData.getConstructionParts(result).isEmpty()) {
            // Set random drops as salvageable
            GearItemData gearItemData = Gear.Load(result);
            gearItemData.isSalvagable = true;
            Gear.Save(result, gearItemData);

            // And randomize parts, since this item has no construction data
            final int r1 = 1 + SilentGear.random.nextInt(PartManager.getHighestMainPartTier());
            final int r2 = 1 + SilentGear.random.nextInt(PartManager.getHighestMainPartTier());
            final int tier = (r1 + r2) / 2;
            return GearGenerator.randomizeParts(result, tier);
        }

        return result;
    }
}
