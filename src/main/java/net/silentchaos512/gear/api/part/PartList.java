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
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PartList extends AbstractList<PartInstance> {
    public static final Codec<PartList.Immutable> CODEC = Codec.list(PartInstance.CODEC)
            .xmap(
                    PartList::immutable,
                    partList -> partList.list
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, PartList.Immutable> STREAM_CODEC = StreamCodec.of(
            (buf, list) -> CodecUtils.encodeList(buf, list, PartInstance.STREAM_CODEC),
            buf -> PartList.immutable(CodecUtils.decodeList(buf, PartInstance.STREAM_CODEC))
    );

    final List<PartInstance> list = new ArrayList<>();

    private PartList() {
    }

    public static PartList.Immutable empty() {
        return Immutable.EMPTY;
    }

    public static PartList mutable(Collection<PartInstance> c) {
        PartList ret = new PartList();
        ret.addAll(c);
        return ret;
    }

    public static PartList mutable(PartInstance... parts) {
        PartList ret = new PartList();
        Collections.addAll(ret, parts);
        return ret;
    }

    public static PartList.Immutable immutable(Collection<PartInstance> c) {
        return new Immutable(c);
    }

    public static PartList.Immutable immutable(PartInstance... parts) {
        return new Immutable(parts);
    }

    public List<PartInstance> toSortedList() {
        var result = new ArrayList<PartInstance>();
        for (PartType partType : SgRegistries.PART_TYPE) {
            var subList = getPartsOfType(partType);
            if (subList.size() > 1) {
                var sortedList = new ArrayList<>(subList);
                sortedList.sort(Comparator.comparing(PartInstance::getId));
                result.addAll(sortedList);
            } else if (subList.size() == 1) {
                result.add(subList.getFirst());
            }
        }
        return result;
    }

    public GearPropertyMap getPropertyModifiersFromParts(GearType gearType) {
        GearPropertyMap stats = new GearPropertyMap();

        for (GearProperty<?, ? extends GearPropertyValue<?>> property : SgRegistries.GEAR_PROPERTY) {
            PropertyKey<?, ?> key = PropertyKey.of(property, gearType);

            for (PartInstance part : this) {
                if (!part.isValid()) continue;

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

    public PartList mutableCopy() {
        return PartList.mutable(this);
    }

    @Override
    public String toString() {
        var listText = this.list.stream()
                .map(part -> part != null && part.isValid() ? part.getDisplayName().getString() : null)
                .collect(Collectors.joining(", "));
        return "PartList[" + listText + "]";
    }

    private void validateNotNull(PartInstance part) {
        Validate.notNull(part, "Gear part is null");
    }

    //region List overrides

    @Override
    public void add(int arg0, PartInstance arg1) {
        validateNotNull(arg1);
        this.list.add(arg0, arg1);
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
    public PartInstance get(int arg0) {
        return this.list.get(arg0);
    }

    @Override
    public int indexOf(Object arg0) {
        return this.list.indexOf(arg0);
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
    public PartInstance set(int arg0, PartInstance arg1) {
        validateNotNull(arg1);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof PartList)) return false;

        ListIterator<PartInstance> iterator1 = listIterator();
        ListIterator<PartInstance> iterator2 = ((PartList) obj).listIterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            var part1 = iterator1.next();
            var part2 = iterator2.next();
            if (!(Objects.equals(part1, part2))) {
                return false;
            }
        }
        return !(iterator1.hasNext() || iterator2.hasNext());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (PartInstance part : this)
            hashCode = 31*hashCode + (part==null ? 0 : part.hashCode());
        return hashCode;
    }

    public static class Immutable extends PartList {
        static final Immutable EMPTY = new Immutable();

        private Immutable(Collection<PartInstance> parts) {
            this.list.addAll(parts);
        }

        private Immutable(PartInstance... parts) {
            Collections.addAll(this.list, parts);
        }

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
