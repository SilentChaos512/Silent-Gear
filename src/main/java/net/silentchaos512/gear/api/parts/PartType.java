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

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.ToolHeadItem;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.type.*;
import net.silentchaos512.gear.util.ModResourceLocation;
import net.silentchaos512.lib.registry.ItemRegistryObject;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PartType {
    private static final Map<ResourceLocation, PartType> VALUES = new HashMap<>();

    public static final PartType BINDING = create(Builder.builder(SilentGear.getId("binding"))
            .serializer(createSerializer("binding", BindingPart::new))
            .compoundPartItem(() -> ModItems.BINDING.orElseThrow(IllegalStateException::new))
    );
    public static final PartType BOWSTRING = create(Builder.builder(SilentGear.getId("bowstring"))
            .serializer(createSerializer("bowstring", BowstringPart::new))
            .compoundPartItem(() -> ModItems.BOWSTRING.orElseThrow(IllegalStateException::new))
    );
    public static final PartType COATING = create(Builder.builder(SilentGear.getId("coating"))
            .compoundPartItem(() -> ModItems.COATING.orElseThrow(IllegalStateException::new))
    );
    public static final PartType FLETCHING = create(Builder.builder(SilentGear.getId("fletching"))
            .serializer(createSerializer("fletching", FletchingPart::new))
            .compoundPartItem(() -> ModItems.FLETCHING.orElseThrow(IllegalStateException::new))
    );
    public static final PartType GRIP = create(Builder.builder(SilentGear.getId("grip"))
            .serializer(createSerializer("grip", GripPart::new))
            .compoundPartItem(() -> ModItems.GRIP.orElseThrow(IllegalStateException::new))
    );
    public static final PartType MAIN = create(Builder.builder(SilentGear.getId("main"))
            .serializer(createSerializer("main", MainPart::new))
    );
    public static final PartType MISC_UPGRADE = create(Builder.builder(SilentGear.getId("misc_upgrade"))
            .serializer(createSerializer("misc_upgrade", UpgradePart::new))
    );
    public static final PartType ROD = create(Builder.builder(SilentGear.getId("rod"))
            .serializer(createSerializer("rod", RodPart::new))
            .compoundPartItem(() -> ModItems.ROD.orElseThrow(IllegalStateException::new))
    );
    public static final PartType TIP = create(Builder.builder(SilentGear.getId("tip"))
            .serializer(createSerializer("tip", TipPart::new))
            .compoundPartItem(() -> ModItems.TIP.orElseThrow(IllegalStateException::new))
    );

    @Deprecated
    public static PartType create(ResourceLocation name, String debugSymbol, IPartSerializer<? extends IGearPart> serializer) {
        return create(name, serializer);
    }

    public static PartType create(ResourceLocation name, IPartSerializer<? extends IGearPart> serializer) {
        return create(name, serializer, null, null);
    }

    public static PartType create(ResourceLocation name, IPartSerializer<? extends IGearPart> serializer, @Nullable ResourceLocation fallbackPart) {
        return create(name, serializer, fallbackPart, null);
    }

    public static PartType create(ResourceLocation name, IPartSerializer<? extends IGearPart> serializer, @Nullable Supplier<ItemRegistryObject<CompoundPartItem>> compoundPartItem) {
        return create(name, serializer, null, compoundPartItem);
    }

    public static PartType create(ResourceLocation name, IPartSerializer<? extends IGearPart> serializer, @Nullable ResourceLocation fallbackPart, @Nullable Supplier<ItemRegistryObject<CompoundPartItem>> compoundPartItem) {
        if (VALUES.containsKey(name))
            throw new IllegalArgumentException(String.format("Already have PartType \"%s\"", name));

        int maxPerItem = name.equals(SilentGear.getId("main")) ? 9 : 1;
        PartType type = new PartType(name, maxPerItem, serializer, fallbackPart, () -> compoundPartItem != null ? compoundPartItem.get().get() : null);
        VALUES.put(name, type);
        return type;
    }

    public static PartType create(Builder builder) {
        if (VALUES.containsKey(builder.name))
            throw new IllegalArgumentException(String.format("Already have PartType \"%s\"", builder.name));

        PartType type = new PartType(builder.name, 1, builder.serializer, null, builder.compoundPartItem);
        VALUES.put(builder.name, type);
        return type;
    }

    @Nullable
    public static PartType get(ResourceLocation name) {
        return VALUES.get(name);
    }

    public static Collection<PartType> getValues() {
        return VALUES.values();
    }

    public static PartType fromJson(JsonObject json, String key) {
        String str = JSONUtils.getString(json, key);
        PartType type = get(new ModResourceLocation(str));
        if (type == null) {
            throw new JsonSyntaxException("Unknown part type: " + str);
        }
        return type;
    }

    private final ResourceLocation name;
    private final int maxPerItem;
    @Nullable private final IPartSerializer<? extends IGearPart> serializer;
    @Nullable private final ResourceLocation fallbackPart;
    @Nullable private final Supplier<CompoundPartItem> compoundPartItem;

    private PartType(ResourceLocation name, int maxPerItem, @Nullable IPartSerializer<? extends IGearPart> serializer, @Nullable ResourceLocation fallbackPart, @Nullable Supplier<CompoundPartItem> compoundPartItem) {
        this.name = name;
        this.maxPerItem = maxPerItem;
        this.serializer = serializer;
        this.fallbackPart = fallbackPart;
        this.compoundPartItem = compoundPartItem;
    }

    public ResourceLocation getName() {
        return name;
    }

    public int getMaxPerItem() {
        return maxPerItem;
    }

    public IFormattableTextComponent getDisplayName(int tier) {
        return new TranslationTextComponent("part." + name.getNamespace() + ".type." + name.getPath());
    }

    @Deprecated
    @Nullable
    public IPartSerializer<? extends IGearPart> getSerializer() {
        return serializer;
    }

    @Deprecated
    @Nullable
    public IGearPart getFallbackPart() {
        if (fallbackPart == null) {
            return null;
        }
        return PartManager.get(fallbackPart);
    }

    public ResourceLocation getCompoundPartId(GearType gearType) {
        return getCompoundPartItem(gearType)
                .map(NameUtils::from)
                .orElseGet(() -> SilentGear.getId("invalid"));
    }

    public Optional<? extends CompoundPartItem> getCompoundPartItem(GearType gearType) {
        if (this == MAIN) {
            return ForgeRegistries.ITEMS.getValues().stream()
                    .filter(item -> item instanceof ToolHeadItem && gearType.matches(((ToolHeadItem) item).getGearType()))
                    .map(item -> (ToolHeadItem) item)
                    .findFirst();
        }
        if (compoundPartItem == null) {
            return Optional.empty();
        }
        return Optional.of(compoundPartItem.get());
    }

    @Override
    public String toString() {
        return "PartType{" +
                "name='" + name + "'}";
    }

    private static <T extends AbstractGearPart> IPartSerializer<T> createSerializer(String id, Function<ResourceLocation, T> function) {
        return new AbstractGearPart.Serializer<>(new ResourceLocation(SilentGear.MOD_ID, id), function);
    }

    public static final class Builder {
        private final ResourceLocation name;
        private IPartSerializer<? extends IGearPart> serializer;
        @Nullable private Supplier<CompoundPartItem> compoundPartItem;

        private Builder(ResourceLocation name) {
            this.name = name;
        }

        public static Builder builder(ResourceLocation name) {
            return new Builder(name);
        }

        public Builder serializer(IPartSerializer<? extends IGearPart> serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder compoundPartItem(Supplier<CompoundPartItem> item) {
            this.compoundPartItem = item;
            return this;
        }
    }
}
