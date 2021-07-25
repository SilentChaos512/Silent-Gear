package net.silentchaos512.gear.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GearHudOverlay extends GuiComponent {
    private final Minecraft mc;
    private int scaledWidth;
    private int scaledHeight;

    public GearHudOverlay() {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        this.scaledWidth = this.mc.getWindow().getGuiScaledWidth();
        this.scaledHeight = this.mc.getWindow().getGuiScaledHeight();

        if (!this.mc.options.hideGui) {
            renderAttackIndicator(event.getMatrixStack());
        }
    }

    private void renderAttackIndicator(PoseStack p_238456_1_) {
        // Renders an attack indicator if an entity is within extra reach distance of a gear weapon

//        RenderSystem.defaultAlphaFunc();
        this.mc.getTextureManager().bind(GUI_ICONS_LOCATION);
//        RenderSystem.enableBlend();
//        RenderSystem.enableAlphaTest();

        Options gamesettings = this.mc.options;
        if (gamesettings.getCameraType().isFirstPerson()) {
            MultiPlayerGameMode playerController = this.mc.gameMode;
            if (playerController != null && playerController.getPlayerMode() != GameType.SPECTATOR && !isEntityTargeted(mc.hitResult)) {
                LocalPlayer player = this.mc.player;
                if (player == null) return;

                if (!gamesettings.renderDebug || gamesettings.hideGui || player.isReducedDebugInfo() || gamesettings.reducedDebugInfo) {
//                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    if (this.mc.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                        float f = player.getAttackStrengthScale(0.0F);
                        boolean flag = false;
                        Entity entity = GearHelper.getAttackTargetWithExtraReach(player);
                        if (entity instanceof LivingEntity && f >= 1.0F) {
                            flag = player.getCurrentItemAttackStrengthDelay() > 5.0F;
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

//        RenderSystem.defaultBlendFunc();
//        RenderSystem.color4f(1f, 1f, 1f, 1f);
//        RenderSystem.disableBlend();
    }

    private static boolean isEntityTargeted(@Nullable HitResult rayTraceIn) {
        return rayTraceIn != null && rayTraceIn.getType() == HitResult.Type.ENTITY;
    }
}
