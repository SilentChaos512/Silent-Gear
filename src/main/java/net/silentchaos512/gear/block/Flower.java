package net.silentchaos512.gear.block;

import net.minecraft.block.BlockBush;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

import java.util.Map;

public class Flower extends BlockBush implements IRegistryObject {

    public Flower() {
        setUnlocalizedName(getName());
    }

    @Override
    public void addRecipes(RecipeMaker recipes) {
        recipes.addShapeless("dye_blue", new ItemStack(ModItems.dye, 2), new ItemStack(this));
    }

    @Override
    public void addOreDict() {
    }

    @Override
    public String getModId() {
        return SilentGear.MOD_ID;
    }

    @Override
    public String getName() {
        return "flower";
    }

    @Override
    public void getModels(Map<Integer, ModelResourceLocation> models) {
        models.put(0, new ModelResourceLocation(getFullName(), "inventory"));
    }
}
