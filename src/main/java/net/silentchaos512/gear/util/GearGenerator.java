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
import net.silentchaos512.gear.parts.type.PartBowstring;
import net.silentchaos512.gear.parts.type.PartMain;
import net.silentchaos512.gear.parts.type.PartRod;
import net.silentchaos512.gear.parts.type.PartTip;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// FIXME: modify to use PartType?
public final class GearGenerator {
    private GearGenerator() {}

    public static List<? extends IGearPart> getPartsOfType(Class<? extends IGearPart> partClass) {
        return getPartsOfType(partClass::isInstance);
    }

    public static List<? extends IGearPart> getPartsOfType(Class<? extends IGearPart> partClass, int partTier) {
        return getPartsOfType(part -> partClass.isInstance(part) && (partTier < 0 || part.getTier() == partTier));
    }

    public static List<? extends IGearPart> getPartsOfType(Predicate<? super IGearPart> condition) {
        return PartManager.getValues().stream()
//                .filter(p -> !p.isBlacklisted())
                .filter(condition)
                .collect(Collectors.toList());
    }

    public static Optional<IGearPart> selectRandom(Class<? extends IGearPart> partClass) {
        return selectRandom(partClass, -1);
    }

    public static Optional<IGearPart> selectRandom(Class<? extends IGearPart> partClass, int partTier) {
        List<? extends IGearPart> list = getPartsOfType(partClass, partTier);
        if (!list.isEmpty())
            return Optional.of(list.get(SilentGear.random.nextInt(list.size())));
        IGearPart fallback = getFallback(partClass);
        return fallback == null ? Optional.empty() : Optional.of(fallback);
    }

    @Nullable
    private static IGearPart getFallback(Class<? extends IGearPart> partClass) {
        SilentGear.LOGGER.debug("GearGenerator::getFallback: class {}", partClass);

        if (partClass == PartRod.class)
            return PartManager.get(PartConst.FALLBACK_ROD);
        else if (partClass == PartBowstring.class)
            return selectRandom(partClass, -1).orElse(PartManager.get(PartConst.FALLBACK_BOWSTRING));

        SilentGear.LOGGER.debug("    no fallback part available");
        return null;
    }

    public static ItemStack create(ICoreItem item, int minTier, int maxTier) {
        if (minTier >= maxTier)
            return create(item, maxTier);
        return create(item, minTier + SilentGear.random.nextInt(maxTier - minTier));
    }

    public static ItemStack create(ICoreItem item, int tier) {
        // Select main and rod
        Optional<IGearPart> main = selectRandom(PartMain.class, tier);
        Optional<IGearPart> rod = selectRandom(PartRod.class, tier);
        if (!main.isPresent() || !rod.isPresent()) {
            SilentGear.LOGGER.warn("Could not create {} of tier {}", item.getGearType().getName(), tier);
            return ItemStack.EMPTY;
        }

        // Make the base item
        PartDataList parts = PartDataList.of();
        // Proper number of mains for head -- FIXME
        for (int i = 0; i < 1; ++i) {
            parts.addPart(main.get());
        }
        // Requires a rod?
        if (item.requiresPartOfType(PartType.ROD)) {
            parts.addPart(rod.get());
        }
        // Requires bowstring?
        if (item.requiresPartOfType(PartType.BOWSTRING)) {
            Optional<IGearPart> bowstring = selectRandom(PartBowstring.class, tier);
            bowstring.ifPresent(parts::addPart);
        }
        ItemStack result = item.construct(parts);

        // Apply some random upgrades?
        if (item instanceof ICoreTool && SilentGear.random.nextFloat() < 0.2f * tier + 0.1f) {
            Optional<IGearPart> tip = selectRandom(PartTip.class);
            tip.ifPresent(part -> GearData.addUpgradePart(result, PartData.of(part)));
        }

        GearData.recalculateStats(null, result);
        return result;
    }
}
