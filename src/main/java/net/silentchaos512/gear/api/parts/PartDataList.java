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
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.parts.PartData;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public final class PartDataList implements List<PartData> {
    private final List<PartData> list = new ArrayList<>();

    private PartDataList() {
    }

    public static PartDataList empty() {
        return new PartDataList();
    }

    public static PartDataList of(Collection<PartData> c) {
        PartDataList ret = new PartDataList();
        ret.list.addAll(c);
        return ret;
    }

    public static PartDataList of(PartData... parts) {
        PartDataList ret = new PartDataList();
        Collections.addAll(ret.list, parts);
        return ret;
    }

    public static PartDataList from(Collection<ItemStack> stacks) {
        PartDataList ret = new PartDataList();
        // Get part data for each stack, if it is a part. Silently ignore non-parts.
        stacks.stream().map(PartData::from).filter(Objects::nonNull).forEach(ret.list::add);
        return ret;
    }

    public PartDataList getUniqueParts(boolean mainsOnly) {
        PartDataList result = PartDataList.of();
        for (PartData data : (mainsOnly ? getMains() : this.list)) {
            if (result.stream().map(PartData::getPart).noneMatch(part -> part == data.getPart())) {
                result.add(data);
            }
        }
        return result;
    }

    @Nullable
    public PartData firstInPosition(IPartPosition position) {
        for (PartData part : this.list)
            if (part.getPart().getPartPosition() == position)
                return part;
        return null;
    }

    @Nullable
    public PartData getPrimaryMain() {
        for (PartData data : this.list)
            if (data.getType() == PartType.MAIN)
                return data;
        return null;
    }

    public List<PartData> getMains() {
        return getPartsOfType(PartType.MAIN);
    }

    public List<PartData> getRods() {
        return getPartsOfType(PartType.ROD);
    }

    public List<PartData> getTips() {
        return getPartsOfType(PartType.TIP);
    }

    public List<PartData> getPartsOfType(PartType type) {
        return getParts(part -> part.getType() == type);
    }

    public List<PartData> getParts(Predicate<PartData> predicate) {
        ImmutableList.Builder<PartData> builder = ImmutableList.builder();
        this.list.stream().filter(predicate).forEach(builder::add);
        return builder.build();
    }

    /**
     * Convenience method which wraps the part in {@link PartData} for you. Useful for ungraded
     * parts and parts without a unique crafting stack.
     *
     * @param part The gear part
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean addPart(IGearPart part) {
        return this.list.add(PartData.of(part));
    }

    public int getPartsWithTrait(ITrait trait) {
        return (int) this.stream()
                .filter(part -> part.getTraits().stream().anyMatch(inst -> inst.getTrait() == trait))
                .count();
    }

    //region List overrides

    @Override
    public boolean add(PartData arg0) {
        return this.list.add(arg0);
    }

    @Override
    public void add(int arg0, PartData arg1) {
        this.list.add(arg0, arg1);
    }

    @Override
    public boolean addAll(Collection<? extends PartData> arg0) {
        return this.list.addAll(arg0);
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends PartData> arg1) {
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
    public PartData get(int arg0) {
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
    public Iterator<PartData> iterator() {
        return this.list.iterator();
    }

    @Override
    public int lastIndexOf(Object arg0) {
        return this.list.lastIndexOf(arg0);
    }

    @Override
    public ListIterator<PartData> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<PartData> listIterator(int arg0) {
        return this.list.listIterator(arg0);
    }

    @Override
    public boolean remove(Object arg0) {
        return this.list.remove(arg0);
    }

    @Override
    public PartData remove(int arg0) {
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
    public PartData set(int arg0, PartData arg1) {
        return this.list.set(arg0, arg1);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public List<PartData> subList(int arg0, int arg1) {
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

    //endregion
}
