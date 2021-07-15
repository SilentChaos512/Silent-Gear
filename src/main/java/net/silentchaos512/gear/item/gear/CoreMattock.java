package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CoreMattock extends HoeItem implements ICoreTool {
    private static final Set<ToolType> TOOL_CLASSES = ImmutableSet.of(ToolType.HOE, ToolType.AXE, ToolType.SHOVEL);
    private static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.LEAVES,
            Material.PLANT,
            Material.REPLACEABLE_PLANT,
            Material.GRASS,
            Material.DIRT,
            Material.CLAY,
            Material.SAND,
            Material.TOP_SNOW,
            Material.VEGETABLE,
            Material.WOOD
    );

    public CoreMattock() {
        super(ItemTier.DIAMOND, 0, 0f, GearHelper.getBuilder(null)
                .addToolType(ToolType.HOE, 3)
                .addToolType(ToolType.AXE, 3)
                .addToolType(ToolType.SHOVEL, 3)
        );
    }

    @Override
    public GearType getGearType() {
        return GearType.MATTOCK;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        if (GearHelper.isBroken(stack)) return ActionResultType.PASS;

        // Try to let traits do their thing first
        ActionResultType result = GearHelper.onItemUse(context);
        if (result == ActionResultType.PASS)
            return super.useOn(context);
        return result;
    }

//    @Override
//    protected void setBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, BlockState state) {
//        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
//
//        if (!worldIn.isRemote) {
//            worldIn.setBlockState(pos, state, 11);
//            GearHelper.attemptDamage(stack, 1, player);
//        }
//    }

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
        // Included in effective materials?
        return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || super.isCorrectToolForDrops(state);
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return GearHelper.getDestroySpeed(stack, state, EFFECTIVE_MATERIALS);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return GearHelper.getHarvestLevel(stack, tool, blockState, EFFECTIVE_MATERIALS);
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
    public ITextComponent getName(ItemStack stack) {
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
        return GearHelper.isBroken(stack) ? ImmutableSet.of() : TOOL_CLASSES;
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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    @Override
    public boolean mineBlock(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return GearHelper.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    //endregion
}
