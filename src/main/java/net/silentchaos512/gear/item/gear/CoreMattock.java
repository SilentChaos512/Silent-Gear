package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoreMattock extends ItemHoe implements IRegistryObject, ICoreTool {

    public static final Set<Material> BASE_EFFECTIVE_MATERIALS = Sets.newHashSet(Material.GOURD, Material.WOOD);
    public static final Material[] EXTRA_EFFECTIVE_MATERIALS = {Material.WOOD, Material.LEAVES, Material.PLANTS, Material.VINE};

    public CoreMattock() {
        super(GearData.FAKE_MATERIAL);
        setUnlocalizedName(getFullName());
        setNoRepair();
    }

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.mattock;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return GearHelper.getDestroySpeed(stack, state, EXTRA_EFFECTIVE_MATERIALS);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, EntityPlayer player, IBlockState state) {
        if (!("shovel".equals(toolClass) || "axe".equals(toolClass)) || GearHelper.isBroken(stack))
            return -1;
        return GearData.getStatInt(stack, CommonItemStats.HARVEST_LEVEL);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return GearHelper.isBroken(stack) ? ImmutableSet.of() : ImmutableSet.of("shovel", "axe");
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.ENCHANTABILITY);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        return GearHelper.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return GearHelper.hitEntity(stack, target, attacker);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    // Forge ItemStack-sensitive version
    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack tool) {
        return canHarvestBlock(state, getStatInt(tool, CommonItemStats.HARVEST_LEVEL));
    }

    // Vanilla version... Not good because we can't get the actual harvest level.
    @Override
    public boolean canHarvestBlock(IBlockState state) {
        // Assume a very high level since we can't get the actual value.
        return canHarvestBlock(state, 10);
    }

    protected boolean canHarvestBlock(IBlockState state, int toolLevel) {
        // Wrong harvest level?
        if (state.getBlock().getHarvestLevel(state) > toolLevel)
            return false;
        // Included in base materials?
        if (BASE_EFFECTIVE_MATERIALS.contains(state.getMaterial()))
            return true;
        // Included in extra materials?
        for (Material mat : EXTRA_EFFECTIVE_MATERIALS)
            if (mat.equals(state.getMaterial()))
                return true;

        return super.canHarvestBlock(state);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        if (GearHelper.isBroken(stack))
            return EnumActionResult.PASS;

        // TODO

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    protected void setBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state) {
        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, state, 11);
            GearHelper.attemptDamage(stack, 1, player);
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        boolean canceled = super.onBlockStartBreak(stack, pos, player);
        if (!canceled) {
            GearHelper.onBlockStartBreak(stack, pos, player);
        }
        return canceled;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        GearHelper.onUpdate(stack, world, entity, itemSlot, isSelected);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        // TODO Auto-generated method stub
        return super.onEntityItemUpdate(entityItem);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.DURABILITY);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        // TODO Auto-generated method stub
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        // TODO
        return super.hasEffect(stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return GearClientHelper.getRarity(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return GearHelper.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public FontRenderer getFontRenderer(ItemStack stack) {
        // return CustomFontRenderer.INSTANCE;
        return super.getFontRenderer(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        // TODO Auto-generated method stub
        super.getSubItems(tab, items);
    }

    @Override
    public void addRecipes(RecipeMaker recipes) {
    }

    @Override
    public void addOreDict() {
    }

    @Override
    public String getModId() {
        return SilentGear.MOD_ID;
    }

    @Override
    public String getName() {
        return "mattock";
    }

    @Override
    public void getModels(Map<Integer, ModelResourceLocation> models) {
        // TODO Auto-generated method stub
        models.put(0, new ModelResourceLocation(getFullName(), "inventory"));
    }

    @Override
    public String getGearClass() {
        return "mattock";
    }
}
