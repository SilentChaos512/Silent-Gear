package net.silentchaos512.gear.block;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

import javax.annotation.Nonnull;
import java.util.Map;

public class FlaxPlant extends BlockCrops implements IRegistryObject {

    public FlaxPlant() {
        setUnlocalizedName(getName());
    }

    @Nonnull
    @Override
    protected Item getCrop() {
        return ModItems.flaxFiber;
    }

    @Nonnull
    @Override
    protected Item getSeed() {
        return ModItems.flaxseeds;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        // Always get one seed back
        drops.add(new ItemStack(getSeed()));

        int age = getAge(state);
        if (age >= 7) {
            // Seeds
            for (int i = 0; i < 1 + fortune; ++i)
                if (SilentGear.random.nextInt(15) <= age)
                    drops.add(new ItemStack(getSeed()));
            // Fibers
            int fiberCount = 2 + fortune + SilentGear.random.nextInt(3);
            for (int i = 0; i < fiberCount; ++i)
                drops.add(new ItemStack(getCrop()));
        }
    }

    @Override
    public void addRecipes(@Nonnull RecipeMaker recipes) {
    }

    @Override
    public void addOreDict() {
    }

    @Nonnull
    @Override
    public String getModId() {
        return SilentGear.MOD_ID;
    }

    @Nonnull
    @Override
    public String getName() {
        return "flax_plant";
    }

    @Override
    public void getModels(@Nonnull Map<Integer, ModelResourceLocation> models) {
        for (int i = 0; i < 4; ++i)
            models.put(i, new ModelResourceLocation(getFullName(), "age=" + i));
    }
}
