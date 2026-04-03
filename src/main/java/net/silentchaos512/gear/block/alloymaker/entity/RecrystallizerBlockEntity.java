package net.silentchaos512.gear.block.alloymaker.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.block.alloymaker.AlloyMakerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.alloy.GemAlloyRecipe;
import net.silentchaos512.gear.util.Const;

public class RecrystallizerBlockEntity extends AlloyMakerBlockEntity<GemAlloyRecipe> {
    public RecrystallizerBlockEntity(BlockPos pos, BlockState state) {
        super(Const.GEM_ALLOY_MAKER_INFO, pos, state);
    }
}
