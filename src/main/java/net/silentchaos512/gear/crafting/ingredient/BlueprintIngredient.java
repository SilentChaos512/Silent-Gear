package net.silentchaos512.gear.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.blueprint.IBlueprint;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

public class BlueprintIngredient extends Ingredient {
    private final PartType partType;
    private final GearType gearType;

    private BlueprintIngredient(PartType partType, GearType gearType) {
        super(Stream.of());
        this.partType = partType;
        this.gearType = gearType;
    }

    public static <T extends Item & IBlueprint> BlueprintIngredient of(T item) {
        ItemStack stack = new ItemStack(item);
        return new BlueprintIngredient(item.getPartType(stack), item.getGearType(stack));
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        if (stack.getItem() instanceof IBlueprint) {
            IBlueprint item = (IBlueprint) stack.getItem();
            return item.getGearType(stack) == this.gearType && item.getPartType(stack) == this.partType;
        }

        return false;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return Registration.getItems(IBlueprint.class).stream()
                .map(item -> new ItemStack((Item) item))
                .filter(this)
                .toArray(ItemStack[]::new);
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
        if (this.partType != PartType.MAIN) {
            json.addProperty("part_type", this.partType.getName().toString());
        }
        if (this.gearType != GearType.PART) {
            json.addProperty("gear_type", this.gearType.getName());
        }
        return json;
    }

    public static final class Serializer implements IIngredientSerializer<BlueprintIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = SilentGear.getId("blueprint");

        private Serializer() {}

        @Override
        public BlueprintIngredient parse(PacketBuffer buffer) {
            ResourceLocation typeName = buffer.readResourceLocation();
            PartType partType = PartType.get(typeName);
            if (partType == null) {
                throw new JsonParseException("Unknown part type: " + typeName);
            }

            GearType gearType = GearType.get(buffer.readString());
            if (gearType == null) {
                throw new JsonParseException("Unknown gear type: " + typeName);
            }

            return new BlueprintIngredient(partType, gearType);
        }

        @Override
        public BlueprintIngredient parse(JsonObject json) {
            String typeName = JSONUtils.getString(json, "part_type", "main");
            PartType type = PartType.get(Objects.requireNonNull(SilentGear.getIdWithDefaultNamespace(typeName)));
            if (type == null) {
                throw new JsonSyntaxException("part_type " + typeName + " does not exist");
            }

            String gearTypeName = JSONUtils.getString(json, "gear_type", "part");
            GearType gearType = GearType.get(gearTypeName);
            if (gearType == null) {
                throw new JsonSyntaxException("gear_type " + gearTypeName + " does not exist");
            }

            return new BlueprintIngredient(type, gearType);
        }

        @Override
        public void write(PacketBuffer buffer, BlueprintIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.partType.getName());
            buffer.writeString(ingredient.gearType.getName());
        }
    }
}
