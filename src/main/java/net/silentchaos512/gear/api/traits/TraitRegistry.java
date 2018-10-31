/*
 * Silent Gear -- TraitRegistry
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

package net.silentchaos512.gear.api.traits;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TraitRegistry {
    private static final Map<String, Trait> MAP = new LinkedHashMap<>();

    private TraitRegistry() { throw new IllegalAccessError("Utility class"); }

    @Nullable
    public static Trait get(String key) {
        return MAP.get(key);
    }

    public static <T extends Trait> T register(T trait) {
        String key = trait.getName().toString();
        if (MAP.containsKey(key))
            throw new IllegalArgumentException("Already have a trait with key " + key);
        MAP.put(key, trait);

        return trait;
    }
}
