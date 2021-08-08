package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CorePickaxe extends PickaxeItem implements ICoreTool {
    private static final Set<Material> BASE_EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.STONE,
            Material.HEAVY_METAL,
            Material.ICE,
            Material.METAL,
            Material.ICE_SOLID,
            Material.STONE
    );
    private static final Set<Material> EXTRA_EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.DECORATION,
            Material.GLASS,
            Material.PISTON,
            Material.BUILDABLE_GLASS
    );

    private static final ImmutableSet<ToolType> TOOL_CLASSES_BASE =
            ImmutableSet.of(ToolType.PICKAXE);
    private static final ImmutableSet<ToolType> TOOL_CLASSES_WITH_SPOON =
            ImmutableSet.of(ToolType.PICKAXE, ToolType.SHOVEL);

    private final Set<Material> extraMaterials;

    public CorePickaxe() {
        this(EXTRA_EFFECTIVE_MATERIALS);
    }

    public CorePickaxe(Set<Material> extraMaterials) {
        super(Tiers.DIAMOND, 0, 0, GearHelper.getBuilder(ToolType.PICKAXE));
        this.extraMaterials = extraMaterials;
    }

    @Override
    public GearType getGearType() {
        return GearType.PICKAXE;
    }

    //region Harvest tool overrides

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        // Forge ItemStack-sensitive version
        return canHarvestBlock(state, getStatInt(stack, ItemStats.HARVEST_LEVEL));
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        // Vanilla version... Not good because we can't get the actual harvest level.
        // Assume a very high level since we can't get the actual value.
        return canHarvestBlock(state, 10);
    }

    private boolean canHarvestBlock(BlockState state, int toolLevel) {
        // Wrong harvest level?
        if (state.getBlock().getHarvestLevel(state) > toolLevel)
            return false;
        // Included in base or extra materials?
        Material material = state.getMaterial();
        if (BASE_EFFECTIVE_MATERIALS.contains(material) || this.extraMaterials.contains(material))
            return true;
        return super.isCorrectToolForDrops(state);
    }
    //endregion

    //region Standard tool overrides

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return GearHelper.getDestroySpeed(stack, state, this.extraMaterials);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable Player player, @Nullable BlockState blockState) {
        return GearHelper.getHarvestLevel(stack, tool, blockState, this.extraMaterials);
    }
//    @Override
//    public void setHarvestLevel(String toolClass, int level) {
//        super.setHarvestLevel(toolClass, level);
//        GearHelper.setHarvestLevel(this, toolClass, level, this.toolClasses);
//    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearHelper.getEnchantability(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getStatInt(stack, ItemStats.DURABILITY);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
    }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        if (!GearHelper.isBroken(stack)) {
            if (TraitHelper.getTraitLevel(stack, Const.Traits.SPOON) > 0) {
                // Pickaxe with spoon
                return TOOL_CLASSES_WITH_SPOON;
            } else {
                // Normal pickaxe
                return TOOL_CLASSES_BASE;
            }
        }
        // Broken
        return Collections.emptySet();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return GearHelper.hitEntity(stack, target, attacker);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return GearHelper.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return GearHelper.onItemUse(context);
    }

    //endregion
}
