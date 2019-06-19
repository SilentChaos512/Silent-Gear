/*
 * Silent Gear -- GearGenerator
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

package net.silentchaos512.gear.util;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.parts.*;
import net.silentchaos512.gear.parts.type.BowstringPart;
import net.silentchaos512.gear.parts.type.MainPart;
import net.silentchaos512.gear.parts.type.RodPart;
import net.silentchaos512.gear.parts.type.TipPart;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class GearGenerator {
    private GearGenerator() {
        throw new IllegalAccessError("Utility class");
    }

    public static Optional<IGearPart> selectRandom(PartType type) {
        return selectRandom(type, -1);
    }

    public static Optional<IGearPart> selectRandom(PartType type, final int partTier) {
        List<IGearPart> list = PartManager.getValues().stream()
                .filter(p -> p.getType() == type)
                .filter(p -> partTier == -1 || p.getTier() == partTier)
                .collect(Collectors.toList());
        if (!list.isEmpty())
            return Optional.of(list.get(SilentGear.random.nextInt(list.size())));
        return Optional.ofNullable(PartManager.tryGetFallback(type));
    }

    public static Collection<IGearPart> selectRandomMains(int count, int partTier) {
        Collection<IGearPart> list = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            selectRandom(PartType.MAIN, partTier).ifPresent(list::add);
        }
        return list;
    }

    public static ItemStack create(ICoreItem item, int minTier, int maxTier) {
        if (minTier >= maxTier)
            return create(item, maxTier);
        return create(item, minTier + SilentGear.random.nextInt(maxTier - minTier));
    }

    public static ItemStack create(ICoreItem item, int tier) {
        // Select main and rod
        Collection<IGearPart> mains = selectRandomMains(getMainPartCount(), tier);
        Optional<IGearPart> rod = selectRandom(PartType.ROD);
        if (mains.isEmpty() || !rod.isPresent()) {
            SilentGear.LOGGER.error("Could not create {} of tier {}", item.getGearType().getName(), tier);
            return ItemStack.EMPTY;
        }

        // Make the base item
        PartDataList parts = PartDataList.of();
        mains.forEach(p -> parts.add(PartData.of(p)));
        // Requires a rod?
        if (item.requiresPartOfType(PartType.ROD)) {
            parts.addPart(rod.get());
        }
        // Requires bowstring?
        if (item.requiresPartOfType(PartType.BOWSTRING)) {
            Optional<IGearPart> bowstring = selectRandom(PartType.BOWSTRING, tier);
            bowstring.ifPresent(parts::addPart);
        }
        ItemStack result = item.construct(parts);

        // Apply some random upgrades?
        if (item instanceof ICoreTool && SilentGear.random.nextFloat() < 0.2f * tier + 0.1f) {
            Optional<IGearPart> tip = selectRandom(PartType.TIP);
            tip.ifPresent(part -> GearData.addUpgradePart(result, PartData.of(part)));
        }

        GearData.recalculateStats(result, null);
        return result;
    }

    public static int getMainPartCount() {
        int randomMultiplier = 9 * (9 + 1) / 2;
        int randomInt = SilentGear.random.nextInt(randomMultiplier);

        int k = 0;
        for (int i = 9; randomInt >= 0; i--) {
            randomInt -= i;
            k++;
        }

        int result = k / 3;
        return result > 0 ? result : 1;
    }
}
