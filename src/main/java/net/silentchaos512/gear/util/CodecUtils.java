package net.silentchaos512.gear.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CodecUtils {
    private CodecUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, TagKey<T>> tagStreamCodec(ResourceKey<? extends Registry<T>> registryKey) {
        return StreamCodec.of(
                (buf, val) -> buf.writeResourceLocation(val.location()),
                buf -> TagKey.create(registryKey, buf.readResourceLocation())
        );
    }

    public static <T> Codec<List<T>> singleOrListCodec(Codec<T> codec) {
        return Codec.either(
                codec,
                Codec.list(codec)
        ).xmap(
                either -> either.map(Collections::singletonList, list -> list),
                list -> {
                    if (list.size() == 1) {
                        return Either.left(list.getFirst());
                    }
                    return Either.right(list);
                }
        );
    }

    public static <B extends FriendlyByteBuf, T> void encodeList(B buf, Collection<T> list, StreamCodec<B, T> streamCodec) {
        buf.writeVarInt(list.size());
        for (T t : list) {
            streamCodec.encode(buf, t);
        }
    }

    public static <B extends FriendlyByteBuf, T> List<T> decodeList(B buf, StreamCodec<B, T> streamCodec) {
        int count = buf.readVarInt();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            list.add(streamCodec.decode(buf));
        }
        return list;
    }

    public static JsonElement encodeIngredient(Ingredient ingredient) {
        return Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow();
    }

    public static <T> Codec<T> byModNameCodec(Registry<T> registry) {
        return referenceHolderWithLifecycle(registry)
                .flatComapMap(Holder.Reference::value, t -> safeCastToReference(registry, registry.wrapAsHolder(t)));
    }

    private static <T> Codec<Holder.Reference<T>> referenceHolderWithLifecycle(Registry<T> registry) {
        Codec<Holder.Reference<T>> codec = ModResourceLocation.CODEC
                .comapFlatMap(
                        p_315852_ -> registry.getHolder(p_315852_)
                                .map(DataResult::success)
                                .orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + registry.key() + ": " + p_315852_)),
                        p_325513_ -> new ModResourceLocation(p_325513_.key().location())
                );
        return ExtraCodecs.overrideLifecycle(
                codec, p_325514_ -> registry.registrationInfo(p_325514_.key()).map(RegistrationInfo::lifecycle).orElse(Lifecycle.experimental())
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> DataResult<Holder.Reference<T>> safeCastToReference(Registry<T> registry, Holder<T> p_326365_) {
        return p_326365_.getDelegate() instanceof Holder.Reference reference
                ? DataResult.success(reference)
                : DataResult.error(() -> "Unregistered holder in " + registry.key() + ": " + p_326365_);
    }
}
