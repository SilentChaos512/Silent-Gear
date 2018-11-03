/*
 * Silent Gear -- GearEvents
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

package net.silentchaos512.gear.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModTraits;
import net.silentchaos512.gear.util.TraitHelper;

@Mod.EventBusSubscriber
public final class GearEvents {
    private GearEvents() {}

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        final EntityPlayer player = event.getEntityPlayer();
        ItemStack tool = player.getHeldItemMainhand();

        if (tool.getItem() instanceof ICoreItem) {
            final IBlockState state = event.getState();
            String toolClass = state.getBlock().getHarvestTool(state);
            if (toolClass == null) toolClass = "";
            final int blockLevel = state.getBlock().getHarvestLevel(state);
            final int toolLevel = tool.getItem().getHarvestLevel(tool, toolClass, player, state);
            final boolean canHarvest = toolLevel >= blockLevel;

            if (canHarvest) {
                int level = TraitHelper.getTraitLevel(tool, ModTraits.speedBoostLight);
                float light = getAreaLightBrightness(player.world, player.getPosition());
                event.setNewSpeed(event.getOriginalSpeed() + 3 * level * light);
            }
        }
    }

    public static float getAreaLightBrightness(World world, BlockPos pos) {
        float value = world.getLightBrightness(pos);
        // Checking this many positions IS necessary. If player is near a wall, it would
        // only check inside blocks otherwise.
        value = Math.max(value, world.getLightBrightness(pos.north()));
        value = Math.max(value, world.getLightBrightness(pos.south()));
        value = Math.max(value, world.getLightBrightness(pos.east()));
        value = Math.max(value, world.getLightBrightness(pos.west()));
        // Fix corners too... This can exploited in some rare cases, but should be fine.
        value = Math.max(value, world.getLightBrightness(pos.north().west()));
        value = Math.max(value, world.getLightBrightness(pos.north().east()));
        value = Math.max(value, world.getLightBrightness(pos.south().west()));
        value = Math.max(value, world.getLightBrightness(pos.south().east()));
        return value;
    }
}
