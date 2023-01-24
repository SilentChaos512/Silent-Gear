package net.silentchaos512.gear.gear.material;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.client.material.DefaultMaterialDisplay;
import net.silentchaos512.gear.gear.material.modifier.GradeMaterialModifier;
import net.silentchaos512.utils.EnumUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public class LazyMaterialInstance implements IMaterialInstance {
    private final ResourceLocation materialId;
    private final Collection<IMaterialModifier> modifiers;

    public LazyMaterialInstance(ResourceLocation materialId) {
        this(materialId, MaterialGrade.NONE);
    }

    public LazyMaterialInstance(ResourceLocation materialId, MaterialGrade grade) {
        this(materialId, Collections.singleton(new GradeMaterialModifier(grade)));
    }

    public LazyMaterialInstance(ResourceLocation materialId, Collection<IMaterialModifier> modifiers) {
        this.materialId = materialId;
        this.modifiers = ImmutableList.copyOf(modifiers);
    }

    public static LazyMaterialInstance of(DataResource<IMaterial> material) {
        return new LazyMaterialInstance(material.getId());
    }

    public static LazyMaterialInstance of(ResourceLocation materialId) {
        return new LazyMaterialInstance(materialId);
    }

    public static LazyMaterialInstance of(DataResource<IMaterial> material, MaterialGrade grade) {
        return new LazyMaterialInstance(material.getId(), grade);
    }

    public static LazyMaterialInstance of(ResourceLocation materialId, MaterialGrade grade) {
        return new LazyMaterialInstance(materialId, grade);
    }

    @Override
    public ResourceLocation getId() {
        return materialId;
    }

    @Nullable
    @Override
    public IMaterial get() {
        return MaterialManager.get(materialId);
    }

    @Override
    public MaterialGrade getGrade() {
        for (IMaterialModifier mod : this.modifiers) {
            if (mod instanceof GradeMaterialModifier) {
                return ((GradeMaterialModifier) mod).getGrade();
            }
        }
        return MaterialGrade.NONE;
    }

    @Override
    public ItemStack getItem() {
        IMaterial material = get();
        return material != null ? MaterialInstance.of(material).getItem() : ItemStack.EMPTY;
    }

    @Override
    public int getTier(PartType partType) {
        IMaterial material = get();
        return material != null ? material.getTier(this, partType) : 0;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        nbt.putString("ID", materialId.toString());
        if (!modifiers.isEmpty()) {
            ListTag list = new ListTag();
            for (IMaterialModifier mod : modifiers) {
                CompoundTag compoundTag = MaterialModifiers.writeNbt(mod);
                if (compoundTag != null) {
                    list.add(compoundTag);
                }
            }

            if (!list.isEmpty()) {
                nbt.put("Modifiers", list);
            }
        }
        return nbt;
    }

    @Override
    public Component getDisplayName(PartType partType, ItemStack gear) {
        IMaterial material = get();
        return material != null ? material.getDisplayName(this, partType, gear) : Component.literal("INVALID");
    }

    @Override
    public String getModelKey() {
        return "null";
    }

    @Override
    public IMaterialDisplay getDisplayProperties() {
        IMaterial mat = get();
        return mat != null ? mat.getDisplayProperties(this) : DefaultMaterialDisplay.INSTANCE;
    }

    public static LazyMaterialInstance deserialize(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return LazyMaterialInstance.of(new ResourceLocation(json.getAsString()));
        }
        return deserialize(json.getAsJsonObject());
    }

    public static LazyMaterialInstance deserialize(JsonObject json) {
        ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "material"));
        MaterialGrade grade = EnumUtils.byName(GsonHelper.getAsString(json, "grade", "NONE"), MaterialGrade.NONE);
        Collection<IMaterialModifier> modifiers;
        if (json.has("modifiers")) {
            JsonArray array = json.getAsJsonArray("modifiers");
            modifiers = MaterialModifiers.readFromJson(array);
        } else {
            modifiers = Collections.singleton(new GradeMaterialModifier(grade));
        }
        return new LazyMaterialInstance(id, modifiers);
    }

    public static LazyMaterialInstance read(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        int modCount = buf.readVarInt();
        Collection<IMaterialModifier> mods = Lists.newArrayList();
        for (int i = 0; i < modCount; ++i) {
            mods.add(MaterialModifiers.readFromNetwork(buf));
        }
        return new LazyMaterialInstance(id, mods);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.materialId);
        buf.writeVarInt(this.modifiers.size());
        this.modifiers.forEach(mod -> MaterialModifiers.writeToNetwork(mod, buf));
    }
}
