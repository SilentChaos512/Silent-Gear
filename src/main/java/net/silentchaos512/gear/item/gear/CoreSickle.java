package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class CoreSickle extends DiggerItem implements ICoreTool {
    public static final ToolType TOOL_TYPE = ToolType.get("sickle");

    private static final int DURABILITY_USAGE = 3;
    private static final int BREAK_RANGE = 4;
    private static final int HARVEST_RANGE = 2;
    static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.CACTUS,
            Material.LEAVES,
            Material.PLANT,
            Material.REPLACEABLE_PLANT,
            Material.WEB,
            Material.BAMBOO_SAPLING
    );
    private static final Map<Block, BlockState> HARVEST_STATES = new HashMap<>();

    static {
        putHarvestState(Blocks.SWEET_BERRY_BUSH, Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 1));
    }

    /**
     * When a block is harvested by right-clicking with a sickle, the block is normally reset to the
     * default state. This can be used to override that with a different state.
     *
     * @param block The block to match
     * @param state The state to set after harvesting
     */
    public static void putHarvestState(Block block, BlockState state) {
        HARVEST_STATES.put(block, state);
    }

    public CoreSickle() {
        super(0, 0, Tiers.DIAMOND, BlockTags.LEAVES, GearHelper.getBuilder(TOOL_TYPE));
    }

    @Override
    public GearType getGearType() {
        return GearType.SICKLE;
    }

    //region Sickle harvesting

    private static boolean canRightClickHarvestBlock(BlockState state) {
        Block block = state.getBlock();
        return block instanceof IPlantable && block instanceof BonemealableBlock;
    }

    private static boolean tryHarvest(ServerLevel world, BlockPos pos, BlockState state, Player player, ItemStack sickle, int fortune) {
        Block block = state.getBlock();
        BonemealableBlock growable = (BonemealableBlock) block;

        if (!growable.isValidBonemealTarget(world, pos, state, world.isClientSide)) {
            // Fully grown crop
            NonNullList<ItemStack> drops = NonNullList.create();
            drops.addAll(Block.getDrops(state, world, pos, null, player, sickle));
            //ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, fortune, 1, false, player);

            // Spawn drops in world, remove first seed
            boolean foundSeed = false;
            for (ItemStack drop : drops) {
                Item item = drop.getItem();
                if (!foundSeed && item instanceof BlockItem && ((BlockItem) item).getBlock() == block) {
                    foundSeed = true;
                } else {
                    Block.popResource(world, pos, drop);
                }
            }

            // Reset state
            world.setBlock(pos, HARVEST_STATES.getOrDefault(block, block.defaultBlockState()), 2);
            return true;
        }

        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Sickles can right-click to harvest an area of plants
        ItemStack sickle = context.getItemInHand();
        if (GearHelper.isBroken(sickle) || !(context.getLevel() instanceof ServerLevel))
            return InteractionResult.PASS;

        ServerLevel world = (ServerLevel) context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (!canRightClickHarvestBlock(state)) return InteractionResult.PASS;

        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, sickle);
        int harvestCount = 0;
        final int radius = HARVEST_RANGE;

        for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
            for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
                BlockPos target = new BlockPos(x, pos.getY(), z);
                state = world.getBlockState(target);

                if (canRightClickHarvestBlock(state) && tryHarvest(world, target, state, player, sickle, fortune)) {
                    ++harvestCount;
                }
            }
        }

        if (harvestCount > 0) {
            GearHelper.attemptDamage(sickle, DURABILITY_USAGE, player, context.getHand());
            player.causeFoodExhaustion(0.02f);
            return InteractionResult.SUCCESS;
        }

        return GearHelper.onItemUse(context);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack sickle, BlockPos pos, Player player) {
        return onSickleStartBreak(sickle, pos, player, BREAK_RANGE, EFFECTIVE_MATERIALS);
    }

    boolean onSickleStartBreak(ItemStack sickle, BlockPos pos, Player player, final int range, Set<Material> effectiveMaterials) {
        if (GearHelper.isBroken(sickle)) return false;

        Level world = player.level;
        BlockState state = world.getBlockState(pos);

        if (!effectiveMaterials.contains(state.getMaterial())) return false;

        int blocksBroken = 1;

        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();

        for (int xPos = x - range; xPos <= x + range; ++xPos) {
            for (int zPos = z - range; zPos <= z + range; ++zPos) {
                BlockPos target = new BlockPos(xPos, y, zPos);
                if (!(xPos == x && zPos == z) && world.getBlockState(target) == state && breakExtraBlock(sickle, world, target, player, effectiveMaterials)) {
                    ++blocksBroken;
                }
            }
        }

        return super.onBlockStartBreak(sickle, pos, player);
    }

    private static boolean breakExtraBlock(ItemStack sickle, Level world, BlockPos pos, Player player, Set<Material> effectiveMaterials) {
        if (world.isEmptyBlock(pos) || !(player instanceof ServerPlayer)) return false;

        ServerPlayer playerMP = (ServerPlayer) player;
        BlockState state = player.level.getBlockState(pos);
        Block block = state.getBlock();

        if (!effectiveMaterials.contains(state.getMaterial())) return false;

        int xpDropped = ForgeHooks.onBlockBreakEvent(world, playerMP.gameMode.getGameModeForPlayer(), playerMP, pos);
        boolean canceled = xpDropped == -1;
        if (canceled) return false;

        if (playerMP.getAbilities().instabuild) {
            block.playerWillDestroy(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, playerMP, false, state.getFluidState())) {
                block.destroy(world, pos, state);
            }
            if (!world.isClientSide) {
                playerMP.connection.send(new ClientboundBlockUpdatePacket(world, pos));
            }
            return true;
        }

        if (!world.isClientSide && world instanceof ServerLevel) {
            block.playerWillDestroy(world, pos, state, playerMP);

            if (block.removedByPlayer(state, world, pos, playerMP, true, state.getFluidState())) {
                block.destroy(world, pos, state);
                block.playerDestroy(world, player, pos, state, null, sickle);
                block.popExperience((ServerLevel) world, pos, xpDropped);
            }

            playerMP.connection.send(new ClientboundBlockUpdatePacket(world, pos));
        } else {
            world.levelEvent(2001, pos, Block.getId(state));
            if (block.removedByPlayer(state, world, pos, playerMP, true, state.getFluidState())) {
                block.destroy(world, pos, state);
            }

            sickle.mineBlock(world, state, pos, playerMP);
        }

        return true;
    }

    @Override
    public int getDamageOnBlockBreak(ItemStack gear, Level world, BlockState state, BlockPos pos) {
        return DURABILITY_USAGE;
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return state.getMaterial() == Material.WEB;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return GearHelper.getDestroySpeed(stack, state, EFFECTIVE_MATERIALS);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable Player player, @Nullable BlockState blockState) {
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
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
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

    //endregion
}
