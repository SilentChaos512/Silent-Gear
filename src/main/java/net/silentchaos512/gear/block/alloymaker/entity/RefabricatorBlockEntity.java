package net.silentchaos512.gear.block.alloymaker.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.block.alloymaker.AlloyMakerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.alloy.FabricAlloyRecipe;
import net.silentchaos512.gear.util.Const;

public class RefabricatorBlockEntity extends AlloyMakerBlockEntity<FabricAlloyRecipe> {
    public RefabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(Const.FABRIC_ALLOY_MAKER_INFO, pos, state);
    }
}
