package net.silentchaos512.gear.block.alloymaker.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.block.alloymaker.AlloyMakerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.alloy.CrudeAlloyRecipe;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.util.Const;

public class CrudeMixerBlockEntity extends AlloyMakerBlockEntity<CrudeAlloyRecipe> {
    public CrudeMixerBlockEntity(BlockPos pos, BlockState state) {
        super(Const.CRUDE_MIXER_INFO, pos, state);
    }

    @Override
    protected void applyModifiers(ItemStack result) {
        result.set(SgDataComponents.CRUDE, Unit.INSTANCE);
    }
}
