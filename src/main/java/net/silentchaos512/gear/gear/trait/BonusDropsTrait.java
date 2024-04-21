package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;

import java.util.Collection;

public final class BonusDropsTrait extends SimpleTrait {
    @SuppressWarnings("OverlyLongLambda")
    public static final ITraitSerializer<BonusDropsTrait> SERIALIZER = new Serializer<>(
            ApiConst.BONUS_DROPS_TRAIT_ID,
            BonusDropsTrait::new,
            (trait, json) -> {
                trait.baseChance = GsonHelper.getAsFloat(json, "base_chance", 0f);
                trait.bonusMultiplier = GsonHelper.getAsFloat(json, "bonus_multiplier", 1f);
                trait.readIngredient(json.get("ingredient"));
            },
            (trait, buffer) -> {
                trait.baseChance = buffer.readFloat();
                trait.bonusMultiplier = buffer.readFloat();
                trait.ingredient = Ingredient.fromNetwork(buffer);
                trait.matchedItemsText = buffer.readUtf();
            },
            (trait, buffer) -> {
                buffer.writeFloat(trait.baseChance);
                buffer.writeFloat(trait.bonusMultiplier);
                trait.ingredient.toNetwork(buffer);
                buffer.writeUtf(trait.matchedItemsText);
            }
    );

    private float baseChance;
    private float bonusMultiplier;
    private Ingredient ingredient = Ingredient.EMPTY;
    private String matchedItemsText = "some items";

    private BonusDropsTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    private void readIngredient(JsonElement json) {
        this.ingredient = Ingredient.fromJson(json);

        // For wiki output
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("tag")) {
                matchedItemsText = "`" + obj.get("tag").getAsString() + "` (tag)";
            } else if (obj.has("item")) {
                matchedItemsText = "`" + obj.get("item").getAsString() + "`";
            }
        }
    }

    @Override
    public ItemStack addLootDrops(TraitActionContext context, ItemStack stack) {
        if (ingredient.test(stack) && SilentGear.RANDOM.nextFloat() < this.baseChance * context.traitLevel()) {
            ItemStack copy = stack.copy();
            copy.setCount(Math.round(stack.getCount() * this.bonusMultiplier));
            return copy;
        }
        return super.addLootDrops(context, stack);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();
        ret.add(String.format("  - %d%% chance per level of dropping %d%% more of %s",
                (int) (100 * this.baseChance),
                (int) (100 * this.bonusMultiplier),
                matchedItemsText));
        return ret;
    }
}
