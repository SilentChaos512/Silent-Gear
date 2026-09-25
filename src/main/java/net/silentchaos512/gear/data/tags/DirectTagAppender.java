package net.silentchaos512.gear.data.tags;

import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Preserves the convenient instance-based tag generator API while Minecraft's
 * data generators require registry keys.
 */
public final class DirectTagAppender<T> implements TagAppender<T> {
    private final TagAppender<T> delegate;
    private final Function<T, ResourceKey<T>> keyGetter;

    public DirectTagAppender(TagAppender<T> delegate, Function<T, ResourceKey<T>> keyGetter) {
        this.delegate = delegate;
        this.keyGetter = keyGetter;
    }

    @SafeVarargs
    public final DirectTagAppender<T> add(T... elements) {
        for (T element : elements) {
            delegate.add(keyGetter.apply(element));
        }
        return this;
    }

    @Override
    public DirectTagAppender<T> add(ResourceKey<T> element) {
        delegate.add(element);
        return this;
    }

    @Override
    public DirectTagAppender<T> add(ResourceKey<T>... elements) {
        delegate.add(elements);
        return this;
    }

    @Override
    public DirectTagAppender<T> addAll(Collection<ResourceKey<T>> elements) {
        delegate.addAll(elements);
        return this;
    }

    @Override
    public DirectTagAppender<T> addAll(Stream<ResourceKey<T>> elements) {
        delegate.addAll(elements);
        return this;
    }

    @Override
    public DirectTagAppender<T> addOptional(ResourceKey<T> element) {
        delegate.addOptional(element);
        return this;
    }

    @Override
    public DirectTagAppender<T> addTag(TagKey<T> tag) {
        delegate.addTag(tag);
        return this;
    }

    @Override
    public DirectTagAppender<T> addOptionalTag(TagKey<T> tag) {
        delegate.addOptionalTag(tag);
        return this;
    }

    @Override
    public DirectTagAppender<T> add(TagEntry entry) {
        delegate.add(entry);
        return this;
    }

    @Override
    public DirectTagAppender<T> replace(boolean value) {
        delegate.replace(value);
        return this;
    }

    @Override
    public DirectTagAppender<T> remove(TagKey<T> tag) {
        delegate.remove(tag);
        return this;
    }

    @Override
    public DirectTagAppender<T> remove(ResourceKey<T> element) {
        delegate.remove(element);
        return this;
    }
}
