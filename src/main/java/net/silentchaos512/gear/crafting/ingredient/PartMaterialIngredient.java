package net.silentchaos512.gear.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Stream;

public final class PartMaterialIngredient extends Ingredient {
    private final PartType type;

    private PartMaterialIngredient(PartType type) {
        super(Stream.of());
        this.type = type;
    }

    public static PartMaterialIngredient of(PartType type) {
        return new PartMaterialIngredient(type);
    }

    public PartType getPartType() {
        return type;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        IMaterial material = MaterialManager.from(stack);
        return material != null && material.isCraftingAllowed(type);
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        Collection<IMaterial> materials = MaterialManager.getValues();
        if (!materials.isEmpty()) {
            return materials.stream()
                    .filter(mat -> mat.isCraftingAllowed(this.type))
                    .flatMap(mat -> Stream.of(mat.getIngredient(this.type).getMatchingStacks()))
                    .filter(stack -> !stack.isEmpty())
                    .toArray(ItemStack[]::new);
        }
        return super.getMatchingStacks();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean hasNoMatchingItems() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", Serializer.NAME.toString());
        json.addProperty("part_type", this.type.getName().toString());
        return json;
    }

    public static final class Serializer implements IIngredientSerializer<PartMaterialIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = SilentGear.getId("material");

        private Serializer() {}

        @Override
        public PartMaterialIngredient parse(PacketBuffer buffer) {
            ResourceLocation typeName = buffer.readResourceLocation();
            PartType type = PartType.get(typeName);
            if (type == null) throw new JsonParseException("Unknown part type: " + typeName);
            return new PartMaterialIngredient(type);
        }

        @Override
        public PartMaterialIngredient parse(JsonObject json) {
            String typeName = JSONUtils.getString(json, "part_type", "");
            if (typeName.isEmpty())
                throw new JsonSyntaxException("'part_type' is missing");

            ResourceLocation id = typeName.contains(":")
                    ? new ResourceLocation(typeName)
                    : SilentGear.getId(typeName);
            PartType type = PartType.get(id);
            if (type == null)
                throw new JsonSyntaxException("part_type " + typeName + " does not exist");

            return new PartMaterialIngredient(type);
        }

        @Override
        public void write(PacketBuffer buffer, PartMaterialIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.type.getName());
        }
    }
}
