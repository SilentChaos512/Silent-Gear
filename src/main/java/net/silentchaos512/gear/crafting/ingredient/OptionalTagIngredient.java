package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.setup.SgIngredientTypes;

import java.util.stream.Stream;

public class OptionalTagIngredient implements ICustomIngredient {
    public static final MapCodec<OptionalTagIngredient> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.xmap(
                                    str -> {
                                        String trim = str.replace("#", "");
                                        ResourceLocation location = ResourceLocation.parse(trim);
                                        return TagKey.create(Registries.ITEM, location);
                                    },
                                    tag -> {
                                        return "#" + tag.location();
                                    }
                            )
                            .fieldOf("tag")
                            .forGetter(ing -> ing.tag)
            ).apply(instance, tag -> new OptionalTagIngredient(VanillaRegistries.createLookup().lookupOrThrow(Registries.ITEM), tag))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, OptionalTagIngredient> STREAM_CODEC = StreamCodec.composite(
            TagKey.streamCodec(Registries.ITEM), ing -> ing.tag,
            tag -> new OptionalTagIngredient(VanillaRegistries.createLookup().lookupOrThrow(Registries.ITEM), tag)
    );

    private final TagKey<Item> tag;
    private final HolderSet<Item> values;

    public OptionalTagIngredient(HolderGetter<Item> items, TagKey<Item> tag) {
        this.tag = tag;
        this.values = items.getOrThrow(tag);
    }

    public static Ingredient create(HolderGetter<Item> items, TagKey<Item> tag) {
        return new OptionalTagIngredient(items, tag).toVanilla();
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.is(this.tag);
    }

    @Override
    public Stream<Holder<Item>> items() {
        return this.values.stream();
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public IngredientType<?> getType() {
        return SgIngredientTypes.OPTIONAL_TAG.get();
    }
}
