package net.silentchaos512.gear.compat.mineandslash;

public class SGearConfigItem /*extends ConfigItem*/ {
/*    @Override
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
    }*/
}
