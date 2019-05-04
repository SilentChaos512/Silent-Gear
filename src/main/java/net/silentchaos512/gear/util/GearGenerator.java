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
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.item.ToolRods;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// FIXME: modify to use PartType?
public final class GearGenerator {
    private GearGenerator() {}

    public static List<? extends ItemPart> getPartsOfType(Class<? extends ItemPart> partClass) {
        return getPartsOfType(partClass::isInstance);
    }

    public static List<? extends ItemPart> getPartsOfType(Class<? extends ItemPart> partClass, int partTier) {
        return getPartsOfType(part -> partClass.isInstance(part) && (partTier < 0 || part.getTier() == partTier));
    }

    public static List<? extends ItemPart> getPartsOfType(Predicate<? super ItemPart> condition) {
        return PartRegistry.getValues().stream()
                .filter(p -> !p.isBlacklisted())
                .filter(condition)
                .collect(Collectors.toList());
    }

    public static Optional<ItemPart> selectRandom(Class<? extends ItemPart> partClass) {
        return selectRandom(partClass, -1);
    }

    public static Optional<ItemPart> selectRandom(Class<? extends ItemPart> partClass, int partTier) {
        List<? extends ItemPart> list = getPartsOfType(partClass, partTier);
        if (!list.isEmpty())
            return Optional.of(list.get(SilentGear.RANDOM.nextInt(list.size())));
        ItemPart fallback = getFallback(partClass);
        return fallback == null ? Optional.empty() : Optional.of(fallback);
    }

    @Nullable
    private static ItemPart getFallback(Class<? extends ItemPart> partClass) {
        SilentGear.log.debug("GearGenerator::getFallback: class {}", partClass);

        if (partClass == PartRod.class)
            return ToolRods.WOOD.getPart();
        else if (partClass == PartBowstring.class)
            return selectRandom(partClass, -1).orElse(ModMaterials.bowstringString);

        SilentGear.log.debug("    no fallback part available");
        return null;
    }

    public static ItemStack create(ICoreItem item, int minTier, int maxTier) {
        if (minTier >= maxTier)
            return create(item, maxTier);
        return create(item, minTier + SilentGear.RANDOM.nextInt(maxTier - minTier));
    }

    public static ItemStack create(ICoreItem item, int tier) {
        // Select main and rod
        Optional<ItemPart> main = selectRandom(PartMain.class, tier);
        Optional<ItemPart> rod = selectRandom(PartRod.class, tier);
        if (!main.isPresent() || !rod.isPresent()) {
            SilentGear.log.warn("Could not create {} of tier {}", item.getGearClass(), tier);
            return ItemStack.EMPTY;
        }

        // Make the base item
        PartDataList parts = PartDataList.of();
        // Proper number of mains for head
        for (int i = 0; i < item.getConfig().getHeadCount(); ++i)
            parts.addPart(main.get());
        // Requires a rod?
        if (item.getConfig().getRodCount() > 0)
            parts.addPart(rod.get());
        // Requires bowstring?
        if (item.getConfig().getBowstringCount() > 0) {
            Optional<ItemPart> bowstring = selectRandom(PartBowstring.class, tier);
            bowstring.ifPresent(parts::addPart);
        }
        ItemStack result = item.construct(item.getItem(), parts);

        // Apply some random upgrades?
        if (item instanceof ICoreTool && SilentGear.RANDOM.nextFloat() < 0.2f * tier + 0.1f) {
            Optional<ItemPart> tip = selectRandom(PartTip.class);
            tip.ifPresent(part -> GearData.addUpgradePart(result, ItemPartData.instance(part)));
        }

        GearData.recalculateStats(result);
        return result;
    }
}
