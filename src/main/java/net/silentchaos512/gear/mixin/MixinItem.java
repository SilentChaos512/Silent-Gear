package net.silentchaos512.gear.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.util.Const;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {
    @Inject(at = @At("HEAD"), method = "isFoil(Lnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    private void isFoil(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
        if (stack.hasTag() && stack.getOrCreateTag().getBoolean(Const.NBT_IS_FOIL)) {
            callback.setReturnValue(true);
        }
    }
}
