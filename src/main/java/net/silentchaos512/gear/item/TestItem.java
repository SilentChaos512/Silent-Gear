/*
 * Silent Gear -- TestItem
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

package net.silentchaos512.gear.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.silentchaos512.lib.item.IColoredItem;

public class TestItem extends Item implements IColoredItem {
    @Override
    public IItemColor getColorHandler() {
        return (stack, tintIndex) -> {
            if (tintIndex == 0) return 0xFFFFFF;
            else if (tintIndex == 1) return 3949738;
            else if (tintIndex == 2) return 0xFFFFFF;
            return 0xFFFFFF;
        };
    }
}
