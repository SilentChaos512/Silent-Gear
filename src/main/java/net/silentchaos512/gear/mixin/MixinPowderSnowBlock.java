package net.silentchaos512.gear.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TraitHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class MixinPowderSnowBlock {
    @Inject(at = @At("HEAD"), method = "canEntityWalkOnPowderSnow(Lnet/minecraft/world/entity/Entity;)Z", cancellable = true)
    private static void canEntityWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        // Players can walk on powder snow with the Snow Walker trait
        if (entity instanceof Player) {
            int snowWalker = TraitHelper.getHighestLevelArmorOrCurio((Player) entity, Const.Traits.SNOW_WALKER);
            if (snowWalker > 0) {
                callback.setReturnValue(true);
            }
        }
    }
}
