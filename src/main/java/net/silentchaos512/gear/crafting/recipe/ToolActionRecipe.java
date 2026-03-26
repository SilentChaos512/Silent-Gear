package net.silentchaos512.gear.crafting.recipe;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.core.SoundPlayback;
import net.silentchaos512.gear.setup.SgRecipeBookCategories;
import net.silentchaos512.gear.setup.SgRecipes;

import javax.annotation.Nullable;
import java.util.List;

public class ToolActionRecipe implements Recipe<ToolActionRecipe.Input> {
    public static final RecipeSerializer<ToolActionRecipe> SERIALIZER = new RecipeSerializer<>(
            RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Ingredient.CODEC.fieldOf("tool").forGetter(r -> r.tool),
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("damage_to_tool").forGetter(r -> r.damageToTool),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
                            SoundPlayback.CODEC.fieldOf("sound").forGetter(r -> r.sound)
                    ).apply(instance, ToolActionRecipe::new)
            ),
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, r -> r.tool,
                    Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
                    ByteBufCodecs.VAR_INT, r -> r.damageToTool,
                    ItemStackTemplate.STREAM_CODEC, r -> r.result,
                    SoundPlayback.STREAM_CODEC, r -> r.sound,
                    ToolActionRecipe::new
            )
    );

    private final Ingredient tool;
    private final Ingredient ingredient;
    private final int damageToTool;
    private final ItemStackTemplate result;
    private final SoundPlayback sound;
    @Nullable private PlacementInfo placementInfo;

    public ToolActionRecipe(Ingredient tool, Ingredient ingredient, int damageToTool, ItemStackTemplate result, SoundPlayback sound) {
        this.tool = tool;
        this.ingredient = ingredient;
        this.damageToTool = damageToTool;
        this.result = result;
        this.sound = sound;
    }

    public Ingredient getTool() {
        return tool;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getDamageToTool() {
        return damageToTool;
    }

    public ItemStackTemplate getResult() {
        return result;
    }

    public SoundPlayback getSound() {
        return sound;
    }

    @Override
    public boolean matches(ToolActionRecipe.Input input, Level pLevel) {
        return this.tool.test(input.tool()) && this.ingredient.test(input.ingredient());
    }

    @Override
    public ItemStack assemble(ToolActionRecipe.Input input) {
        return result.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends ToolActionRecipe> getSerializer() {
        return SgRecipes.TOOL_ACTION.get();
    }

    @Override
    public RecipeType<? extends ToolActionRecipe> getType() {
        return SgRecipes.TOOL_ACTION_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(List.of(this.tool, this.ingredient));
        }
        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return SgRecipeBookCategories.TOOL_ACTION.get();
    }

    public record Input(ItemStack tool, ItemStack ingredient) implements RecipeInput {
        @Override
        public ItemStack getItem(int pIndex) {
            return switch (pIndex) {
                case 0 -> tool;
                case 1 -> ingredient;
                default -> throw new IllegalArgumentException("Index out of bounds: " + pIndex);
            };
        }

        @Override
        public int size() {
            return 2;
        }
    }
}
