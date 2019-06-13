/*
 * Silent Gear -- VanillaGearSalvage
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

package net.silentchaos512.gear.block.salvager;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.IItemProvider;
import net.silentchaos512.gear.SilentGear;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;

final class VanillaGearSalvage {
    private static final Collection<Item> ITEMS = ImmutableList.of(
            Items.DIAMOND_SWORD, Items.GOLDEN_SWORD, Items.IRON_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD,
            Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE,
            Items.DIAMOND_SHOVEL, Items.GOLDEN_SHOVEL, Items.IRON_SHOVEL, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL,
            Items.DIAMOND_AXE, Items.GOLDEN_AXE, Items.IRON_AXE, Items.STONE_AXE, Items.WOODEN_AXE,
            Items.DIAMOND_HOE, Items.GOLDEN_HOE, Items.IRON_HOE, Items.STONE_HOE, Items.WOODEN_HOE,
            Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS,
            Items.CHAINMAIL_BOOTS, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_LEGGINGS,
            Items.IRON_BOOTS, Items.IRON_CHESTPLATE, Items.IRON_HELMET, Items.IRON_LEGGINGS,
            Items.GOLDEN_BOOTS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET, Items.GOLDEN_LEGGINGS,
            Items.DIAMOND_BOOTS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET, Items.DIAMOND_LEGGINGS
    );

    private VanillaGearSalvage() {}

    static boolean isVanillaGear(ItemStack stack) {
        return ITEMS.contains(stack.getItem());
    }

    static int getHeadCount(ItemStack stack) {
        Item item = stack.getItem();
        //noinspection ChainOfInstanceofChecks
        if (item instanceof ShovelItem) return 1;
        if (item instanceof SwordItem || item instanceof HoeItem) return 2;
        if (item instanceof PickaxeItem || item instanceof AxeItem) return 3;
        if (item instanceof ArmorItem) {
            int multi = Objects.requireNonNull(item.getRegistryName()).getPath().startsWith("chainmail") ? 4 : 1;
            EquipmentSlotType type = ((ArmorItem) item).getEquipmentSlot();
            if (type == EquipmentSlotType.CHEST) return 8 * multi;
            if (type == EquipmentSlotType.FEET) return 4 * multi;
            if (type == EquipmentSlotType.HEAD) return 5 * multi;
            if (type == EquipmentSlotType.LEGS) return 7 * multi;
        }

        SilentGear.LOGGER.warn("Tried to salvage '{}' as vanilla gear, but could not identify item type", stack);
        return 0;
    }

    static int getRodCount(ItemStack stack) {
        //noinspection ChainOfInstanceofChecks
        if (stack.getItem() instanceof ArmorItem) return 0;
        if (stack.getItem() instanceof ArmorItem) return 1;
        return 2;
    }

    @Nullable
    static IItemProvider getHeadItem(ItemStack stack) {
        String name = Objects.requireNonNull(stack.getItem().getRegistryName()).getPath();
        if (name.startsWith("diamond")) return Items.DIAMOND;
        if (name.startsWith("golden")) return Items.GOLD_INGOT;
        if (name.startsWith("iron")) return Items.IRON_INGOT;
        if (name.startsWith("stone")) return Blocks.COBBLESTONE;
        if (name.startsWith("wooden")) return Blocks.OAK_PLANKS;
        if (name.startsWith("leather")) return Items.LEATHER;
        if (name.startsWith("chainmail")) return Items.IRON_NUGGET;

        SilentGear.LOGGER.warn("Don't know salvage head part for vanilla gear '{}'", stack);
        return null;
    }
}
