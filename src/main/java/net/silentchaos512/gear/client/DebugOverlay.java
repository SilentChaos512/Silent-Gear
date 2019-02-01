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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.event.GearEvents;
import net.silentchaos512.gear.init.ModTraits;
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
//            PartRegistry.getDebugLines(list);
//            list.add("GearClientHelper.modelCache=" + GearClientHelper.modelCache.size());
//            list.add("ColorHandlers.gearColorCache=" + ColorHandlers.gearColorCache.size());

        // Harvest level checks
        Minecraft mc = Minecraft.getInstance();
        RayTraceResult rt = mc.objectMouseOver;
        if (rt != null && rt.type == RayTraceResult.Type.BLOCK) {
            Entity renderViewEntity = mc.getRenderViewEntity();
            if (renderViewEntity != null) {
                BlockPos pos = rt.getBlockPos();
                IBlockState state = renderViewEntity.world.getBlockState(pos);

                EntityPlayerSP player = mc.player;
                ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
                if (heldItem.getItem() instanceof ICoreTool) {
                    ToolType toolClass = state.getBlock().getHarvestTool(state);
                    final int blockLevel = state.getBlock().getHarvestLevel(state);
                    final int toolLevel = heldItem.getItem().getHarvestLevel(heldItem, toolClass, player, state);

                    final boolean canHarvest = toolLevel >= blockLevel;
                    TextFormatting format = canHarvest ? TextFormatting.GREEN : TextFormatting.RED;
                    list.add(format + String.format("%s=%d (%d)", toolClass, blockLevel, toolLevel));

                    final float destroySpeed = heldItem.getDestroySpeed(state);
                    if (canHarvest) {
                        int level = TraitHelper.getTraitLevel(heldItem, ModTraits.speedBoostLight);
                        float light = GearEvents.getAreaLightBrightness(player.world, player.getPosition());
                        final float newSpeed = destroySpeed + 3 * level * light;
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
