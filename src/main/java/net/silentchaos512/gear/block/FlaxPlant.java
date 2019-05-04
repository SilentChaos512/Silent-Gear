package net.silentchaos512.gear.block;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.lib.registry.ICustomModel;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FlaxPlant extends BlockCrops implements ICustomModel {

    @Nonnull
    @Override
    protected Item getCrop() {
        return CraftingItems.FLAX_FIBER.getItem();
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
                if (SilentGear.RANDOM.nextInt(15) <= age)
                    drops.add(new ItemStack(getSeed()));
            // Fibers
            int fiberCount = 2 + fortune + SilentGear.RANDOM.nextInt(3);
            for (int i = 0; i < fiberCount; ++i)
                drops.add(new ItemStack(getCrop()));
        }
    }

    @Override
    public void registerModels() {
        Item item = Item.getItemFromBlock(this);
        for (int i = 0; i < 4; ++i) {
            ModelResourceLocation model = new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "age=" + i);
            ModelLoader.setCustomModelResourceLocation(item, i, model);
        }
    }
}
