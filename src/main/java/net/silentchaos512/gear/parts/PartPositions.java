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

package net.silentchaos512.gear.parts;

import com.google.common.collect.ImmutableMap;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.PartType;

import java.util.Map;

public enum PartPositions implements IPartPosition {
    ANY("head", "any", false),
    ARMOR("main", "main", false),
    ROD("rod", "rod", true),
    GRIP("grip", "grip", true),
    HEAD("head", "head", true),
    GUARD("guard", "guard", true),
    HIGHLIGHT("", "highlight", true),
    TIP("tip", "tip", true),
    BOWSTRING("bowstring", "bowstring", true),
    BINDING("binding", "binding", true);

    public static final Map<PartPositions, PartType> LITE_MODEL_LAYERS = ImmutableMap.<PartPositions, PartType>builder()
            .put(ROD, PartType.ROD)
            .put(GRIP, PartType.GRIP)
            .put(HEAD, PartType.MAIN)
            .put(TIP, PartType.TIP)
            .put(BOWSTRING, PartType.BOWSTRING)
            .build();

    private final String texturePrefix;
    private final String modelKey;

    PartPositions(String texture, String model, boolean isRenderLayer) {
        this.texturePrefix = texture;
        this.modelKey = model;

        if (isRenderLayer) RENDER_LAYERS.add(this);
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
