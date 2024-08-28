package net.silentchaos512.gear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.util.ModelPropertiesHelper;
import net.silentchaos512.gear.item.gear.GearCrossbowItem;
import net.silentchaos512.lib.client.gui.DebugRenderOverlay;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DebugOverlay extends DebugRenderOverlay {
    private static final int SPLIT_WIDTH = 160;
    private static final float TEXT_SCALE = 0.7f;

    @Nonnull
    @Override
    public List<String> getDebugText() {
        List<String> list = new ArrayList<>();

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return list;

        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.isEmpty()) return list;

        Item item = heldItem.getItem();

        // Crossbow debugging
        if (item instanceof GearCrossbowItem) {
            float pull = ModelPropertiesHelper.getValue(heldItem, ResourceLocation.withDefaultNamespace("pull"), mc.level, player);
            float pulling = ModelPropertiesHelper.getValue(heldItem, ResourceLocation.withDefaultNamespace("pulling"), mc.level, player);
            float charged = ModelPropertiesHelper.getValue(heldItem, ResourceLocation.withDefaultNamespace("charged"), mc.level, player);
            float firework = ModelPropertiesHelper.getValue(heldItem, ResourceLocation.withDefaultNamespace("firework"), mc.level, player);
            list.add(String.format("pull=%.1f", pull));
            list.add(String.format("pulling=%.1f", pulling));
            list.add(String.format("charged=%.1f", charged));
            list.add(String.format("firework=%.1f", firework));
            list.add(String.format("chargeTime=%d", GearCrossbowItem.getChargeTime(heldItem)));
            return list;
        }

        // Harvest level checks
        HitResult rt = mc.hitResult;
        if (rt != null && rt.getType() == HitResult.Type.BLOCK) {
            BlockHitResult brt = (BlockHitResult) rt;
            Entity renderViewEntity = mc.getCameraEntity();
            if (renderViewEntity != null) {
                BlockPos pos = brt.getBlockPos();
                BlockState state = renderViewEntity.level().getBlockState(pos);

                /*if (item instanceof ICoreTool) {
                    ToolType toolClass = state.getBlock().getHarvestTool(state);
                    final int blockLevel = state.getBlock().getHarvestLevel(state);
                    final int toolLevel = item.getHarvestLevel(heldItem, toolClass, player, state);

                    final boolean canHarvest = toolLevel >= blockLevel;
                    ChatFormatting format = canHarvest ? ChatFormatting.GREEN : ChatFormatting.RED;
                    String name = toolClass == null ? "null" : toolClass.getName();
                    list.add(format + String.format("%s=%d (%d)", name, blockLevel, toolLevel));

                    final float destroySpeed = heldItem.getDestroySpeed(state);
                    if (canHarvest) {
                        int level = TraitHelper.getTraitLevel(heldItem, Const.Traits.LUSTROUS);
                        int light = GearEvents.getLightForLustrousTrait(player.level, player.blockPosition());
                        final float newSpeed = destroySpeed + GearEvents.getLustrousSpeedBonus(level, light);
                        list.add(String.format("speed = %.1f", newSpeed));
                    } else {
                        list.add(String.format("speed = %.1f", destroySpeed));
                    }
                }*/
            }
        } else if (rt != null && rt.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ert = (EntityHitResult) rt;
            Entity entity = ert.getEntity();
            if (entity instanceof LivingEntity) {
                list.add(String.format("%s", entity.getScoreboardName()));
                list.add(String.format("health = %.3f", ((LivingEntity) entity).getHealth()));
            }
        }

        return list;
    }

    @Override
    public float getTextScale() {
        return TEXT_SCALE;
    }

    @Override
    public int getSplitWidth() {
        return SPLIT_WIDTH;
    }

    @Override
    public boolean isHidden() {
        return !SilentGear.isDevBuild();
    }
}
