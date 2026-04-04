package net.silentchaos512.gear.block.alloymaker.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.block.alloymaker.AlloyMakerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.alloy.SuperAlloyRecipe;
import net.silentchaos512.gear.util.Const;

public class SuperMixerBlockEntity extends AlloyMakerBlockEntity<SuperAlloyRecipe> {
    public SuperMixerBlockEntity(BlockPos pos, BlockState state) {
        super(Const.SUPER_MIXER_INFO, pos, state);
    }
}
