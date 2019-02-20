package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.ForgeEventFactory;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class CoreSickle extends ItemTool implements ICoreTool {
    public static final ToolType TOOL_TYPE = ToolType.get("sickle");

    // TODO: Durability use config
    private static final int DURABILITY_USAGE = 2;
    private static final int BREAK_RANGE = 4;
    private static final int HARVEST_RANGE = 2;
    private static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.CACTUS,
            Material.LEAVES,
            Material.PLANTS,
            Material.VINE,
            Material.WEB
    );

    public CoreSickle() {
        super(0, 0, ItemTier.DIAMOND, ImmutableSet.of(), GearHelper.getBuilder(TOOL_TYPE));
    }

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.sickle;
    }

    @Override
    public String getGearClass() {
        return "sickle";
    }

    //region Sickle harvesting

    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {
        // Sickles can right-click to harvest an area of plants
        // TODO: I seem to recall this not working on servers. Maybe should be implemented like hammers?

        ItemStack sickle = context.getItem();
        if (GearHelper.isBroken(sickle)) return EnumActionResult.PASS;

        World world = context.getWorld();
        BlockPos pos = context.getPos();

        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof IGrowable)) return EnumActionResult.PASS;

        boolean somethingHarvested = false;
        final int radius = HARVEST_RANGE;
        @Nullable EntityPlayer player = context.getPlayer();
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, sickle);

        for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
            for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
                BlockPos target = new BlockPos(x, pos.getY(), z);
                state = world.getBlockState(target);
                Block block = state.getBlock();

                if (block instanceof IGrowable && !(block instanceof BlockDoublePlant)) {
                    IGrowable crop = (IGrowable) block;
                    if (!crop.canGrow(world, target, state, world.isRemote)) {
                        // Fully grown crop, get the drops
                        NonNullList<ItemStack> drops = NonNullList.create();
                        block.getDrops(state, drops, world, target, fortune);

                        ForgeEventFactory.fireBlockHarvesting(drops, world, target, state, fortune, 1, false, player);

                        // Spawn drops in world, remove first seed
                        boolean foundSeed = false;
                        for (ItemStack drop : drops) {
                            if (!foundSeed && drop.getItem() instanceof IPlantable) {
                                IPlantable seed = (IPlantable) drop.getItem();
                                if (seed.getPlant(world, target) == block.getDefaultState()) {
                                    foundSeed = true;
                                }
                            } else {
                                Block.spawnAsEntity(world, target, drop);
                            }
                        }

                        // Reset to default state
                        world.setBlockState(target, block.getDefaultState(), 2);
                        somethingHarvested = true;
                    }
                }
            }
        }

        if (somethingHarvested) {
            GearHelper.attemptDamage(sickle, DURABILITY_USAGE, player);
            if (player != null) {
                player.addExhaustion(0.02f); // TODO: Config?
            }
        }
        return somethingHarvested ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack sickle, BlockPos pos, EntityPlayer player) {
        return onSickleStartBreak(sickle, pos, player, BREAK_RANGE);
    }

    boolean onSickleStartBreak(ItemStack sickle, BlockPos pos, EntityPlayer player, final int range) {
        if (GearHelper.isBroken(sickle)) return false;

        World world = player.world;
        IBlockState state = world.getBlockState(pos);

        if (!EFFECTIVE_MATERIALS.contains(state.getMaterial())) return false;

        int blocksBroken = 1;

        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();

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

        if (playerMP.abilities.isCreativeMode) {
            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, playerMP, false, state.getFluidState())) {
                block.onPlayerDestroy(world, pos, state);
            }
            if (!world.isRemote) {
                playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
            }
            return true;
        }

        if (!world.isRemote) {
            block.onBlockHarvested(world, pos, state, playerMP);

            if (block.removedByPlayer(state, world, pos, playerMP, true, state.getFluidState())) {
                block.onPlayerDestroy(world, pos, state);
                block.harvestBlock(world, player, pos, state, null, sickle);
                block.dropXpOnBlockBreak(world, pos, xpDropped);
            }

            playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
        } else {
            world.playEvent(2001, pos, Block.getStateId(state));
            if (block.removedByPlayer(state, world, pos, playerMP, true, state.getFluidState())) {
                block.onPlayerDestroy(world, pos, state);
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
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
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, GearHelper.calcDamageClamped(stack, damage));
        if (GearHelper.isBroken(stack)) {
            GearData.recalculateStats(null, stack);
        }
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
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
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
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    //endregion
}
