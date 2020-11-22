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
        return other.getDamage() > 0 && GearHelper.isGear(other) && GearData.getUUID(other).equals(uuid);
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
        ret.setDamage(ret.getMaxDamage() - 1);
        return ret;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        Entity shooter = func_234616_v_();
        if (shooter instanceof PlayerEntity && !((PlayerEntity) shooter).abilities.isCreativeMode) {
            // Correct pickup status. Gear arrows are "infinite" so vanilla makes this creative only
            this.pickupStatus = PickupStatus.ALLOWED;
        }
    }

    @Override
    public void func_234612_a_(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float speedMulti = GearData.getStat(arrowStack, ItemStats.PROJECTILE_SPEED);
        float accuracy = GearData.getStat(arrowStack, ItemStats.PROJECTILE_ACCURACY);
        super.func_234612_a_(shooter, x, y, z, velocity * speedMulti, accuracy > 0f ? inaccuracy / accuracy : inaccuracy);
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        if (!this.world.isRemote && (this.inGround || this.getNoClip()) && this.arrowShake <= 0) {
            boolean flag = this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED || this.pickupStatus == AbstractArrowEntity.PickupStatus.CREATIVE_ONLY && entityIn.abilities.isCreativeMode || this.getNoClip() && this.func_234616_v_().getUniqueID() == entityIn.getUniqueID();
            if (this.pickupStatus == PickupStatus.ALLOWED && (!GearHelper.isGear(arrowStack) || !addArrowToPlayerInventory(entityIn))) {
                flag = false;
            }

            if (flag) {
                entityIn.onItemPickup(this, 1);
                this.remove();
            }
        }
    }

    private boolean addArrowToPlayerInventory(PlayerEntity player) {
        UUID uuid = GearData.getUUID(arrowStack);

        for (ItemStack stack : player.inventory.offHandInventory) {
            if (isSameArrowStack(uuid, stack)) {
                stack.setDamage(stack.getDamage() - 1);
                return true;
            }
        }
        for (ItemStack stack : player.inventory.mainInventory) {
            if (isSameArrowStack(uuid, stack)) {
                stack.setDamage(stack.getDamage() - 1);
                return true;
            }
        }

        return player.addItemStackToInventory(arrowStack);
    }

}
