package net.silentchaos512.gear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearFishingRodItem extends FishingRodItem implements GearItem {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableSet.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get(),
            PartTypes.CORD.get()
    ));

    private final Supplier<GearType> gearType;

    public GearFishingRodItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(properties);
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Broken fishing rods cannot be used
        if (GearHelper.isBroken(stack)) {
            return InteractionResult.PASS;
        }

        // Rewrite of super to spawn custom fishing hook entity
        if (player.fishing != null) {
            if (!level.isClientSide) {
                int i = player.fishing.retrieve(stack);
                ItemStack original = stack.copy();
                stack.hurtAndBreak(i, player, LivingEntity.getSlotForHand(hand));
                if(stack.isEmpty()) {
                    net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(player, original, hand);
                }
            }

            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.FISHING_BOBBER_RETRIEVE,
                    SoundSource.NEUTRAL,
                    1.0F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (level instanceof ServerLevel serverlevel) {
                int j = (int)(EnchantmentHelper.getFishingTimeReduction(serverlevel, stack, player) * 20.0F);
                int k = EnchantmentHelper.getFishingLuckBonus(serverlevel, stack, player);
                Projectile.spawnProjectile(new FishingHook(player, level, k, j), serverlevel, stack);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResult.SUCCESS;
    }

    //region Standard tool overrides

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getDurabilityStat());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!GearHelper.isBroken(stack)) {
            return super.mineBlock(stack, level, state, pos, miningEntity);
        }
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        GearHelper.inventoryTick(stack, level, entity, slot);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return GearHelper.useOn(context);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    //endregion
}
