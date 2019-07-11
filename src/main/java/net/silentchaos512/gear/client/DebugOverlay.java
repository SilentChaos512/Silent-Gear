/*
 * Silent Gear -- DebugOverlay
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.event.GearEvents;
import net.silentchaos512.gear.item.gear.CoreCrossbow;
import net.silentchaos512.gear.traits.TraitConst;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.gear.util.TraitHelper;
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
        ClientPlayerEntity player = mc.player;
        if (player == null) return list;

        ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
        if (heldItem.isEmpty()) return list;

        Item item = heldItem.getItem();

        // Crossbow debugging
        if (item instanceof CoreCrossbow) {
            float pull = item.getPropertyGetter(new ResourceLocation("pull")).call(heldItem, mc.world, player);
            float pulling = item.getPropertyGetter(new ResourceLocation("pulling")).call(heldItem, mc.world, player);
            float charged = item.getPropertyGetter(new ResourceLocation("charged")).call(heldItem, mc.world, player);
            float firework = item.getPropertyGetter(new ResourceLocation("firework")).call(heldItem, mc.world, player);
            list.add(String.format("pull=%.1f", pull));
            list.add(String.format("pulling=%.1f", pulling));
            list.add(String.format("charged=%.1f", charged));
            list.add(String.format("firework=%.1f", firework));
            list.add(String.format("chargeTime=%d", CoreCrossbow.getChargeTime(heldItem)));
            return list;
        }

        // Harvest level checks
        RayTraceResult rt = mc.objectMouseOver;
        if (rt != null && rt.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult brt = (BlockRayTraceResult) rt;
            Entity renderViewEntity = mc.getRenderViewEntity();
            if (renderViewEntity != null) {
                BlockPos pos = brt.getPos();
                BlockState state = renderViewEntity.world.getBlockState(pos);

                if (item instanceof ICoreTool) {
                    ToolType toolClass = state.getBlock().getHarvestTool(state);
                    final int blockLevel = state.getBlock().getHarvestLevel(state);
                    final int toolLevel = item.getHarvestLevel(heldItem, toolClass, player, state);

                    final boolean canHarvest = toolLevel >= blockLevel;
                    TextFormatting format = canHarvest ? TextFormatting.GREEN : TextFormatting.RED;
                    String name = toolClass == null ? "null" : toolClass.getName();
                    list.add(format + String.format("%s=%d (%d)", name, blockLevel, toolLevel));

                    final float destroySpeed = heldItem.getDestroySpeed(state);
                    if (canHarvest) {
                        ITrait lustrous = TraitManager.get(TraitConst.LUSTROUS);
                        int level = TraitHelper.getTraitLevel(heldItem, lustrous);
                        int light = GearEvents.getLightForLustrousTrait(player.world, player.getPosition());
                        final float newSpeed = destroySpeed + GearEvents.getLustrousSpeedBonus(level, light);
                        list.add(String.format("speed = %.1f", newSpeed));
                    } else {
                        list.add(String.format("speed = %.1f", destroySpeed));
                    }
                }
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
