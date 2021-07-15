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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class BlueprintIngredient extends Ingredient implements IGearIngredient {
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
    public ItemStack[] getItems() {
        return ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof IBlueprint)
                .map(ItemStack::new)
                .filter(this)
                .toArray(ItemStack[]::new);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isEmpty() {
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
        if (this.partType != PartType.MAIN) {
            json.addProperty("part_type", this.partType.getName().toString());
        }
        if (this.gearType != GearType.PART) {
            json.addProperty("gear_type", this.gearType.getName());
        }
        return json;
    }

    @Override
    public PartType getPartType() {
        return this.partType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType;
    }

    @Override
    public Optional<ITextComponent> getJeiHint() {
        PartGearKey key = PartGearKey.of(this.gearType, this.partType);
        IFormattableTextComponent keyText = new StringTextComponent(key.toString());
        ITextComponent text = TextUtil.withColor(keyText, Color.DODGERBLUE);
        return Optional.of(TextUtil.translate("jei", "blueprintType", text));
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

            GearType gearType = GearType.get(buffer.readUtf());
            if (gearType.isInvalid()) {
                throw new JsonParseException("Unknown gear type: " + typeName);
            }

            return new BlueprintIngredient(partType, gearType);
        }

        @Override
        public BlueprintIngredient parse(JsonObject json) {
            String typeName = JSONUtils.getAsString(json, "part_type", "main");
            PartType type = PartType.get(Objects.requireNonNull(SilentGear.getIdWithDefaultNamespace(typeName)));
            if (type == null) {
                throw new JsonSyntaxException("part_type " + typeName + " does not exist");
            }

            String gearTypeName = JSONUtils.getAsString(json, "gear_type", "part");
            GearType gearType = GearType.get(gearTypeName);
            if (gearType.isInvalid()) {
                throw new JsonSyntaxException("gear_type " + gearTypeName + " does not exist");
            }

            return new BlueprintIngredient(type, gearType);
        }

        @Override
        public void write(PacketBuffer buffer, BlueprintIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.partType.getName());
            buffer.writeUtf(ingredient.gearType.getName());
        }
    }
}
