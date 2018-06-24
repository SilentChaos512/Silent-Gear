package net.silentchaos512.gear.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

public class ModMaterials implements IPhasedInitializer {

    public static final ModMaterials INSTANCE = new ModMaterials();

    public static ItemPartMain mainWood, mainStone, mainFlint, mainIron, mainGold, mainEmerald, mainDiamond, mainTest;
    public static ToolPartRod rodWood, rodBone, rodStone, rodIron;
    public static ToolPartTip tipIron, tipGold, tipDiamond, tipEmerald, tipRedstone, tipGlowstone, tipLapis;
    public static BowPartString bowstringString, bowstringSinew;

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {

        mainWood = PartRegistry.putPart(new ItemPartMain(getPath("main_wood")));
        mainStone = PartRegistry.putPart(new ItemPartMain(getPath("main_stone")));
        mainFlint = PartRegistry.putPart(new ItemPartMain(getPath("main_flint")));
        mainIron = PartRegistry.putPart(new ItemPartMain(getPath("main_iron")));
        mainGold = PartRegistry.putPart(new ItemPartMain(getPath("main_gold")));
        mainEmerald = PartRegistry.putPart(new ItemPartMain(getPath("main_emerald")));
        mainDiamond = PartRegistry.putPart(new ItemPartMain(getPath("main_diamond")));
        mainTest = PartRegistry.putPart(new ItemPartMain(getPath("main_test")));

        rodWood = PartRegistry.putPart(new ToolPartRod(getPath("rod_wood")));
        rodBone = PartRegistry.putPart(new ToolPartRod(getPath("rod_bone")));
        rodStone = PartRegistry.putPart(new ToolPartRod(getPath("rod_stone")));
        rodIron = PartRegistry.putPart(new ToolPartRod(getPath("rod_iron")));

        tipIron = PartRegistry.putPart(new ToolPartTip(getPath("tip_iron")));
        tipGold = PartRegistry.putPart(new ToolPartTip(getPath("tip_gold")));
        tipDiamond = PartRegistry.putPart(new ToolPartTip(getPath("tip_diamond")));
        tipEmerald = PartRegistry.putPart(new ToolPartTip(getPath("tip_emerald")));
        tipRedstone = PartRegistry.putPart(new ToolPartTip(getPath("tip_redstone")));
        tipGlowstone = PartRegistry.putPart(new ToolPartTip(getPath("tip_glowstone")));
        tipLapis = PartRegistry.putPart(new ToolPartTip(getPath("tip_lapis")));

        bowstringString = PartRegistry.putPart(new BowPartString(getPath("bowstring_string")));
        bowstringSinew = PartRegistry.putPart(new BowPartString(getPath("bowstring_sinew")));
    }

    private ResourceLocation getPath(String key) {
        return new ResourceLocation(SilentGear.MOD_ID, key);
    }
}
