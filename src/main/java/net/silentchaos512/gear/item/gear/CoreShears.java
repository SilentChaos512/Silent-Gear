package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CoreShears extends ShearsItem implements ICoreTool {
    public static final Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.ENCHANTABILITY,
            ItemStats.HARVEST_SPEED
    );

    public CoreShears() {
        super(GearHelper.getBuilder(null).durability(100));
    }

    @Override
    public GearType getGearType() {
        return GearType.SHEARS;
    }

    @Override
    public Set<ItemStat> getRelevantStats(ItemStack stack) {
        return RELEVANT_STATS;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (GearHelper.isBroken(stack)) {
            return 0f;
        }

        float speed = getStat(stack, ItemStats.HARVEST_SPEED);

        if (!state.is(Blocks.COBWEB) && !state.is(BlockTags.LEAVES)) {
            return state.is(BlockTags.WOOL) ? speed - 1 : 1;
        } else {
            return 2.5f * speed;
        }
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
        if (GearHelper.isBroken(stack)) {
            return ActionResultType.PASS;
        }
        return super.interactLivingEntity(stack, playerIn, entity, hand);
    }

    @Override
    public int getDamageOnBlockBreak(ItemStack gear, World world, BlockState state, BlockPos pos) {
        if (!state.getBlock().is(BlockTags.FIRE)) {
            return 1;
        }
        return ICoreTool.super.getDamageOnBlockBreak(gear, world, state, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearHelper.getEnchantability(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
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
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
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

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();

        if (player != null && getHoneyLevel(state) >= 5) {
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            BeehiveBlock.dropHoneycomb(world, pos);
            context.getItemInHand().hurtAndBreak(1, player, (playerEntity) -> {
                playerEntity.broadcastBreakEvent(context.getHand());
            });

            BeehiveBlock block = (BeehiveBlock) state.getBlock();
            if (!CampfireBlock.isSmokeyPos(world, pos)) {
                if (block.hiveContainsBees(world, pos)) {
                    block.angerNearbyBees(world, pos);
                }

                block.releaseBeesAndResetHoneyLevel(world, state, pos, player, BeehiveTileEntity.State.EMERGENCY);
            } else {
                block.resetHoneyLevel(world, state, pos);
            }

            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        return GearHelper.onItemUse(context);
    }

    private static int getHoneyLevel(BlockState state) {
        if (state.getBlock() instanceof BeehiveBlock && state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
            return state.getValue(BeehiveBlock.HONEY_LEVEL);
        }
        return 0;
    }
}
