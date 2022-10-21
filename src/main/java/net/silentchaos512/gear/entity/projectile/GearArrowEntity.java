package net.silentchaos512.gear.entity.projectile;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.init.SgEntities;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GearArrowEntity extends Arrow {
    private static final Cache<UUID, ItemStack> STACK_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private ItemStack arrowStack = ItemStack.EMPTY;

    public GearArrowEntity(EntityType<? extends Arrow> type, Level worldIn) {
        super(type, worldIn);
    }

    public GearArrowEntity(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public GearArrowEntity(Level worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    public GearArrowEntity(PlayMessages.SpawnEntity message, Level world) {
        this(SgEntities.ARROW.get(), world);
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
        if (shooter instanceof Player && !((Player) shooter).getAbilities().instabuild) {
            // Correct pickup status. Gear arrows are "infinite" so vanilla makes this creative only
            this.pickup = Pickup.ALLOWED;
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
    public void playerTouch(Player entityIn) {
        if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
            boolean flag = this.pickup == AbstractArrow.Pickup.ALLOWED || this.pickup == AbstractArrow.Pickup.CREATIVE_ONLY && entityIn.getAbilities().instabuild || this.isNoPhysics() && this.getOwner().getUUID() == entityIn.getUUID();
            if (this.pickup == Pickup.ALLOWED && (!GearHelper.isGear(arrowStack) || !addArrowToPlayerInventory(entityIn))) {
                flag = false;
            }

            if (flag) {
                entityIn.take(this, 1);
                this.discard();
            }
        }
    }

    private boolean addArrowToPlayerInventory(Player player) {
        UUID uuid = GearData.getUUID(arrowStack);

        for (ItemStack stack : player.getInventory().offhand) {
            if (isSameArrowStack(uuid, stack)) {
                stack.setDamageValue(stack.getDamageValue() - 1);
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().items) {
            if (isSameArrowStack(uuid, stack)) {
                stack.setDamageValue(stack.getDamageValue() - 1);
                return true;
            }
        }

        return player.addItem(arrowStack);
    }

}
