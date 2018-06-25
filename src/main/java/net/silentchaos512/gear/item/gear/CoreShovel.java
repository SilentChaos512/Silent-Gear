package net.silentchaos512.gear.item.gear;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

import javax.annotation.Nonnull;
import java.util.Map;

public class CoreShovel extends ItemSpade implements IRegistryObject, ICoreTool {

    public CoreShovel() {
        super(GearData.FAKE_MATERIAL);
        setUnlocalizedName(getFullName());
    }

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.shovel;
    }

    /*
     * IRegistryObject
     */

    @Override
    public void addRecipes(RecipeMaker recipes) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addOreDict() {
    }

    @Override
    public String getModId() {
        return SilentGear.MOD_ID;
    }

    @Nonnull
    @Override
    public String getName() {
        return getGearClass();
    }

    @Override
    public String getGearClass() {
        return "shovel";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return GearHelper.getItemStackDisplayName(stack);
    }

    @Override
    public void getModels(Map<Integer, ModelResourceLocation> models) {
        models.put(0, new ModelResourceLocation(getFullName(), "inventory"));
    }
}
