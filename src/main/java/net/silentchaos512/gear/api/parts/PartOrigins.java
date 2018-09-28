/*
 * Silent Gear -- PartOrigins
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

package net.silentchaos512.gear.api.parts;

public enum PartOrigins {
    /**
     * Built-in part of Silent Gear. Using this in an add-on mod will throw an exception.
     */
    BUILTIN_CORE,
    /**
     * Built-in part of another mod. This is the only valid origin for add-on mods.
     */
    BUILTIN_ADDON,
    /**
     * A part defined in the config folder (data packs in 1.13+, hopefully).
     */
    USER_DEFINED;

    public boolean isBuiltin() {
        return this == BUILTIN_CORE || this == BUILTIN_ADDON;
    }

    public boolean isUserDefined() {
        return this == USER_DEFINED;
    }
}
