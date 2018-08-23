/*
 * Silent Gear -- MiscUpgrades
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

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartUpgrade;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.lib.item.IEnumItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public enum MiscUpgrades implements IEnumItems<MiscUpgrades, MiscUpgrades.Item> {
    SPOON(MiscUpgrades::matchPickaxe),
    RED_CARD(MiscUpgrades::matchAny);

    private final MiscUpgrades.Item item;
    private final PartUpgrade part;

    MiscUpgrades(final Predicate<ICoreItem> canApplyTo) {
        this.item = new MiscUpgrades.Item();
        ResourceLocation partName = new ResourceLocation(SilentGear.MOD_ID, "misc_" + name().toLowerCase(Locale.ROOT));

        this.part = new PartUpgrade(partName) {
            @Override
            public boolean isValidFor(@Nonnull ICoreItem gearItem) {
                return canApplyTo.test(gearItem);
            }
        };
    }

    private static boolean matchPickaxe(ICoreItem item) {
        return item == ModItems.pickaxe;
    }

    private static boolean matchAny(ICoreItem item) {
        return item != null;
    }

    @Nonnull
    @Override
    public MiscUpgrades getEnum() {
        return this;
    }

    @Nonnull
    @Override
    public MiscUpgrades.Item getItem() {
        return this.item;
    }

    @Nonnull
    public PartUpgrade getPart() {
        return this.part;
    }

    @Nonnull
    @Override
    public String getName() {
        return getEnum().name().toLowerCase(Locale.ROOT) + "_upgrade";
    }
    public static class Item extends net.minecraft.item.Item {
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
            tooltip.add(TextFormatting.ITALIC + SilentGear.i18n.subText(this, "desc"));
        }

    }
}
