/*
 * Silent Gear -- PartType
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

import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.type.*;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class PartType {
    private static final Map<ResourceLocation, PartType> VALUES = new HashMap<>();

    public static final PartType BINDING = create(SilentGear.getId("binding"), "b", createSerializer("binding", BindingPart::new));
    public static final PartType BOWSTRING = create(SilentGear.getId("bowstring"), "B", createSerializer("bowstring", BowstringPart::new));
    public static final PartType GRIP = create(SilentGear.getId("grip"), "G", createSerializer("grip", GripPart::new));
    public static final PartType HIGHLIGHT = create(SilentGear.getId("highlight"), "h", createSerializer("highlight", HighlightPart::new));
    public static final PartType MAIN = create(SilentGear.getId("main"), "M", createSerializer("main", MainPart::new));
    public static final PartType MISC_UPGRADE = create(SilentGear.getId("misc_upgrade"), "U", createSerializer("misc_upgrade", UpgradePart::new));
    public static final PartType ROD = create(SilentGear.getId("rod"), "R", createSerializer("rod", RodPart::new));
    public static final PartType TIP = create(SilentGear.getId("tip"), "T", createSerializer("tip", TipPart::new));

    @Deprecated
    public static PartType create(String name, String debugSymbol, IPartSerializer<? extends IGearPart> serializer) {
        return create(new ResourceLocation(name), debugSymbol, serializer);
    }

    public static PartType create(ResourceLocation name, String debugSymbol, IPartSerializer<? extends IGearPart> serializer) {
        if (VALUES.containsKey(name))
            throw new IllegalArgumentException(String.format("Already have PartType \"%s\"", name));

        int maxPerItem = name.equals(SilentGear.getId("main")) ? 9 : 1;
        PartType type = new PartType(name, debugSymbol, maxPerItem, serializer);
        VALUES.put(name, type);
        return type;
    }

    @Deprecated
    @Nullable
    public static PartType get(String name) {
        return get(new ResourceLocation(name));
    }

    @Nullable
    public static PartType get(ResourceLocation name) {
        return VALUES.get(name);
    }

    public static Collection<PartType> getValues() {
        return VALUES.values();
    }

    @Getter private final ResourceLocation name;
    @Getter private final String debugSymbol;
    @Getter private final int maxPerItem;
    private final IPartSerializer<? extends IGearPart> serializer;

    private PartType(ResourceLocation name, String debugSymbol, int maxPerItem, IPartSerializer<? extends IGearPart> serializer) {
        this.name = name;
        this.debugSymbol = debugSymbol;
        this.maxPerItem = maxPerItem;
        this.serializer = serializer;
    }

    public ITextComponent getDisplayName(int tier) {
        return new TranslationTextComponent("part." + name.getNamespace() + ".type." + name.getPath(), tier);
    }

    public IPartSerializer<? extends IGearPart> getSerializer() {
        return serializer;
    }

    @Override
    public String toString() {
        return "PartType[" + debugSymbol + "]{" +
                "name='" + name + "'}";
    }

    private static <T extends AbstractGearPart> IPartSerializer<T> createSerializer(String id, Function<ResourceLocation, T> function) {
        return new AbstractGearPart.Serializer<>(new ResourceLocation(SilentGear.MOD_ID, id), function);
    }
}
