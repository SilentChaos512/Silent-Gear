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
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
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
import net.silentchaos512.gear.client.util.ModelPropertiesHelper;
import net.silentchaos512.gear.event.GearEvents;
import net.silentchaos512.gear.item.gear.CoreCrossbow;
import net.silentchaos512.gear.util.Const;
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

//        addAttributeInfo(list, player, SharedMonsterAttributes.LUCK);

        ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
        if (heldItem.isEmpty()) return list;

        Item item = heldItem.getItem();

        // Crossbow debugging
        if (item instanceof CoreCrossbow) {
            float pull = ModelPropertiesHelper.getValue(heldItem, new ResourceLocation("pull"), mc.world, player);
            float pulling = ModelPropertiesHelper.getValue(heldItem, new ResourceLocation("pulling"), mc.world, player);
            float charged = ModelPropertiesHelper.getValue(heldItem, new ResourceLocation("charged"), mc.world, player);
            float firework = ModelPropertiesHelper.getValue(heldItem, new ResourceLocation("firework"), mc.world, player);
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
                        int level = TraitHelper.getTraitLevel(heldItem, Const.Traits.LUSTROUS);
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

    private static void addAttributeInfo(List<String> list, PlayerEntity player, Attribute attribute) {
        ModifiableAttributeInstance attribute1 = player.getAttribute(attribute);
        list.add(String.format("%s=%.1f (%dx mods)", attribute, attribute1.getValue(), attribute1.getModifierListCopy().size()));
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
