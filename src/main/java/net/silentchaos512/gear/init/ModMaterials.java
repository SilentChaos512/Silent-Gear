package net.silentchaos512.gear.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

public class ModMaterials implements IPhasedInitializer {

    public static final ModMaterials INSTANCE = new ModMaterials();

    public static PartMain mainWood, mainStone, mainFlint, mainIron, mainGold, mainEmerald, mainDiamond, mainTest;
    public static PartRod rodWood, rodBone, rodStone, rodIron;
    public static PartTip tipIron, tipGold, tipDiamond, tipEmerald, tipRedstone, tipGlowstone, tipLapis;
    public static PartBowstring bowstringString, bowstringSinew;

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {

        mainWood = PartRegistry.putPart(new PartMain(getPath("main_wood")));
        mainStone = PartRegistry.putPart(new PartMain(getPath("main_stone")));
        mainFlint = PartRegistry.putPart(new PartMain(getPath("main_flint")));
        mainIron = PartRegistry.putPart(new PartMain(getPath("main_iron")));
        mainGold = PartRegistry.putPart(new PartMain(getPath("main_gold")));
        mainEmerald = PartRegistry.putPart(new PartMain(getPath("main_emerald")));
        mainDiamond = PartRegistry.putPart(new PartMain(getPath("main_diamond")));
        mainTest = PartRegistry.putPart(new PartMain(getPath("main_test")));

        rodWood = PartRegistry.putPart(new PartRod(getPath("rod_wood")));
        rodBone = PartRegistry.putPart(new PartRod(getPath("rod_bone")));
        rodStone = PartRegistry.putPart(new PartRod(getPath("rod_stone")));
        rodIron = PartRegistry.putPart(new PartRod(getPath("rod_iron")));

        tipIron = PartRegistry.putPart(new PartTip(getPath("tip_iron")));
        tipGold = PartRegistry.putPart(new PartTip(getPath("tip_gold")));
        tipDiamond = PartRegistry.putPart(new PartTip(getPath("tip_diamond")));
        tipEmerald = PartRegistry.putPart(new PartTip(getPath("tip_emerald")));
        tipRedstone = PartRegistry.putPart(new PartTip(getPath("tip_redstone")));
        tipGlowstone = PartRegistry.putPart(new PartTip(getPath("tip_glowstone")));
        tipLapis = PartRegistry.putPart(new PartTip(getPath("tip_lapis")));

        bowstringString = PartRegistry.putPart(new PartBowstring(getPath("bowstring_string")));
        bowstringSinew = PartRegistry.putPart(new PartBowstring(getPath("bowstring_sinew")));
    }

    private ResourceLocation getPath(String key) {
        return new ResourceLocation(SilentGear.MOD_ID, key);
    }
}
