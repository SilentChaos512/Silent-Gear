package net.silentchaos512.gear.crafting.recipe.smithing;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * @noinspection OptionalUsedAsFieldOrParameterType
 */
public abstract class GearSmithingRecipe implements SmithingRecipe {
    protected final Optional<Ingredient> template;
    protected final Optional<Ingredient> addition;
    protected final Ingredient base;
    @Nullable
    private PlacementInfo placementInfo;

    public GearSmithingRecipe(Ingredient gearItem, Optional<Ingredient> template, Optional<Ingredient> addition) {
        this.template = template;
        this.addition = addition;
        this.base = gearItem;
    }

    @Override
    public abstract RecipeSerializer<? extends GearSmithingRecipe> getSerializer();

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        boolean isGearItem = input.base().getItem() instanceof GearItem;
        return isGearItem && SmithingRecipe.super.matches(input, level);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input) {
        ItemStack result = input.base().copy();
        result.setCount(1); // Ensure arrows or other stackable items are not duplicated
        ItemStack upgradeItem = input.addition();
        applyUpgrade(result, upgradeItem);
        return result;
    }

    protected abstract void applyUpgrade(ItemStack stackToModify, ItemStack upgradeItem);

    @Override
    public Optional<Ingredient> templateIngredient() {
        return this.template;
    }

    @Override
    public Ingredient baseIngredient() {
        return this.base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return this.addition;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.template, Optional.of(this.base), this.addition));
        }
        return this.placementInfo;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @FunctionalInterface
    public interface Factory<R extends GearSmithingRecipe> {
        R create(Ingredient gearItem, Optional<Ingredient> template, Optional<Ingredient> addition);
    }

    public static <R extends GearSmithingRecipe> RecipeSerializer<R> createSerializer(Factory<R> factory) {
        return new RecipeSerializer<R>(
                RecordCodecBuilder.mapCodec(
                        i -> i.group(
                                Ingredient.CODEC.fieldOf("gear").forGetter(r -> r.base),
                                Ingredient.CODEC.optionalFieldOf("template").forGetter(r -> r.template),
                                Ingredient.CODEC.optionalFieldOf("addition").forGetter(r -> r.addition)
                        ).apply(i, factory::create)
                ),
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
                        Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, r -> r.template,
                        Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, r -> r.addition,
                        factory::create
                )
        );
    }
}
