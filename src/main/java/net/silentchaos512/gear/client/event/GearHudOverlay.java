package net.silentchaos512.gear.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GearHudOverlay extends AbstractGui {
    private final Minecraft mc;
    private int scaledWidth;
    private int scaledHeight;

    public GearHudOverlay() {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        this.scaledWidth = this.mc.getMainWindow().getScaledWidth();
        this.scaledHeight = this.mc.getMainWindow().getScaledHeight();

        if (!this.mc.gameSettings.hideGUI) {
            renderAttackIndicator(event.getMatrixStack());
        }
    }

    private void renderAttackIndicator(MatrixStack p_238456_1_) {
        // Renders an attack indicator if an entity is within extra reach distance of a gear weapon

//        RenderSystem.defaultAlphaFunc();
        this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
//        RenderSystem.enableBlend();
//        RenderSystem.enableAlphaTest();

        GameSettings gamesettings = this.mc.gameSettings;
        if (gamesettings.getPointOfView().func_243192_a()) {
            PlayerController playerController = this.mc.playerController;
            if (playerController != null && playerController.getCurrentGameType() != GameType.SPECTATOR && !isEntityTargeted(mc.objectMouseOver)) {
                ClientPlayerEntity player = this.mc.player;
                if (player == null) return;

                if (!gamesettings.showDebugInfo || gamesettings.hideGUI || player.hasReducedDebug() || gamesettings.reducedDebugInfo) {
//                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    if (this.mc.gameSettings.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                        float f = player.getCooledAttackStrength(0.0F);
                        boolean flag = false;
                        Entity entity = GearHelper.getAttackTargetWithExtraReach(player);
                        if (entity instanceof LivingEntity && f >= 1.0F) {
                            flag = player.getCooldownPeriod() > 5.0F;
                            flag = flag & entity.isAlive();
                        }

                        int j = this.scaledHeight / 2 - 7 + 16;
                        int k = this.scaledWidth / 2 - 8;
                        if (flag) {
                            RenderSystem.color4f(0.5f, 1f, 0.5f, 1f);
                            this.blit(p_238456_1_, k, j, 68, 94, 16, 16);
                        }
                    }
                }

            }
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    private static boolean isEntityTargeted(@Nullable RayTraceResult rayTraceIn) {
        return rayTraceIn != null && rayTraceIn.getType() == RayTraceResult.Type.ENTITY;
    }
}
