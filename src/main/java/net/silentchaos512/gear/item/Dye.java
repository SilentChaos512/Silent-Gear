package net.silentchaos512.gear.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.item.ItemSL;

public class Dye extends ItemSL {

    public Dye() {
        super(1, SilentGear.MOD_ID, "dye");
    }

    @Override
    public void addOreDict() {
        OreDictionary.registerOre("dyeBlue", new ItemStack(this));
    }
}
