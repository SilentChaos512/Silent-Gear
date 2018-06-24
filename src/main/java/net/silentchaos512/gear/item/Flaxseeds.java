package net.silentchaos512.gear.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

import javax.annotation.Nonnull;
import java.util.Map;

public class Flaxseeds extends ItemSeeds implements IRegistryObject {

    public Flaxseeds() {
        super(ModBlocks.flaxPlant, Blocks.FARMLAND);
        setUnlocalizedName(getFullName());
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return ModBlocks.flaxPlant.getDefaultState();
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Plains;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // TODO: Need to override?
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
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
        return "flaxseeds";
    }

    @Override
    public void getModels(@Nonnull Map<Integer, ModelResourceLocation> models) {
        models.put(0, new ModelResourceLocation(getFullName(), "inventory"));
    }
}
