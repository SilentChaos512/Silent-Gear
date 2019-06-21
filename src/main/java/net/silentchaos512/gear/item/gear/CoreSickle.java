package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.ForgeEventFactory;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CoreSickle extends ToolItem implements ICoreTool {
    public static final ToolType TOOL_TYPE = ToolType.get("sickle");

    // TODO: Durability use config
    private static final int DURABILITY_USAGE = 2;
    private static final int BREAK_RANGE = 4;
    private static final int HARVEST_RANGE = 2;
    private static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.CACTUS,
            Material.LEAVES,
            Material.PLANTS,
            Material.TALL_PLANTS,
            Material.WEB
    );

    public CoreSickle() {
        super(0, 0, ItemTier.DIAMOND, ImmutableSet.of(), GearHelper.getBuilder(TOOL_TYPE));
        GearHelper.addModelTypeProperty(this);
    }

    @Override
    public GearType getGearType() {
        return GearType.SICKLE;
    }

    @Override
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == ItemStats.MELEE_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(1));
        if (stat == ItemStats.ATTACK_SPEED)
            return Optional.of(StatInstance.makeBaseMod(-1.8f));
        if (stat == ItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(1));
        return Optional.empty();
    }

    //region Sickle harvesting

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        // Sickles can right-click to harvest an area of plants
        // TODO: I seem to recall this not working on servers. Maybe should be implemented like hammers?

        ItemStack sickle = context.getItem();
        if (GearHelper.isBroken(sickle)) return ActionResultType.PASS;

        if (!(context.getWorld() instanceof ServerWorld)) return ActionResultType.PASS;

        ServerWorld world = (ServerWorld) context.getWorld();
        BlockPos pos = context.getPos();

        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof IGrowable)) return ActionResultType.PASS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResultType.PASS;

        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, sickle);
        boolean somethingHarvested = false;
        final int radius = HARVEST_RANGE;

        for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
            for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
                BlockPos target = new BlockPos(x, pos.getY(), z);
                state = world.getBlockState(target);
                Block block = state.getBlock();

                if (block instanceof IGrowable && !(block instanceof DoublePlantBlock)) {
                    IGrowable crop = (IGrowable) block;
                    if (!crop.canGrow(world, target, state, world.isRemote)) {
                        // Fully grown crop, get the drops
                        NonNullList<ItemStack> drops = NonNullList.create();
                        drops.addAll(Block.getDrops(state, world, target, null, player, sickle));
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
                                Block.spawnAsEntity(world, pos, drop);
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
            GearHelper.attemptDamage(sickle, DURABILITY_USAGE, player, context.getHand());
            player.addExhaustion(0.02f); // TODO: Config?
        }
        return somethingHarvested ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack sickle, BlockPos pos, PlayerEntity player) {
        return onSickleStartBreak(sickle, pos, player, BREAK_RANGE);
    }

    boolean onSickleStartBreak(ItemStack sickle, BlockPos pos, PlayerEntity player, final int range) {
        if (GearHelper.isBroken(sickle)) return false;

        World world = player.world;
        BlockState state = world.getBlockState(pos);

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

    private boolean breakExtraBlock(ItemStack sickle, World world, BlockPos pos, PlayerEntity player) {
        if (world.isAirBlock(pos) || !(player instanceof ServerPlayerEntity))
            return false;

        ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
        BlockState state = player.world.getBlockState(pos);
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
                playerMP.connection.sendPacket(new SChangeBlockPacket(world, pos));
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

            playerMP.connection.sendPacket(new SChangeBlockPacket(world, pos));
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
    public int getDamageOnBlockBreak(ItemStack gear, World world, BlockState state, BlockPos pos) {
        return DURABILITY_USAGE;
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean canHarvestBlock(BlockState state) {
        return state.getMaterial() == Material.WEB;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
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
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, ItemStats.ENCHANTABILITY);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, GearHelper.calcDamageClamped(stack, damage));
        if (GearHelper.isBroken(stack)) {
            GearData.recalculateStats(stack, null);
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getStatInt(stack, ItemStats.DURABILITY);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
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
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return GearHelper.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
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
