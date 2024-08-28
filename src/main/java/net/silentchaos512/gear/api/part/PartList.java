package net.silentchaos512.gear.api.part;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.CodecUtils;

import java.util.*;
import java.util.function.Predicate;

public class PartList implements List<PartInstance> {
    public static final Codec<PartList> CODEC = Codec.list(PartInstance.CODEC)
            .xmap(
                    PartList::of,
                    partList -> partList.list
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, PartList> STREAM_CODEC = StreamCodec.of(
            (buf, list) -> CodecUtils.encodeList(buf, list, PartInstance.STREAM_CODEC),
            buf -> PartList.of(CodecUtils.decodeList(buf, PartInstance.STREAM_CODEC))
    );

    final List<PartInstance> list = new ArrayList<>();

    private PartList() {
    }

    public static PartList empty() {
        return new PartList();
    }

    public static PartList of(Collection<PartInstance> c) {
        PartList ret = new PartList();
        ret.list.addAll(c);
        return ret;
    }

    public static PartList of(PartInstance... parts) {
        PartList ret = new PartList();
        Collections.addAll(ret.list, parts);
        return ret;
    }

    public static PartList.Immutable immutable(Collection<? extends PartInstance> c) {
        PartList.Immutable ret = new Immutable();
        ret.list.addAll(c);
        return ret;
    }

    public static PartList.Immutable immutable(PartInstance... parts) {
        PartList.Immutable ret = new Immutable();
        Collections.addAll(ret.list, parts);
        return ret;
    }

    public GearPropertyMap getPropertyModifiersFromParts(GearType gearType) {
        GearPropertyMap stats = new GearPropertyMap();

        for (GearProperty<?, ? extends GearPropertyValue<?>> property : SgRegistries.GEAR_PROPERTY) {
            PropertyKey<?, ?> key = PropertyKey.of(property, gearType);

            for (PartInstance part : this) {
                for (GearPropertyValue<?> mod : part.getPropertyModifiers(key)) {
                    stats.put(key, mod);
                }
            }
        }

        return stats;
    }

    public List<PartInstance> getMains() {
        return getPartsOfType(PartTypes.MAIN.get());
    }

    public List<PartInstance> getRods() {
        return getPartsOfType(PartTypes.ROD.get());
    }

    public List<PartInstance> getTips() {
        return getPartsOfType(PartTypes.TIP.get());
    }

    public List<PartInstance> getPartsOfType(PartType type) {
        return getParts(part -> part.getType() == type);
    }

    public List<PartInstance> getParts(Predicate<PartInstance> predicate) {
        ImmutableList.Builder<PartInstance> builder = ImmutableList.builder();
        for (PartInstance partData : this.list) {
            if (predicate.test(partData)) {
                builder.add(partData);
            }
        }
        return builder.build();
    }

    //region List overrides

    @Override
    public boolean add(PartInstance arg0) {
        return this.list.add(arg0);
    }

    @Override
    public void add(int arg0, PartInstance arg1) {
        this.list.add(arg0, arg1);
    }

    @Override
    public boolean addAll(Collection<? extends PartInstance> arg0) {
        return this.list.addAll(arg0);
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends PartInstance> arg1) {
        return this.list.addAll(arg0, arg1);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public boolean contains(Object arg0) {
        return this.list.contains(arg0);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return this.list.containsAll(arg0);
    }

    @Override
    public PartInstance get(int arg0) {
        return this.list.get(arg0);
    }

    @Override
    public int indexOf(Object arg0) {
        return this.list.indexOf(arg0);
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public Iterator<PartInstance> iterator() {
        return this.list.iterator();
    }

    @Override
    public int lastIndexOf(Object arg0) {
        return this.list.lastIndexOf(arg0);
    }

    @Override
    public ListIterator<PartInstance> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<PartInstance> listIterator(int arg0) {
        return this.list.listIterator(arg0);
    }

    @Override
    public boolean remove(Object arg0) {
        return this.list.remove(arg0);
    }

    @Override
    public PartInstance remove(int arg0) {
        return this.list.remove(arg0);
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        return this.list.removeAll(arg0);
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        return this.list.retainAll(arg0);
    }

    @Override
    public PartInstance set(int arg0, PartInstance arg1) {
        return this.list.set(arg0, arg1);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public List<PartInstance> subList(int arg0, int arg1) {
        return this.list.subList(arg0, arg1);
    }

    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return this.list.toArray(arg0);
    }

    public static class Immutable extends PartList {
        @Override
        public boolean add(PartInstance arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int arg0, PartInstance arg1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends PartInstance> arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int arg0, Collection<? extends PartInstance> arg1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PartInstance remove(int arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PartInstance set(int arg0, PartInstance arg1) {
            throw new UnsupportedOperationException();
        }
    }

    //endregion
}
