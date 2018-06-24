package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.silentchaos512.gear.init.ModItems;

@JEIPlugin
public class JeiPlugin implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(ModItems.toolHead, s -> ModItems.toolHead.getSubtypeKey(s));
        subtypeRegistry.registerSubtypeInterpreter(ModItems.blueprint, s -> ModItems.blueprint.getOutputItemType(s));
    }
}
