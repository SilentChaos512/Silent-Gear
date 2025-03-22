package net.silentchaos512.gear.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.silentchaos512.gear.item.gear.GearTridentItem;

public class SgClientItemExtensions implements IClientItemExtensions {
    private final SgBlockEntityWithoutLevelRenderer renderer = new SgBlockEntityWithoutLevelRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }
    
    private void applyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equippedProg) {
        int i = hand == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((float)i * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
    }
    
    //the vanilla Trident use animation has a hardcoded use time of 10 ticks, so this copy is made to circumvent that
    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm humanoidarm, ItemStack stack, float partialTicks, float equippedProgress, float swingProcess) {
    	boolean isRightHand = humanoidarm == HumanoidArm.RIGHT;
    	boolean isMainItemItem = player.getUsedItemHand() == InteractionHand.MAIN_HAND;
    	boolean isUsingItem = (isRightHand && isMainItemItem) || (!isRightHand && !isMainItemItem);
    	if (!(player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && isUsingItem)) {
    		return false;
    	}
    	
    	int k = isRightHand ? 1 : -1;
    	this.applyItemArmTransform(poseStack, humanoidarm, equippedProgress);
        poseStack.translate((float)k * -0.5F, 0.7F, 0.1F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-55.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees((float)k * 35.3F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float)k * -9.785F));
        float f7 = (float)stack.getUseDuration(player) - ((float)player.getUseItemRemainingTicks() - partialTicks + 1.0F);
        float f11 = f7 / GearTridentItem.getUseTimeRequiredToThrow(stack);
        if (f11 > 1.0F) {
            f11 = 1.0F;
        }

        if (f11 > 0.1F) {
            float f14 = Mth.sin((f7 - 0.1F) * 1.3F);
            float f17 = f11 - 0.1F;
            float f19 = f14 * f17;
            poseStack.translate(f19 * 0.0F, f19 * 0.004F, f19 * 0.0F);
        }

        poseStack.translate(0.0F, 0.0F, f11 * 0.2F);
        poseStack.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
        poseStack.mulPose(Axis.YN.rotationDegrees((float)k * 45.0F));
    	return true;
    }
}