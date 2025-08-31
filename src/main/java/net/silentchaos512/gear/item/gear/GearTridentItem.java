package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.entity.projectile.GearThrownTrident;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearTridentItem extends TridentItem implements GearWeapon {
    private final Supplier<GearType> gearType;

    public GearTridentItem(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
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
    	if (FMLEnvironment.dist == Dist.CLIENT) {
    		return GearClientHelper.hasEffect(stack);
    	}
        return super.isFoil(stack); //client config will be applied in the renderer
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
    
    // Throwing
    
    public static float getUseTimeRequiredToThrow(ItemStack stack) {
    	float mult = GearData.getProperties(stack).getNumber(GearProperties.DRAW_SPEED);
    	return Mth.floor(10.0F / ( mult == 0 ? 1.0F : mult ));
    }
    
    public static float getProjectileSpeedMultiplier(ItemStack stack) {
    	float mult = GearData.getProperties(stack).getNumber(GearProperties.PROJECTILE_SPEED);
    	return mult == 0 ? 1.0F : mult;
    	
    }
    
    public static float getProjectileAttackDamage(ItemStack stack) {
        var properties = GearData.getProperties(stack);
        float mult = properties.getNumber(GearProperties.RANGED_DAMAGE);
    	mult = 1 + (mult-1)/4;
    	return properties.getNumber(GearProperties.ATTACK_DAMAGE) * mult;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        GearThrownTrident throwntrident = new GearThrownTrident(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
        throwntrident.pickup = AbstractArrow.Pickup.ALLOWED;
        return throwntrident;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            int i = this.getUseDuration(stack, entityLiving) - timeLeft;
            if (i >= getUseTimeRequiredToThrow(stack)) {
                float f = EnchantmentHelper.getTridentSpinAttackStrength(stack, player);
                if (!(f > 0.0F) || player.isInWaterOrRain()) {
                    if (!GearHelper.isBroken(stack)) {
                        Holder<SoundEvent> holder = EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND)
                            .orElse(SoundEvents.TRIDENT_THROW);
                        player.awardStat(Stats.ITEM_USED.get(this));
                        if (level instanceof ServerLevel serverLevels) {
                            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
                            if (f == 0.0F) {
                                GearThrownTrident throwntrident = new GearThrownTrident(level, player, stack);
                                float vel = Mth.clamp(2.5F*getProjectileSpeedMultiplier(stack), 0.0F, 4.0F); //capped speed due to client sync issue
                                throwntrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, vel, 1.0F);
                                if (player.hasInfiniteMaterials()) {
                                    throwntrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                }

                                level.addFreshEntity(throwntrident);
                                level.playSound(null, throwntrident, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                if (!player.hasInfiniteMaterials()) {
                                    player.getInventory().removeItem(stack);
                                }
                            }
                        }

                        if (f > 0.0F) {
                        	f = f * getProjectileSpeedMultiplier(stack);
                            float f7 = player.getYRot();
                            float f1 = player.getXRot();
                            float f2 = -Mth.sin(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f1 * (float) (Math.PI / 180.0));
                            float f3 = -Mth.sin(f1 * (float) (Math.PI / 180.0));
                            float f4 = Mth.cos(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f1 * (float) (Math.PI / 180.0));
                            float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
                            f2 *= f / f5;
                            f3 *= f / f5;
                            f4 *= f / f5;
                            player.push((double)f2, (double)f3, (double)f4);
                            player.startAutoSpinAttack(20, getProjectileAttackDamage(stack), stack);
                            if (player.onGround()) {
                                float f6 = 1.1999999F;
                                player.move(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
                            }

                            level.playSound(null, player, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
        return false;
    }

    //endregion
}
