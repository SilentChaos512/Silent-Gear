package net.silentchaos512.gear.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.stream.Stream;

import net.minecraft.world.item.crafting.Ingredient.ItemValue;

public class CustomCompoundIngredient extends Ingredient {
    private final CustomMaterialItem item;
    private final ResourceLocation material;

    protected CustomCompoundIngredient(CustomMaterialItem item, ResourceLocation materialId) {
        super(Stream.of(new ItemValue(item.create(LazyMaterialInstance.of(materialId)))));
        this.item = item;
        this.material = materialId;
    }

    public static CustomCompoundIngredient of(CustomMaterialItem item, IMaterial material) {
        return of(item, material.getId());
    }

    public static CustomCompoundIngredient of(CustomMaterialItem item, ResourceLocation materialId) {
        return new CustomCompoundIngredient(item, materialId);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.getItem().equals(this.item)) return false;

        MaterialInstance material = CustomMaterialItem.getMaterial(stack);
        return material != null && material.getId().equals(this.material);
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", Serializer.NAME.toString());
        json.addProperty("item", NameUtils.from(this.item).toString());
        json.addProperty("material", this.material.toString());
        return json;
    }

    public static final class Serializer implements IIngredientSerializer<CustomCompoundIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = SilentGear.getId("custom_compound");

        @Override
        public CustomCompoundIngredient parse(JsonObject json) {
            ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(json, "item"));
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) {
                throw new JsonParseException("Unknown item: " + itemId);
            } else if (!(item instanceof CustomMaterialItem)) {
                throw new JsonParseException("Item '" + itemId + "' is not a CustomMaterialItem");
            }

            ResourceLocation materialId = new ResourceLocation(GsonHelper.getAsString(json, "material"));

            return new CustomCompoundIngredient((CustomMaterialItem) item, materialId);
        }

        @Override
        public CustomCompoundIngredient parse(FriendlyByteBuf buffer) {
            ResourceLocation itemId = buffer.readResourceLocation();
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) {
                throw new JsonParseException("Unknown item: " + itemId);
            } else if (!(item instanceof CustomMaterialItem)) {
                throw new JsonParseException("Item '" + itemId + "' is not a CustomMaterialItem");
            }

            ResourceLocation materialId = buffer.readResourceLocation();

            return new CustomCompoundIngredient((CustomMaterialItem) item, materialId);
        }

        @Override
        public void write(FriendlyByteBuf buffer, CustomCompoundIngredient ingredient) {
            buffer.writeResourceLocation(NameUtils.from(ingredient.item));
            buffer.writeResourceLocation(ingredient.material);
        }
    }
}
