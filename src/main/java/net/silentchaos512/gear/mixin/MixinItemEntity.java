package net.silentchaos512.gear.mixin;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class MixinItemEntity {
    @Inject(at = @At("HEAD"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
    private void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if (source.is(DamageTypeTags.IS_FIRE) && isFireproof(getItem())) {
            callback.setReturnValue(false);
        }
    }

    @Unique
    private static boolean isFireproof(ItemStack stack) {
        return GearHelper.isGear(stack) && TraitHelper.hasTraitEffect(stack, TraitEffectTypes.FIREPROOF.get());
    }

    @Shadow
    public ItemStack getItem() {
        throw new IllegalStateException("Mixin failed to shadow getItem()");
    }
}
