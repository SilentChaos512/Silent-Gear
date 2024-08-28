package net.silentchaos512.gear.crafting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.setup.SgRecipes;

public class ToolActionRecipe implements Recipe<ToolActionRecipe.Input> {
    private final Ingredient tool;
    private final Ingredient ingredient;
    private final int damageToTool;
    private final ItemStack result;

    public ToolActionRecipe(Ingredient tool, Ingredient ingredient, int damageToTool, ItemStack result) {
        this.tool = tool;
        this.ingredient = ingredient;
        this.damageToTool = damageToTool;
        this.result = result;
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

    public ItemStack getResult() {
        return result.copy();
    }

    @Override
    public boolean matches(ToolActionRecipe.Input input, Level pLevel) {
        return this.tool.test(input.tool()) && this.ingredient.test(input.ingredient());
    }

    @Override
    public ItemStack assemble(ToolActionRecipe.Input pContainer, HolderLookup.Provider pRegistryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.TOOL_ACTION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SgRecipes.TOOL_ACTION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ToolActionRecipe> {
        public static final MapCodec<ToolActionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("tool").forGetter(r -> r.tool),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("damage_to_tool").forGetter(r -> r.damageToTool),
                        ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result)
                ).apply(instance, ToolActionRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ToolActionRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<ToolActionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ToolActionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static ToolActionRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var tool = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            var ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            var damageToTool = buf.readVarInt();
            var result = ItemStack.STREAM_CODEC.decode(buf);
            return new ToolActionRecipe(tool, ingredient, damageToTool, result);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, ToolActionRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.tool);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
            buf.writeVarInt(recipe.damageToTool);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }
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
