package net.silentchaos512.gear.item;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.item.ItemNamedSubtypesSorted;
import net.silentchaos512.lib.registry.RecipeMaker;

public class CraftingItem extends ItemNamedSubtypesSorted {

    public static final String BLUEPRINT_PAPER = "blueprint_paper";
    public static final String DIAMOND_SHARD = "diamond_shard";
    public static final String EMERALD_SHARD = "emerald_shard";
    public static final String SINEW = "sinew";
    public static final String SINEW_DRIED = "sinew_dried";
    public static final String SINEW_FIBER = "sinew_fiber";
    public static final String STICK_IRON = "stick_iron";
    public static final String STICK_STONE = "stick_stone";
    public static final String UPGRADE_BASE = "upgrade_base";

    public static final String[] NAMES = {BLUEPRINT_PAPER, UPGRADE_BASE, STICK_STONE, STICK_IRON, DIAMOND_SHARD,
            EMERALD_SHARD, SINEW, SINEW_DRIED, SINEW_FIBER};
    public static final String[] SORTED_NAMES = {BLUEPRINT_PAPER, UPGRADE_BASE, DIAMOND_SHARD, EMERALD_SHARD,
            STICK_STONE, STICK_IRON, SINEW, SINEW_DRIED, SINEW_FIBER};

    public final ItemStack blueprintPaper = getStack(BLUEPRINT_PAPER);
    public final ItemStack diamondShard = getStack(DIAMOND_SHARD);
    public final ItemStack emeraldShard = getStack(EMERALD_SHARD);
    public final ItemStack sinew = getStack(SINEW);
    public final ItemStack sinewDried = getStack(SINEW_DRIED);
    public final ItemStack sinewFiber = getStack(SINEW_FIBER);
    public final ItemStack stickIron = getStack(STICK_IRON);
    public final ItemStack stickStone = getStack(STICK_STONE);
    public final ItemStack upgradeBase = getStack(UPGRADE_BASE);

    public CraftingItem() {
        super(NAMES, SORTED_NAMES, SilentGear.MOD_ID, "crafting_item");
    }

    @Override
    public void addRecipes(RecipeMaker recipes) {
        recipes.addShapelessOre(BLUEPRINT_PAPER, getStack(BLUEPRINT_PAPER, 8), "paper", "paper", "paper", "paper", "dyeBlue");
        recipes.addShapelessOre(UPGRADE_BASE, getStack(UPGRADE_BASE, 4), "paper", "paper", "plankWood", "flint");
        recipes.addShapedOre(STICK_STONE, getStack(STICK_STONE, 4), "s", "s", 's', "cobblestone");
        recipes.addShapedOre(STICK_IRON, getStack(STICK_IRON, 4), "i", "i", 'i', "ingotIron");
        recipes.addCompressionOre(DIAMOND_SHARD, diamondShard, new ItemStack(Items.DIAMOND), "nuggetDiamond", "gemDiamond", 9);
        recipes.addCompressionOre(EMERALD_SHARD, emeraldShard, new ItemStack(Items.EMERALD), "nuggetEmerald", "gemEmerald", 9);
        // TODO: Sinew drying
        recipes.addShapeless(SINEW_DRIED + "_temp", sinewDried, sinew);
        recipes.addShapeless(SINEW_FIBER, getStack(SINEW_FIBER, 3), getStack(SINEW_DRIED));
    }

    @Override
    public void addOreDict() {
        OreDictionary.registerOre("stickStone", stickStone);
        OreDictionary.registerOre("stickIron", stickIron);
        OreDictionary.registerOre("nuggetDiamond", diamondShard);
        OreDictionary.registerOre("nuggetEmerald", emeraldShard);
        OreDictionary.registerOre("string", sinewFiber);
    }
}
