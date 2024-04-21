package net.silentchaos512.gear.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GearHudOverlay {
    protected static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");

    private final Minecraft mc;
    private int scaledWidth;
    private int scaledHeight;

    public GearHudOverlay() {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void renderOverlay(RenderGuiOverlayEvent.Post event) {
        this.scaledWidth = this.mc.getWindow().getGuiScaledWidth();
        this.scaledHeight = this.mc.getWindow().getGuiScaledHeight();

        if (!this.mc.options.hideGui) {
            renderAttackIndicator(event.getGuiGraphics());
        }
    }

    private void renderAttackIndicator(GuiGraphics graphics) {
        // Renders an attack indicator if an entity is within extra reach distance of a gear weapon
        RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);

        Options options = this.mc.options;
        if (options.getCameraType().isFirstPerson()) {
            MultiPlayerGameMode playerGameMode = this.mc.gameMode;
            if (playerGameMode != null && playerGameMode.getPlayerMode() != GameType.SPECTATOR && !isEntityTargeted(mc.hitResult)) {
                LocalPlayer player = this.mc.player;
                if (player == null) return;

                if (options.hideGui || player.isReducedDebugInfo() || options.reducedDebugInfo().get()) {
                    if (this.mc.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
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
                            RenderSystem.clearColor(0.5f, 1f, 0.5f, 1f);
                            graphics.blit(GUI_ICONS_LOCATION, k, j, 68, 94, 16, 16);
                        }
                    }
                }

            }
        }
    }

    private static boolean isEntityTargeted(@Nullable HitResult rayTraceIn) {
        return rayTraceIn != null && rayTraceIn.getType() == HitResult.Type.ENTITY;
    }
}
