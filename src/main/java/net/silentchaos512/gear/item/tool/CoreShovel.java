package net.silentchaos512.gear.item.tool;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.EquipmentData;
import net.silentchaos512.gear.util.EquipmentHelper;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

import javax.annotation.Nonnull;
import java.util.Map;

public class CoreShovel extends ItemSpade implements IRegistryObject, ICoreTool {

    public CoreShovel() {
        super(EquipmentData.FAKE_MATERIAL);
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
        return getItemClassName();
    }

    @Override
    public String getItemClassName() {
        return "shovel";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return EquipmentHelper.getItemStackDisplayName(stack);
    }

    @Override
    public void getModels(Map<Integer, ModelResourceLocation> models) {
        models.put(0, new ModelResourceLocation(getFullName(), "inventory"));
    }
}
