package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class CoreMattock extends ItemHoe implements ICoreTool {
    private static Set<ToolType> TOOL_CLASSES = ImmutableSet.of(ToolType.AXE, ToolType.SHOVEL);
    private static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.LEAVES,
            Material.PLANTS,
            Material.VINE,
            Material.GRASS,
            Material.GROUND,
            Material.CLAY,
            Material.SAND,
            Material.SNOW,
            Material.GOURD,
            Material.WOOD
    );

    public CoreMattock() {
        super(ItemTier.DIAMOND, 0, GearHelper.getBuilder(null)
                .addToolType(ToolType.AXE, 3)
                .addToolType(ToolType.SHOVEL, 3)
        );
    }

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.mattock;
    }

    @Override
    public String getGearClass() {
        return "mattock";
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        if (GearHelper.isBroken(stack)) return EnumActionResult.PASS;
        return super.onItemUse(context);
    }

//    @Override
//    protected void setBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state) {
//        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
//
//        if (!worldIn.isRemote) {
//            worldIn.setBlockState(pos, state, 11);
//            GearHelper.attemptDamage(stack, 1, player);
//        }
//    }

    //region Harvest tool overrides


    @Override
    public boolean canHarvestBlock(ItemStack stack, IBlockState state) {
        // Forge ItemStack-sensitive version
        return canHarvestBlock(state, getStatInt(stack, CommonItemStats.HARVEST_LEVEL));
    }

    @Override
    public boolean canHarvestBlock(IBlockState state) {
        // Vanilla version... Not good because we can't get the actual harvest level.
        // Assume a very high level since we can't get the actual value.
        return canHarvestBlock(state, 10);
    }

    private boolean canHarvestBlock(IBlockState state, int toolLevel) {
        // Wrong harvest level?
        if (state.getBlock().getHarvestLevel(state) > toolLevel)
            return false;
        // Included in effective materials?
        return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || super.canHarvestBlock(state);
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return GearHelper.getDestroySpeed(stack, state, EFFECTIVE_MATERIALS);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        return GearHelper.getHarvestLevel(stack, tool, blockState, EFFECTIVE_MATERIALS);
    }

//    @Override
//    public void setHarvestLevel(String toolClass, int level) {
//        super.setHarvestLevel(toolClass, level);
//        GearHelper.setHarvestLevel(this, toolClass, level, this.toolClasses);
//    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.ENCHANTABILITY);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.DURABILITY);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
    }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        return GearHelper.isBroken(stack) ? ImmutableSet.of() : TOOL_CLASSES;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return GearHelper.hitEntity(stack, target, attacker);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
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
