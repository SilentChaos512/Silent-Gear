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

package net.silentchaos512.gear.api.parts;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class PartDataList implements List<ItemPartData> {
    private List<ItemPartData> list = new ArrayList<>();

    private PartDataList() {
    }

    public static PartDataList of(Collection<ItemPartData> c) {
        PartDataList ret = new PartDataList();
        ret.list.addAll(c);
        return ret;
    }

    public static PartDataList of(ItemPartData... parts) {
        PartDataList ret = new PartDataList();
        Collections.addAll(ret.list, parts);
        return ret;
    }

    public static PartDataList from(Collection<ItemStack> stacks) {
        PartDataList ret = new PartDataList();
        // Get part data for each stack, if it is a part. Silently ignore non-parts.
        stacks.stream().map(ItemPartData::fromStack).filter(Objects::nonNull).forEach(data -> ret.list.add(data));
        return ret;
    }

    public PartDataList getUniqueParts(boolean mainsOnly) {
        PartDataList result = PartDataList.of();
        for (ItemPartData data : (mainsOnly ? getMains() : this.list)) {
            if (result.stream().map(ItemPartData::getPart).noneMatch(part -> part == data.part)) {
                result.add(data);
            }
        }
        return result;
    }

    @Nullable
    public ItemPartData getPrimaryMain() {
        for (ItemPartData data : this.list)
            if (data.part instanceof PartMain)
                return data;
        return null;
    }

    public List<ItemPartData> getMains() {
        return getParts(ItemPartData::isMain);
    }

    public List<ItemPartData> getRods() {
        return getParts(ItemPartData::isRod);
    }

    public List<ItemPartData> getTips() {
        return getParts(ItemPartData::isTip);
    }

    public List<ItemPartData> getParts(Predicate<ItemPartData> predicate) {
        ImmutableList.Builder<ItemPartData> builder = ImmutableList.builder();
        this.list.stream().filter(predicate).forEach(builder::add);
        return builder.build();
    }

    /**
     * Convenience method which wraps the part in {@link ItemPartData} for you. Useful for ungraded
     * parts and parts without a unique crafting stack.
     */
    public boolean addPart(ItemPart part) {
        return this.list.add(ItemPartData.instance(part));
    }

    @Override
    public boolean add(ItemPartData arg0) {
        return this.list.add(arg0);
    }

    @Override
    public void add(int arg0, ItemPartData arg1) {
        this.list.add(arg0, arg1);
    }

    @Override
    public boolean addAll(Collection<? extends ItemPartData> arg0) {
        return this.list.addAll(arg0);
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends ItemPartData> arg1) {
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
    public ItemPartData get(int arg0) {
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
    public Iterator<ItemPartData> iterator() {
        return this.list.iterator();
    }

    @Override
    public int lastIndexOf(Object arg0) {
        return this.list.lastIndexOf(arg0);
    }

    @Override
    public ListIterator<ItemPartData> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<ItemPartData> listIterator(int arg0) {
        return this.list.listIterator(arg0);
    }

    @Override
    public boolean remove(Object arg0) {
        return this.list.remove(arg0);
    }

    @Override
    public ItemPartData remove(int arg0) {
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
    public ItemPartData set(int arg0, ItemPartData arg1) {
        return this.list.set(arg0, arg1);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public List<ItemPartData> subList(int arg0, int arg1) {
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
}
