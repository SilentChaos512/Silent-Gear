package net.silentchaos512.gear.block.alloymaker;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.crafting.recipe.alloy.CrudeAlloyRecipe;
import net.silentchaos512.gear.setup.SgDataComponents;

public class CrudeAlloyMakerBlockEntity<R extends CrudeAlloyRecipe> extends AlloyMakerBlockEntity<R> {
    public CrudeAlloyMakerBlockEntity(AlloyMakerInfo<R> info, BlockPos pos, BlockState state) {
        super(info, pos, state);
    }

    @Override
    protected void applyModifiers(ItemStack result) {
        result.set(SgDataComponents.CRUDE, Unit.INSTANCE);
    }
}
