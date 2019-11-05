package net.silentchaos512.gear.parts;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IPartMaterial;
import net.silentchaos512.gear.crafting.ingredient.CustomTippedUpgradeIngredient;

/**
 * Represents the items that an {@link IGearPart} can be crafted from.
 */
public class PartMaterial implements IPartMaterial {
    private Ingredient normal = Ingredient.EMPTY;
    private Ingredient small = Ingredient.EMPTY;

    PartMaterial() {}

    @Override
    public boolean test(ItemStack itemStack) {
        return normal.test(itemStack);
    }

    @Override
    public Ingredient getNormal() {
        return normal;
    }

    @Override
    public Ingredient getSmall() {
        return small;
    }

    public static PartMaterial deserialize(ResourceLocation partId, JsonObject json) {
        PartMaterial material = new PartMaterial();

        if (json.has("custom_tipped_upgrade"))
            material.normal = CustomTippedUpgradeIngredient.of(partId);
        else if (json.has("normal"))
            material.normal = deserialize(partId, json.get("normal"));
        else if (json.has("uncraftable"))
            material.normal = Ingredient.EMPTY;
        else
            throw new JsonSyntaxException("Missing non-small crafting_items");

        if (json.has("small"))
            material.small = deserialize(partId, json.get("small"));

        return material;
    }

    public static PartMaterial read(PacketBuffer buffer) {
        PartMaterial material = new PartMaterial();
        material.normal = Ingredient.read(buffer);
        material.small = Ingredient.read(buffer);
        return material;
    }

    // Simple wrapper which will sidestep some of the exceptions thrown by vanilla, allowing
    // outdated part files to load anyway
    private static Ingredient deserialize(ResourceLocation partId, JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();

            // Remove item property, to allow older files to load
            if (obj.has("item") && obj.has("tag")) {
                SilentGear.LOGGER.warn("Part '{}' has a crafting item with both an 'item' and 'tag' property. Ignoring 'item'.", partId);
                obj.remove("item");
            }

            // Check for non-existent tag
            if (obj.has("tag")) {
                ResourceLocation tagId = new ResourceLocation(JSONUtils.getString(obj, "tag"));
                if (ItemTags.getCollection().get(tagId) == null) {
                    SilentGear.LOGGER.warn("Unknown item tag '{}' on part '{}'. Part will not be usable for crafting.", tagId, partId);
                    return Ingredient.EMPTY;
                }
            }

            return Ingredient.deserialize(obj);
        }
        // For arrays, we'll assume all is well
        return CraftingHelper.getIngredient(json);
    }
}
