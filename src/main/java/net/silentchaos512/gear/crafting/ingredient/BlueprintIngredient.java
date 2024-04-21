package net.silentchaos512.gear.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class BlueprintIngredient extends Ingredient implements IGearIngredient {
    private final PartType partType;
    private final GearType gearType;

    @Nullable
    private ItemStack[] itemStacks;

    private BlueprintIngredient(PartType partType, GearType gearType) {
        super(Stream.of());
        this.partType = partType;
        this.gearType = gearType;
    }

    public static <T extends Item & IBlueprint> BlueprintIngredient of(T item) {
        ItemStack stack = new ItemStack(item);
        return new BlueprintIngredient(item.getPartType(stack), item.getGearType(stack));
    }

    private void dissolve() {
        if (this.itemStacks == null) {
            this.itemStacks = BuiltInRegistries.ITEM.stream()
                    .filter(item -> item instanceof IBlueprint)
                    .map(ItemStack::new)
                    .filter(this::testBlueprint)
                    .toArray(ItemStack[]::new);
        }
    }

    private boolean testBlueprint(ItemStack stack) {
        if (stack.getItem() instanceof IBlueprint item) {
            return item.getGearType(stack) == this.gearType && item.getPartType(stack) == this.partType;
        }
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        this.dissolve();
        return this.testBlueprint(stack);
    }

    @Override
    public ItemStack[] getItems() {
        this.dissolve();
        //noinspection AssignmentOrReturnOfFieldWithMutableType,ConstantConditions
        return this.itemStacks;
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
    public Optional<Component> getJeiHint() {
        PartGearKey key = PartGearKey.of(this.gearType, this.partType);
        MutableComponent keyText = key.getDisplayName().copy();
        Component text = TextUtil.withColor(keyText, Color.DODGERBLUE);
        return Optional.of(TextUtil.translate("jei", "blueprintType", text));
    }

    public static final class Serializer implements IIngredientSerializer<BlueprintIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = SilentGear.getId("blueprint");

        private Serializer() {}

        @Override
        public BlueprintIngredient parse(FriendlyByteBuf buffer) {
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
            String typeName = GsonHelper.getAsString(json, "part_type", "main");
            PartType type = PartType.get(Objects.requireNonNull(SilentGear.getIdWithDefaultNamespace(typeName)));
            if (type == null) {
                throw new JsonSyntaxException("part_type " + typeName + " does not exist");
            }

            String gearTypeName = GsonHelper.getAsString(json, "gear_type", "part");
            GearType gearType = GearType.get(gearTypeName);
            if (gearType.isInvalid()) {
                throw new JsonSyntaxException("gear_type " + gearTypeName + " does not exist");
            }

            return new BlueprintIngredient(type, gearType);
        }

        @Override
        public void write(FriendlyByteBuf buffer, BlueprintIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.partType.getName());
            buffer.writeUtf(ingredient.gearType.getName());
        }
    }
}
