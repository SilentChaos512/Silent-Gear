package net.silentchaos512.gear.entity.projectile;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.init.ModEntities;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;

public class GearArrowEntity extends ArrowEntity {
    private static final Cache<UUID, ItemStack> STACK_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private ItemStack arrowStack = ItemStack.EMPTY;

    public GearArrowEntity(EntityType<? extends ArrowEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public GearArrowEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public GearArrowEntity(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    public GearArrowEntity(FMLPlayMessages.SpawnEntity message, World world) {
        this(ModEntities.ARROW.get(), world);
    }

    private static boolean isSameArrowStack(UUID uuid, ItemStack other) {
        return other.getDamageValue() > 0 && GearHelper.isGear(other) && GearData.getUUID(other).equals(uuid);
    }

    public void setArrowStack(ItemStack stack) {
        try {
            this.arrowStack = STACK_CACHE.get(GearData.getUUID(stack), () -> getArrowClone(stack));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static ItemStack getArrowClone(ItemStack stack) {
        ItemStack ret = stack.copy();
        ret.setDamageValue(ret.getMaxDamage() - 1);
        return ret;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        Entity shooter = getOwner();
        if (shooter instanceof PlayerEntity && !((PlayerEntity) shooter).abilities.instabuild) {
            // Correct pickup status. Gear arrows are "infinite" so vanilla makes this creative only
            this.pickup = PickupStatus.ALLOWED;
        }
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float speedMulti = GearData.getStat(arrowStack, ItemStats.PROJECTILE_SPEED);
        float accuracy = GearData.getStat(arrowStack, ItemStats.PROJECTILE_ACCURACY);
        super.shootFromRotation(shooter, x, y, z, velocity * speedMulti, accuracy > 0f ? inaccuracy / accuracy : inaccuracy);
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public void playerTouch(PlayerEntity entityIn) {
        if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
            boolean flag = this.pickup == AbstractArrowEntity.PickupStatus.ALLOWED || this.pickup == AbstractArrowEntity.PickupStatus.CREATIVE_ONLY && entityIn.abilities.instabuild || this.isNoPhysics() && this.getOwner().getUUID() == entityIn.getUUID();
            if (this.pickup == PickupStatus.ALLOWED && (!GearHelper.isGear(arrowStack) || !addArrowToPlayerInventory(entityIn))) {
                flag = false;
            }

            if (flag) {
                entityIn.take(this, 1);
                this.remove();
            }
        }
    }

    private boolean addArrowToPlayerInventory(PlayerEntity player) {
        UUID uuid = GearData.getUUID(arrowStack);

        for (ItemStack stack : player.inventory.offhand) {
            if (isSameArrowStack(uuid, stack)) {
                stack.setDamageValue(stack.getDamageValue() - 1);
                return true;
            }
        }
        for (ItemStack stack : player.inventory.items) {
            if (isSameArrowStack(uuid, stack)) {
                stack.setDamageValue(stack.getDamageValue() - 1);
                return true;
            }
        }

        return player.addItem(arrowStack);
    }

}
