package net.silentchaos512.gear.api.material;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import javax.annotation.Nullable;
import java.util.*;

public final class MaterialList implements List<IMaterialInstance> {
    private final List<IMaterialInstance> list = new ArrayList<>();

    private MaterialList() {}

    public static MaterialList empty() {
        return new MaterialList();
    }

    public static MaterialList of(Collection<? extends IMaterialInstance> materials) {
        MaterialList ret = new MaterialList();
        ret.list.addAll(materials);
        return ret;
    }

    public static MaterialList of(IMaterialInstance... materials) {
        MaterialList ret = new MaterialList();
        Collections.addAll(ret.list, materials);
        return ret;
    }

    public static MaterialList deserializeNbt(ListNBT listNbt) {
        MaterialList ret = new MaterialList();

        for (int i = 0; i < listNbt.size(); ++i) {
            CompoundNBT compoundNbt = listNbt.getCompound(i);
            MaterialInstance material = MaterialInstance.read(compoundNbt);

            if (material != null) {
                int count = compoundNbt.contains("Count") ? compoundNbt.getByte("Count") : 1;
                for (int j = 0; j < count; ++j) {
                    ret.list.add(material);
                }
            }
        }

        return ret;
    }

    public ListNBT serializeNbt() {
        ListNBT ret = new ListNBT();
        IMaterialInstance last = null;
        int count = 0;

        for (IMaterialInstance material : list) {
            if (material.equals(last)) {
                // Identical materials
                ++count;
            } else {
                // Different materials
                if (last != null) {
                    // Finished counting last material, so write it
                    ret.add(serializeMaterialWithCount(last, count));
                }
                // Start counting new material
                last = material;
                count = 1;
            }
        }

        // Write the final material(s)
        if (last != null) {
            ret.add(serializeMaterialWithCount(last, count));
        }

        return ret;
    }

    private static CompoundNBT serializeMaterialWithCount(IMaterialInstance material, int count) {
        CompoundNBT ret = new CompoundNBT();
        material.write(ret);
        if (count > 1) {
            ret.putByte("Count", (byte) count);
        }
        return ret;
    }

    /**
     * Deserialize only the first valid material from NBT
     *
     * @param listNbt The material list NBT
     * @return The first valid material, or null if there are none in the list
     */
    @Nullable
    public static IMaterialInstance deserializeFirst(ListNBT listNbt) {
        for (int i = 0; i < listNbt.size(); i++) {
            MaterialInstance material = MaterialInstance.read(listNbt.getCompound(i));
            if (material != null) {
                return material;
            }
        }
        return null;
    }

    public String getModelKey() {
        StringBuilder ret = new StringBuilder("[");
        IMaterialInstance last = null;
        int count = 0;
        int totalWritten = 0;

        for (IMaterialInstance material : list) {
            if (material.equals(last)) {
                // Identical materials
                ++count;
            } else {
                // Different materials
                if (last != null) {
                    // Finished counting last material, so add it to key
                    if (totalWritten > 0) {
                        ret.append(",");
                    }
                    ret.append(makeModelKeyWithCount(last, count));
                    ++totalWritten;
                }
                // Start counting new material
                last = material;
                count = 1;
            }
        }

        // Add the final material(s) to key
        if (last != null) {
            ret.append(makeModelKeyWithCount(last, count));
        }

        return ret.append("]").toString();
    }

    private static String makeModelKeyWithCount(IMaterialInstance material, int count) {
        if (count > 1) {
            return material.getModelKey() + "*" + count;
        }
        return material.getModelKey();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<IMaterialInstance> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return list.toArray(a);
    }

    @Override
    public boolean add(IMaterialInstance materialInstance) {
        return list.add(materialInstance);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends IMaterialInstance> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends IMaterialInstance> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public IMaterialInstance get(int index) {
        return list.get(index);
    }

    @Override
    public IMaterialInstance set(int index, IMaterialInstance element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, IMaterialInstance element) {
        list.add(index, element);
    }

    @Override
    public IMaterialInstance remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<IMaterialInstance> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<IMaterialInstance> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<IMaterialInstance> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
}
