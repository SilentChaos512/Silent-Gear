package net.silentchaos512.gear.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.setup.SgEntities;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;

public class GearArrowEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> ID_COLOR_ROD = SynchedEntityData.defineId(GearArrowEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ID_COLOR_TIP = SynchedEntityData.defineId(GearArrowEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ID_COLOR_FLETCHING = SynchedEntityData.defineId(GearArrowEntity.class, EntityDataSerializers.INT);

    private ItemStack arrowStack = ItemStack.EMPTY;

    public GearArrowEntity(EntityType<? extends GearArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public GearArrowEntity(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(SgEntities.ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
        setArrowStack(pickupItemStack);
        setColors(pickupItemStack);
    }

    public GearArrowEntity(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(SgEntities.ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
        setArrowStack(pickupItemStack);
        setColors(pickupItemStack);
    }

    @Override
    protected void setPickupItemStack(ItemStack pickupItemStack) {
        super.setPickupItemStack(pickupItemStack);
        setArrowStack(pickupItemStack);
        setColors(pickupItemStack);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_COLOR_ROD, -1);
        builder.define(ID_COLOR_TIP, -1);
        builder.define(ID_COLOR_FLETCHING, -1);
    }

    private void setColors(ItemStack stack) {
        int rodColor = ColorUtils.getBlendedColorForPartInGear(stack, PartTypes.ROD.get());
        int tipColor = ColorUtils.getBlendedColorForPartInGear(stack, PartTypes.MAIN.get());
        int fletchingColor = ColorUtils.getBlendedColorForPartInGear(stack, PartTypes.FLETCHING.get());
        this.entityData.set(ID_COLOR_ROD, rodColor);
        this.entityData.set(ID_COLOR_TIP, tipColor);
        this.entityData.set(ID_COLOR_FLETCHING, fletchingColor);
    }

    public int getRodColor() {
        return this.entityData.get(ID_COLOR_ROD);
    }

    public int getTipColor() {
        return this.entityData.get(ID_COLOR_TIP);
    }

    public int getFletchingColor() {
        return this.entityData.get(ID_COLOR_FLETCHING);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        @Nullable var item = GearType.getItem(GearTypes.ARROW.get());
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
    }

    public void setArrowStack(ItemStack stack) {
        this.arrowStack = stack.copyWithCount(1);
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float speedMulti = GearData.getProperties(arrowStack).getNumber(GearProperties.PROJECTILE_SPEED);
        float accuracy = GearData.getProperties(arrowStack).getNumber(GearProperties.PROJECTILE_ACCURACY);
        super.shootFromRotation(shooter, x, y, z, velocity * speedMulti, accuracy > 0f ? inaccuracy / accuracy : inaccuracy);
    }

}
