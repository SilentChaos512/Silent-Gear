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

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.ToolHeadItem;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartConst;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.type.*;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PartType {
    private static final Map<ResourceLocation, PartType> VALUES = new HashMap<>();

    public static final PartType BINDING = create(
            SilentGear.getId("binding"),
            createSerializer("binding", BindingPart::new),
            () -> ModItems.BINDING.getRegistryObject()
    );
    public static final PartType BOWSTRING = create(
            SilentGear.getId("bowstring"),
            createSerializer("bowstring", BowstringPart::new),
            PartConst.FALLBACK_BOWSTRING
    );
    public static final PartType GRIP = create(
            SilentGear.getId("grip"),
            createSerializer("grip", GripPart::new),
            () -> ModItems.GRIP.getRegistryObject()
    );
    @Deprecated
    public static final PartType HIGHLIGHT = create(
            SilentGear.getId("highlight"),
            createSerializer("highlight", HighlightPart::new)
    );
    public static final PartType MAIN = create(
            SilentGear.getId("main"),
            createSerializer("main", MainPart::new),
            PartConst.FALLBACK_MAIN
    );
    public static final PartType MISC_UPGRADE = create(
            SilentGear.getId("misc_upgrade"),
            createSerializer("misc_upgrade", UpgradePart::new)
    );
    public static final PartType ROD = create(
            SilentGear.getId("rod"),
            createSerializer("rod", RodPart::new),
            PartConst.FALLBACK_ROD,
            () -> ModItems.ROD.getRegistryObject()
    );
    public static final PartType TIP = create(
            SilentGear.getId("tip"),
            createSerializer("tip", TipPart::new),
            () -> ModItems.TIP.getRegistryObject()
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

    public static PartType create(ResourceLocation name, IPartSerializer<? extends IGearPart> serializer, @Nullable Supplier<RegistryObject<CompoundPartItem>> compoundPartItem) {
        return create(name, serializer, null, compoundPartItem);
    }

    public static PartType create(ResourceLocation name, IPartSerializer<? extends IGearPart> serializer, @Nullable ResourceLocation fallbackPart, @Nullable Supplier<RegistryObject<CompoundPartItem>> compoundPartItem) {
        if (VALUES.containsKey(name))
            throw new IllegalArgumentException(String.format("Already have PartType \"%s\"", name));

        int maxPerItem = name.equals(SilentGear.getId("main")) ? 9 : 1;
        PartType type = new PartType(name, maxPerItem, serializer, fallbackPart, compoundPartItem);
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

    private final ResourceLocation name;
    private final int maxPerItem;
    private final IPartSerializer<? extends IGearPart> serializer;
    @Nullable private final ResourceLocation fallbackPart;
    @Nullable private final Supplier<RegistryObject<CompoundPartItem>> compoundPartItem;

    private PartType(ResourceLocation name, int maxPerItem, IPartSerializer<? extends IGearPart> serializer, @Nullable ResourceLocation fallbackPart, @Nullable Supplier<RegistryObject<CompoundPartItem>> compoundPartItem) {
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

    public ITextComponent getDisplayName(int tier) {
        return new TranslationTextComponent("part." + name.getNamespace() + ".type." + name.getPath(), tier);
    }

    public IPartSerializer<? extends IGearPart> getSerializer() {
        return serializer;
    }

    @Nullable
    public IGearPart getFallbackPart() {
        if (fallbackPart == null) {
            return null;
        }
        return PartManager.get(fallbackPart);
    }

    public Optional<? extends CompoundPartItem> getCompoundPartItem(GearType gearType) {
        if (this == MAIN) {
            return ForgeRegistries.ITEMS.getValues().stream()
                    .filter(item -> item instanceof ToolHeadItem && gearType.matches(((ToolHeadItem) item).getGearType()))
                    .map(item -> (ToolHeadItem) item)
                    .findFirst();
        }
        if (compoundPartItem == null || !compoundPartItem.get().isPresent()) {
            return Optional.empty();
        }
        return Optional.of(compoundPartItem.get().get());
    }

    @Override
    public String toString() {
        return "PartType{" +
                "name='" + name + "'}";
    }

    private static <T extends AbstractGearPart> IPartSerializer<T> createSerializer(String id, Function<ResourceLocation, T> function) {
        return new AbstractGearPart.Serializer<>(new ResourceLocation(SilentGear.MOD_ID, id), function);
    }
}
