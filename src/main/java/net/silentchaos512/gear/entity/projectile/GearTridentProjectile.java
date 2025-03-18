package net.silentchaos512.gear.entity.projectile;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.setup.SgEntities;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

public class GearTridentProjectile extends AbstractArrow {
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(GearTridentProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(GearTridentProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ID_COLOR_TOOLROD = SynchedEntityData.defineId(GearTridentProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ID_COLOR_GRIP = SynchedEntityData.defineId(GearTridentProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ID_COLOR_SPIKES = SynchedEntityData.defineId(GearTridentProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ID_COLOR_TIP = SynchedEntityData.defineId(GearTridentProjectile.class, EntityDataSerializers.INT);
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;

    public GearTridentProjectile(EntityType<? extends GearTridentProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public GearTridentProjectile(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
        super(SgEntities.TRIDENT_PROJECTILE.get(), shooter, level, pickupItemStack, null);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(pickupItemStack));
        this.entityData.set(ID_FOIL, pickupItemStack.hasFoil());
        
        setColors(pickupItemStack);
    }

    public GearTridentProjectile(Level level, double x, double y, double z, ItemStack pickupItemStack) {
        super(SgEntities.TRIDENT_PROJECTILE.get(), x, y, z, level, pickupItemStack, pickupItemStack);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(pickupItemStack));
        this.entityData.set(ID_FOIL, pickupItemStack.hasFoil());
        
        setColors(pickupItemStack);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LOYALTY, (byte)0);
        builder.define(ID_FOIL, false);
        builder.define(ID_COLOR_TOOLROD, -1);
        builder.define(ID_COLOR_GRIP, -1);
        builder.define(ID_COLOR_SPIKES, -1);
        builder.define(ID_COLOR_TIP, -1);
    }
    
    public int getToolRodColor() {
    	return this.entityData.get(ID_COLOR_TOOLROD);
    }
    public int getGripColor() {
    	return this.entityData.get(ID_COLOR_GRIP);
    }
    public int getSpikesColor() {
    	return this.entityData.get(ID_COLOR_SPIKES);
    }
    public int getTipColor() {
    	return this.entityData.get(ID_COLOR_TIP);
    }
    
    private void setColors(ItemStack pickupItemStack) {
        int toolRodColor = ColorUtils.getBlendedColorForPartInGear(pickupItemStack, PartTypes.ROD.get());
        int gripColor = ColorUtils.getBlendedColorForPartInGear(pickupItemStack, PartTypes.GRIP.get());
        int spikesColor = ColorUtils.getBlendedColorForPartInGear(pickupItemStack, PartTypes.MAIN.get());
        int tipColor = ColorUtils.getBlendedColorForPartInGear(pickupItemStack, PartTypes.TIP.get());
        int coatingColor = ColorUtils.getBlendedColorForPartInGear(pickupItemStack, PartTypes.COATING.get());
        this.entityData.set(ID_COLOR_TOOLROD, toolRodColor);
        this.entityData.set(ID_COLOR_GRIP, gripColor == -1 ? toolRodColor : gripColor);
        this.entityData.set(ID_COLOR_SPIKES, coatingColor == -1 ? spikesColor : coatingColor);
        this.entityData.set(ID_COLOR_TIP, tipColor == -1 ? spikesColor : tipColor);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();
        int i = this.entityData.get(ID_LOYALTY);
        if (i > 0 && (this.dealtDamage || this.isNoPhysics()) && entity != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3 vec3 = entity.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * (double)i, this.getZ());
                if (this.level().isClientSide) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05 * (double)i;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                this.clientSideReturnTridentTickCount++;
            }
        }

        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity entity = this.getOwner();
        return entity == null || !entity.isAlive() ? false : !(entity instanceof ServerPlayer) || !entity.isSpectator();
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    /**
     * Gets the EntityHitResult representing the entity hit
     */
    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    /**
     * Called when the arrow hits an entity
     */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 8.0F;
        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, (Entity)(entity1 == null ? this : entity1));
        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, f);
        }

        this.dealtDamage = true;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (this.level() instanceof ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, entity, damagesource, this.getWeaponItem());
            }

            if (entity instanceof LivingEntity livingentity) {
                this.doKnockback(livingentity, damagesource);
                this.doPostHurtEffects(livingentity);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void hitBlockEnchantmentEffects(ServerLevel level, BlockHitResult hitResult, ItemStack stack) {
        Vec3 vec3 = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
        EnchantmentHelper.onHitBlock(
            level,
            stack,
            this.getOwner() instanceof LivingEntity livingentity ? livingentity : null,
            this,
            null,
            vec3,
            level.getBlockState(hitResult.getBlockPos()),
            p_348680_ -> this.kill()
        );
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(GearType.getItem(GearTypes.TRIDENT.get()));
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    @Override
    public void playerTouch(Player entity) {
        if (this.ownedBy(entity) || this.getOwner() == null) {
            super.playerTouch(entity);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.dealtDamage = compound.getBoolean("DealtDamage");
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.getPickupItemStackOrigin()));
        
        setColors(this.getPickupItemStackOrigin());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    private byte getLoyaltyFromItem(ItemStack stack) {
        return this.level() instanceof ServerLevel serverlevel
            ? (byte)Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverlevel, stack, this), 0, 127)
            : 0;
    }

    @Override
    public void tickDespawn() {
        int i = this.entityData.get(ID_LOYALTY);
        if (this.pickup != AbstractArrow.Pickup.ALLOWED || i <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
