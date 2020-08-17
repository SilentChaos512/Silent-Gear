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
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public final class PartMaterialIngredient extends Ingredient implements IPartIngredient {
    private final PartType partType;
    private final GearType gearType;

    private PartMaterialIngredient(PartType partType, GearType gearType) {
        super(Stream.of());
        this.partType = partType;
        this.gearType = gearType;
    }

    public static PartMaterialIngredient of(PartType partType) {
        return of(partType, GearType.TOOL);
    }

    public static PartMaterialIngredient of(PartType partType, GearType gearType) {
        return new PartMaterialIngredient(partType, gearType);
    }

    @Override
    public PartType getPartType() {
        return partType;
    }

    public GearType getGearType() {
        return gearType;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        IMaterial material = MaterialManager.from(stack);
        return material != null && material.isCraftingAllowed(partType, gearType);
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        Collection<IMaterial> materials = MaterialManager.getValues();
        if (!materials.isEmpty()) {
            return materials.stream()
                    .filter(mat -> mat.isCraftingAllowed(this.partType, gearType))
                    .flatMap(mat -> Stream.of(mat.getIngredient().getMatchingStacks()))
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
        json.addProperty("part_type", this.partType.getName().toString());
        if (this.gearType != GearType.TOOL) {
            json.addProperty("gear_type", this.gearType.getName());
        }
        return json;
    }

    public static final class Serializer implements IIngredientSerializer<PartMaterialIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = SilentGear.getId("material");

        private Serializer() {}

        @Override
        public PartMaterialIngredient parse(PacketBuffer buffer) {
            ResourceLocation typeName = buffer.readResourceLocation();
            PartType partType = PartType.get(typeName);
            if (partType == null) {
                throw new JsonParseException("Unknown part type: " + typeName);
            }

            GearType gearType = GearType.get(buffer.readString());
            if (gearType.isInvalid()) {
                throw new JsonParseException("Unknown gear type: " + typeName);
            }

            return new PartMaterialIngredient(partType, gearType);
        }

        @Override
        public PartMaterialIngredient parse(JsonObject json) {
            String typeName = JSONUtils.getString(json, "part_type", "");
            if (typeName.isEmpty()) {
                throw new JsonSyntaxException("'part_type' is missing");
            }

            PartType type = PartType.get(Objects.requireNonNull(SilentGear.getIdWithDefaultNamespace(typeName)));
            if (type == null) {
                throw new JsonSyntaxException("part_type " + typeName + " does not exist");
            }

            String gearTypeName = JSONUtils.getString(json, "gear_type", "tool");
            GearType gearType = GearType.get(gearTypeName);
            if (gearType.isInvalid()) {
                throw new JsonSyntaxException("gear_type " + gearTypeName + " does not exist");
            }

            return new PartMaterialIngredient(type, gearType);
        }

        @Override
        public void write(PacketBuffer buffer, PartMaterialIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.partType.getName());
            buffer.writeString(ingredient.gearType.getName());
        }
    }
}
