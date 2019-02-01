/*
 * Silent Gear -- EvilCraftCompat
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

package net.silentchaos512.gear.compat.evilcraft;

public class EvilCraftCompat {
    public static void init() {
//        SilentGear.log.info("Loading EvilCraft compatibility");
//
//        IBloodChestRepairActionRegistry reg = EvilCraft._instance.getRegistryManager().getRegistry(IBloodChestRepairActionRegistry.class);
//        reg.register(new DamageableItemRepairAction() {
//            @Override
//            public boolean isItemValidForSlot(ItemStack itemStack) {
//                return itemStack.getItem() instanceof ICoreItem;
//            }
//
//            @Override
//            public boolean canRepair(ItemStack itemStack, int tick) {
//                return itemStack.isItemDamaged() && itemStack.getItem() instanceof ICoreItem;
//            }
//
//            @Override
//            public float repair(ItemStack itemStack, Random random, boolean doAction, boolean isBulk) {
//                boolean wasBroken = GearHelper.isBroken(itemStack);
//                float result = super.repair(itemStack, random, doAction, isBulk);
//                boolean isBroken = GearHelper.isBroken(itemStack);
//
//                // If the item was broken before but isn't now, we need to restore its state
//                if (wasBroken != isBroken) GearData.recalculateStats(itemStack);
//                return result;
//            }
//        });
    }
}
