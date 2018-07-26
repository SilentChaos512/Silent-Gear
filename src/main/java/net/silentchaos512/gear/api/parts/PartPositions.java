/*
 * Silent Gear -- PartPositions
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

public enum PartPositions implements IPartPosition {
    ANY("head", "any"),
    HEAD("head", "head"),
    GUARD("guard", "guard"),
    ROD("rod", "rod"),
    TIP("tip", "tip"),
    GRIP("grip", "grip"),
    BOWSTRING("bowstring", "bowstring"),
    ARMOR("main", "main");

    private final String texturePrefix;
    private final String modelKey;

    PartPositions(String texture, String model) {
        this.texturePrefix = texture;
        this.modelKey = model;
    }

    @Override
    public String getTexturePrefix() {
        return texturePrefix;
    }

    @Override
    public String getModelIndex() {
        return modelKey;
    }
}
