package net.silentchaos512.gear.parts;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
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
            material.normal = Ingredient.deserialize(json.get("normal"));
        else
            throw new JsonSyntaxException("Missing non-small crafting_items");

        if (json.has("small"))
            material.small = Ingredient.deserialize(json.get("small"));

        return material;
    }

    public static PartMaterial read(PacketBuffer buffer) {
        PartMaterial material = new PartMaterial();
        material.normal = Ingredient.read(buffer);
        material.small = Ingredient.read(buffer);
        return material;
    }

    public void write(PacketBuffer buffer) {
        normal.write(buffer);
        small.write(buffer);
    }
}
