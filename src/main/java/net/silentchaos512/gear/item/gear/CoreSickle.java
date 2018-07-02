package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.registry.IRegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoreSickle extends ItemTool implements IRegistryObject, ICoreTool {

    // TODO: Durability use config
    private static final int DURABILITY_USAGE = 2;
    private static final int BREAK_RANGE = 4;
    private static final int HARVEST_RANGE = 2;
    private static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(Material.CACTUS, Material.LEAVES, Material.PLANTS, Material.VINE, Material.WEB);

    public CoreSickle() {
        super(GearData.FAKE_MATERIAL, ImmutableSet.of());
        setUnlocalizedName(getFullName());
        setNoRepair();
    }

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.sickle;
    }

    @Nonnull
    @Override
    public String getModId() {
        return SilentGear.MOD_ID;
    }

    @Nonnull
    @Override
    public String getName() {
        return getGearClass();
    }

    @Override
    public String getGearClass() {
        return "sickle";
    }

    @Override
    public void getModels(@Nonnull Map<Integer, ModelResourceLocation> models) {
        models.put(0, new ModelResourceLocation(getFullName(), "inventory"));
    }

    //region Sickle harvesting

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack sickle = player.getHeldItem(hand);

        if (GearHelper.isBroken(sickle))
            return EnumActionResult.PASS;

        IBlockState state = worldIn.getBlockState(pos);
        if (!(state.getBlock() instanceof IGrowable))
            return EnumActionResult.PASS;

        boolean flag = false;
        final int radius = HARVEST_RANGE;
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, sickle);
        BlockPos target;
        Block block;

        for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
            for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
                target = new BlockPos(x, pos.getY(), z);
                state = worldIn.getBlockState(target);
                block = state.getBlock();

                if (block instanceof IGrowable && !(block instanceof BlockDoublePlant)) {
                    IGrowable crop = (IGrowable) block;
                    if (!crop.canGrow(worldIn, target, state, worldIn.isRemote)) {
                        // Fully grown crop, get the drops
                        NonNullList<ItemStack> drops = NonNullList.create();
                        block.getDrops(drops, worldIn, target, state, fortune);

                        ForgeEventFactory.fireBlockHarvesting(drops, worldIn, target, state, fortune, 1, false, player);

                        // Spawn drops in world, remove first seed
                        boolean foundSeed = false;
                        for (ItemStack drop : drops) {
                            if (!foundSeed && drop.getItem() instanceof IPlantable) {
                                IPlantable seed = (IPlantable) drop.getItem();
                                if (seed.getPlant(worldIn, target) == block.getDefaultState()) {
                                    foundSeed = true;
                                }
                            } else {
                                Block.spawnAsEntity(worldIn, target, drop);
                            }
                        }

                        // Reset to default state
                        worldIn.setBlockState(target, block.getDefaultState(), 2);
                        flag = true;
                    }
                }
            }
        }

        if (flag) {
            GearHelper.attemptDamage(sickle, DURABILITY_USAGE, player);
            player.addExhaustion(0.02f);
        }
        return flag ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack sickle, BlockPos pos, EntityPlayer player) {
        if (GearHelper.isBroken(sickle)) {
            return false;
        }

        World world = player.world;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!EFFECTIVE_MATERIALS.contains(state.getMaterial())) {
            return false;
        }

        int blocksBroken = 1;

        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final int range = BREAK_RANGE;

        for (int xPos = x - range; xPos <= x + range; ++xPos) {
            for (int zPos = z - range; zPos <= z + range; ++zPos) {
                BlockPos target = new BlockPos(xPos, y, zPos);
                if (!(xPos == x && zPos == z) && world.getBlockState(target) == state && breakExtraBlock(sickle, world, target, player)) {
                    ++blocksBroken;
                }
            }
        }

        return super.onBlockStartBreak(sickle, pos, player);
    }

    private boolean breakExtraBlock(ItemStack sickle, World world, BlockPos pos, EntityPlayer player) {
        if (world.isAirBlock(pos) || !(player instanceof EntityPlayerMP))
            return false;

        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();

        if (!EFFECTIVE_MATERIALS.contains(state.getMaterial()))
            return false;

        int xpDropped = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
        boolean canceled = xpDropped == -1;
        if (canceled)
            return false;

        if (playerMP.capabilities.isCreativeMode) {
            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, playerMP, false)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }
            if (!world.isRemote) {
                playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
            }
            return true;
        }

        if (!world.isRemote) {
            block.onBlockHarvested(world, pos, state, playerMP);

            if (block.removedByPlayer(state, world, pos, playerMP, true)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
                block.harvestBlock(world, player, pos, state, null, sickle);
                block.dropXpOnBlockBreak(world, pos, xpDropped);
            }

            playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
        } else {
            int meta = block.getMetaFromState(state);
            world.playEvent(2001, pos, Block.getIdFromBlock(block) + (meta << 12));
            if (block.removedByPlayer(state, world, pos, playerMP, true)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }

            sickle.onBlockDestroyed(world, state, pos, playerMP);
        }

        return true;
    }

    @Override
    public int getDamageOnBlockBreak(ItemStack gear, World world, IBlockState state, BlockPos pos) {
        return DURABILITY_USAGE;
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean canHarvestBlock(IBlockState state) {
        return state.getMaterial() == Material.WEB;
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
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState state) {
        return super.getHarvestLevel(stack, toolClass, player, state) < 0 ? 0 : GearData.getStatInt(stack, CommonItemStats.HARVEST_LEVEL);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.ENCHANTABILITY);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return GearHelper.getItemStackDisplayName(stack);
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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab))
            GearHelper.getSubItems(this, tab, subItems);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of();
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
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        return GearHelper.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    //endregion
}
