package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;

import java.util.Set;

public class GearPickaxeItem extends GearDiggerItem {
    private static final Set<Material> PICKAXE_EXTRA_MATERIALS = ImmutableSet.of(
            Material.STONE,
            Material.METAL,
            Material.HEAVY_METAL,
            Material.ICE,
            Material.ICE_SOLID,
            Material.GLASS,
            Material.BUILDABLE_GLASS,
            Material.PISTON,
            Material.DECORATION
    );

    public static final Set<ToolAction> ACTIONS_WITH_SPOON = GearHelper.makeToolActionSet(ToolActions.PICKAXE_DIG, ToolActions.SHOVEL_DIG);

    public GearPickaxeItem(GearType gearType) {
        this(gearType, PICKAXE_EXTRA_MATERIALS);
    }

    public GearPickaxeItem(GearType gearType, Set<Material> extraMaterials) {
        super(gearType, BlockTags.MINEABLE_WITH_PICKAXE, extraMaterials, GearHelper.getBaseItemProperties());
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }

        // TODO: Make a ToolActionTrait type?
        if (TraitHelper.hasTrait(stack, Const.Traits.SPOON)) {
            // Pickaxe with spoon upgrade can dig dirt and stuff
            return ACTIONS_WITH_SPOON.contains(toolAction);
        }

        // Normal, unbroken pickaxe
        return super.canPerformAction(stack, toolAction);
    }
}
