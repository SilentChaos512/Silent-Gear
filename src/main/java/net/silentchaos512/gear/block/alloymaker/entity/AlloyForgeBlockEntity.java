package net.silentchaos512.gear.block.alloymaker.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.block.alloymaker.AlloyMakerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.alloy.MetalAlloyRecipe;
import net.silentchaos512.gear.util.Const;

public class AlloyForgeBlockEntity extends AlloyMakerBlockEntity<MetalAlloyRecipe> {
    public AlloyForgeBlockEntity(BlockPos pos, BlockState state) {
        super(Const.METAL_ALLOY_MAKER_INFO, pos, state);
    }
}
