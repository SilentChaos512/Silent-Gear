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

package net.silentchaos512.gear.api.part;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.part.LazyPartData;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.ToolHeadItem;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.gear.util.ModResourceLocation;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PartType {
    private static final Map<ResourceLocation, PartType> VALUES = new LinkedHashMap<>();
    private static final Map<PartGearKey, Optional<CompoundPartItem>> ITEM_CACHE = new HashMap<>();

    public static final PartType NONE = create(Builder.builder(SilentGear.getId("none")));

    public static final PartType ADORNMENT = create(Builder.builder(SilentGear.getId("adornment"))
            .compoundPartItem(() -> ModItems.ADORNMENT.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.ADORNMENT_GENERIC)
            .isRemovable(true)
    );
    public static final PartType BINDING = create(Builder.builder(SilentGear.getId("binding"))
            .compoundPartItem(() -> ModItems.BINDING.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.BINDING_GENERIC)
            .isRemovable(true)
    );
    public static final PartType BOWSTRING = create(Builder.builder(SilentGear.getId("bowstring")) // TODO: Rename to "cord"
            .compoundPartItem(() -> ModItems.BOWSTRING.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.BOWSTRING_STRING)
    );
    public static final PartType COATING = create(Builder.builder(SilentGear.getId("coating"))
            .compoundPartItem(() -> ModItems.COATING.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.MAIN_GENERIC_HC)
            .isRemovable(true)
    );
    public static final PartType FLETCHING = create(Builder.builder(SilentGear.getId("fletching"))
            .compoundPartItem(() -> ModItems.FLETCHING.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.FLETCHING_GENERIC)
    );
    public static final PartType GRIP = create(Builder.builder(SilentGear.getId("grip"))
            .compoundPartItem(() -> ModItems.GRIP.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.GRIP_WOOL)
            .isRemovable(true)
    );
    public static final PartType LINING = create(Builder.builder(SilentGear.getId("lining"))
            .compoundPartItem(() -> ModItems.LINING.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.LINING_CLOTH)
            .isRemovable(true)
    );
    public static final PartType MAIN = create(Builder.builder(SilentGear.getId("main"))
            .compoundPartItem(PartType::getToolHeadItem)
            .defaultTexture(PartTextures.MAIN_GENERIC_HC)
    );
    public static final PartType MISC_UPGRADE = create(Builder.builder(SilentGear.getId("misc_upgrade"))
            .isRemovable(true)
            .isUpgrade(true)
            .maxPerItem(Integer.MAX_VALUE)
    );
    public static final PartType ROD = create(Builder.builder(SilentGear.getId("rod"))
            .compoundPartItem(() -> ModItems.ROD.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.ROD_GENERIC_LC)
    );
    public static final PartType TIP = create(Builder.builder(SilentGear.getId("tip"))
            .compoundPartItem(() -> ModItems.TIP.orElseThrow(IllegalStateException::new))
            .defaultTexture(PartTextures.TIP_SHARP)
            .isRemovable(true)
    );

    /**
     * Call during mod construction to create a new part type.
     *
     * @param builder Type builder
     * @return The new PartType
     * @throws IllegalArgumentException if a type with the same name already exists
     */
    public static PartType create(Builder builder) {
        if (VALUES.containsKey(builder.name))
            throw new IllegalArgumentException(String.format("Already have PartType \"%s\"", builder.name));

        PartType type = new PartType(builder);
        VALUES.put(builder.name, type);
        return type;
    }

    @Nullable
    public static PartType get(ResourceLocation name) {
        return VALUES.get(name);
    }

    public static PartType getNonNull(ResourceLocation name) {
        PartType type = get(name);
        return type != null ? type : NONE;
    }

    public static Collection<PartType> getValues() {
        return VALUES.values();
    }

    public static PartType fromJson(JsonObject json, String key) {
        String str = JSONUtils.getAsString(json, key);
        PartType type = get(new ModResourceLocation(str));
        if (type == null) {
            throw new JsonSyntaxException("Unknown part type: " + str);
        }
        return type;
    }

    private final ResourceLocation name;
    private final boolean isRemovable;
    private final boolean isUpgrade;
    private final Function<GearType, Integer> maxPerItem;
    @Nullable private final Function<GearType, Optional<CompoundPartItem>> compoundPartItem;
    @Nullable private final PartTextures defaultTexture;

    private PartType(Builder builder) {
        this.name = builder.name;
        this.isRemovable = builder.isRemovable;
        this.isUpgrade = builder.isUpgrade;
        this.maxPerItem = builder.maxPerItem;
        this.compoundPartItem = builder.compoundPartItem;
        this.defaultTexture = builder.defaultTexture;
    }

    public boolean isInvalid() {
        return this == NONE;
    }

    public ResourceLocation getName() {
        return name;
    }

    public String getShortName() {
        return SilentGear.shortenId(name);
    }

    public boolean isRemovable() {
        return isRemovable;
    }

    public boolean isUpgrade() {
        return isUpgrade;
    }

    public int getMaxPerItem(GearType gearType) {
        return maxPerItem.apply(gearType);
    }

    public IFormattableTextComponent getDisplayName(int tier) {
        return new TranslationTextComponent("part." + name.getNamespace() + ".type." + name.getPath());
    }

    @SuppressWarnings("WeakerAccess")
    public ResourceLocation getCompoundPartId(GearType gearType) {
        return getCompoundPartItem(gearType)
                .map(NameUtils::from)
                .orElseGet(() -> SilentGear.getId("invalid"));
    }

    public Optional<? extends CompoundPartItem> getCompoundPartItem(GearType gearType) {
        if (compoundPartItem == null) {
            return Optional.empty();
        }
        PartGearKey key = PartGearKey.of(gearType, this);
        return ITEM_CACHE.computeIfAbsent(key, gt -> compoundPartItem.apply(gearType));
    }

    public Optional<? extends IPartData> makeCompoundPart(GearType gearType, DataResource<IMaterial> material) {
        return makeCompoundPart(gearType, Collections.singletonList(LazyMaterialInstance.of(material)));
    }

    @SuppressWarnings("WeakerAccess")
    public Optional<? extends IPartData> makeCompoundPart(GearType gearType, List<IMaterialInstance> materials) {
        return getCompoundPartItem(gearType)
                .map(item -> {
                    ItemStack stack = item.create(materials);
                    return LazyPartData.of(this.getCompoundPartId(gearType), stack);
                });
    }

    private static Optional<CompoundPartItem> getToolHeadItem(GearType gearType) {
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof ToolHeadItem && gearType == ((ToolHeadItem) item).getGearType()) {
                return Optional.of((CompoundPartItem) item);
            }
        }

        return Optional.empty();
    }

    @Nullable
    public PartTextures getDefaultTexture() {
        return defaultTexture;
    }

    @Override
    public String toString() {
        return "PartType{" +
                "name='" + name + "'}";
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        private final ResourceLocation name;
        private boolean isRemovable = false;
        private boolean isUpgrade = false;
        @Nullable private Function<GearType, Optional<CompoundPartItem>> compoundPartItem;
        private Function<GearType, Integer> maxPerItem = gt -> 1;
        @Nullable private PartTextures defaultTexture;

        private Builder(ResourceLocation name) {
            this.name = name;
        }

        public static Builder builder(ResourceLocation name) {
            return new Builder(name);
        }

        public Builder isRemovable(boolean value) {
            this.isRemovable = value;
            return this;
        }

        public Builder isUpgrade(boolean value) {
            this.isUpgrade = value;
            return this;
        }

        public Builder compoundPartItem(Supplier<CompoundPartItem> item) {
            return compoundPartItem(gt -> Optional.ofNullable(item.get()));
        }

        public Builder compoundPartItem(Function<GearType, Optional<CompoundPartItem>> itemGetter) {
            this.compoundPartItem = itemGetter;
            return this;
        }

        public Builder maxPerItem(int value) {
            return maxPerItem(gt -> value);
        }

        public Builder maxPerItem(Function<GearType, Integer> function) {
            this.maxPerItem = function;
            return this;
        }

        public Builder defaultTexture(PartTextures texture) {
            this.defaultTexture = texture;
            return this;
        }
    }
}
