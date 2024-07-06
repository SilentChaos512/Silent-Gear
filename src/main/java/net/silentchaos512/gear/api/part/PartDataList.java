/*
 * Silent Gear -- PartDataList
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

import com.google.common.collect.ImmutableList;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.util.*;
import java.util.function.Predicate;

public class PartDataList implements List<IPartData> {
    final List<IPartData> list = new ArrayList<>();

    private PartDataList() {
    }

    public static PartDataList empty() {
        return new PartDataList();
    }

    public static PartDataList of(Collection<IPartData> c) {
        PartDataList ret = new PartDataList();
        ret.list.addAll(c);
        return ret;
    }

    public static PartDataList of(IPartData... parts) {
        PartDataList ret = new PartDataList();
        Collections.addAll(ret.list, parts);
        return ret;
    }

    public static PartDataList.Immutable immutable(Collection<? extends IPartData> c) {
        PartDataList.Immutable ret = new Immutable();
        ret.list.addAll(c);
        return ret;
    }

    public static PartDataList.Immutable immutable(IPartData... parts) {
        PartDataList.Immutable ret = new Immutable();
        Collections.addAll(ret.list, parts);
        return ret;
    }

    public List<IPartData> getMains() {
        return getPartsOfType(PartTypes.MAIN.get());
    }

    public List<IPartData> getRods() {
        return getPartsOfType(PartTypes.ROD.get());
    }

    public List<IPartData> getTips() {
        return getPartsOfType(PartTypes.TIP.get());
    }

    public List<IPartData> getPartsOfType(PartType type) {
        return getParts(part -> part.getType() == type);
    }

    public List<IPartData> getParts(Predicate<IPartData> predicate) {
        ImmutableList.Builder<IPartData> builder = ImmutableList.builder();
        for (IPartData partData : this.list) {
            if (predicate.test(partData)) {
                builder.add(partData);
            }
        }
        return builder.build();
    }

    //region List overrides

    @Override
    public boolean add(IPartData arg0) {
        return this.list.add(arg0);
    }

    @Override
    public void add(int arg0, IPartData arg1) {
        this.list.add(arg0, arg1);
    }

    @Override
    public boolean addAll(Collection<? extends IPartData> arg0) {
        return this.list.addAll(arg0);
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends IPartData> arg1) {
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
    public IPartData get(int arg0) {
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
    public Iterator<IPartData> iterator() {
        return this.list.iterator();
    }

    @Override
    public int lastIndexOf(Object arg0) {
        return this.list.lastIndexOf(arg0);
    }

    @Override
    public ListIterator<IPartData> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<IPartData> listIterator(int arg0) {
        return this.list.listIterator(arg0);
    }

    @Override
    public boolean remove(Object arg0) {
        return this.list.remove(arg0);
    }

    @Override
    public IPartData remove(int arg0) {
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
    public IPartData set(int arg0, IPartData arg1) {
        return this.list.set(arg0, arg1);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public List<IPartData> subList(int arg0, int arg1) {
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

    public static class Immutable extends PartDataList {
        @Override
        public boolean add(IPartData arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int arg0, IPartData arg1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends IPartData> arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int arg0, Collection<? extends IPartData> arg1) {
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
        public IPartData remove(int arg0) {
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
        public IPartData set(int arg0, IPartData arg1) {
            throw new UnsupportedOperationException();
        }
    }

    //endregion
}
