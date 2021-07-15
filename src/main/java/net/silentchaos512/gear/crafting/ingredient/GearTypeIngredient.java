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
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.Registration;

import javax.annotation.Nullable;

import net.minecraft.item.crafting.Ingredient.SingleItemList;

public final class GearTypeIngredient extends Ingredient {
    private final GearType type;

    private GearTypeIngredient(GearType type) {
        super(Registration.ITEMS.getEntries().stream()
                .filter(iro -> iro.isPresent() && iro.get() instanceof ICoreItem)
                .map(iro -> (ICoreItem) iro.get())
                .filter(item -> item.getGearType().matches(type))
                .map(item -> new SingleItemList(new ItemStack(item))));
        this.type = type;
    }

    public static GearTypeIngredient of(GearType type) {
        return new GearTypeIngredient(type);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        return stack.getItem() instanceof ICoreItem && ((ICoreItem) stack.getItem()).getGearType().matches(this.type);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", Serializer.NAME.toString());
        json.addProperty("gear_type", this.type.getName());
        return json;
    }

    public static final class Serializer implements IIngredientSerializer<GearTypeIngredient> {
        public static final GearTypeIngredient.Serializer INSTANCE = new GearTypeIngredient.Serializer();
        public static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "gear_type");

        private Serializer() {}

        @Override
        public GearTypeIngredient parse(PacketBuffer buffer) {
            String typeName = buffer.readUtf();
            GearType type = GearType.get(typeName);
            if (type.isInvalid()) throw new JsonParseException("Unknown gear type: " + typeName);
            return new GearTypeIngredient(type);
        }

        @Override
        public GearTypeIngredient parse(JsonObject json) {
            String typeName = JSONUtils.getAsString(json, "gear_type", "");
            if (typeName.isEmpty())
                throw new JsonSyntaxException("'gear_type' is missing");

            GearType type = GearType.get(typeName);
            if (type.isInvalid())
                throw new JsonSyntaxException("gear_type " + typeName + " does not exist");

            return new GearTypeIngredient(type);
        }

        @Override
        public void write(PacketBuffer buffer, GearTypeIngredient ingredient) {
            buffer.writeUtf(ingredient.type.getName());
        }
    }
}
