package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.gear.CoreArmor;
import net.silentchaos512.gear.util.GearHelper;

@JEIPlugin
public class JeiPlugin implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(ModItems.toolHead, s -> ModItems.toolHead.getSubtypeKey(s));
//        subtypeRegistry.registerSubtypeInterpreter(ModItems.blueprint, s -> ModItems.blueprint.getOutputItemType(s));
    }

    @Override
    public void register(IModRegistry registry) {
        // Add "example recipes". We can't allow these to be crafted, but it's helpful to have them
        // show in JEI. Some people can't read...
        registry.addRecipes(ModItems.toolHead.getExampleRecipes(), VanillaRecipeCategoryUid.CRAFTING);
        for (ICoreItem item : ModItems.toolClasses.values())
            registry.addRecipes(GearHelper.getExampleRecipes(item), VanillaRecipeCategoryUid.CRAFTING);
        for (ICoreItem item : ModItems.armorClasses.values())
            if (item instanceof CoreArmor)
                registry.addRecipes(((CoreArmor) item).getExampleRecipes(), VanillaRecipeCategoryUid.CRAFTING);
    }
}
