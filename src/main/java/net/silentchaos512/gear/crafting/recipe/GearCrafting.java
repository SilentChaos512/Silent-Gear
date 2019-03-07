package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.lib.collection.StackList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class GearCrafting implements IRecipe {
    protected final IRecipe recipe;
    private final ICoreItem item;

    private GearCrafting(IRecipe recipeTemplate, ICoreItem item) {
        this.recipe = recipeTemplate;
        this.item = item;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return recipe.matches(inv, worldIn);
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        Collection<PartData> parts = StackList.from(inv).stream()
                .map(PartData::from)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return item.construct(parts);
    }

    @Override
    public boolean canFit(int width, int height) {
        return recipe.canFit(width, height);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(item);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return recipe.getId();
    }

    /**
     * Shapeless (standard) gear crafting. Used for most recipes.
     */
    public static final class Shapeless extends GearCrafting {
        private Shapeless(IRecipe recipeTemplate, ICoreItem item) {
            super(recipeTemplate, item);
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return Serializer.INSTANCE;
        }
    }

    /**
     * Shaped gear crafting. Used by rough tool recipes, but could be used for standard gear.
     */
    public static final class Shaped extends GearCrafting {
        private Shaped(IRecipe recipeTemplate, ICoreItem item) {
            super(recipeTemplate, item);
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return ShapedSerializer.INSTANCE;
        }
    }

    /**
     * Shapeless gear crafting serializer
     */
    public static final class Serializer implements IRecipeSerializer<GearCrafting.Shapeless> {
        public static final Serializer INSTANCE = new Serializer();
        static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "gear_crafting");

        Serializer() {}

        @Override
        public GearCrafting.Shapeless read(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe recipe = RecipeSerializers.CRAFTING_SHAPELESS.read(recipeId, json);
            Item item = recipe.getRecipeOutput().getItem();
            if (!(item instanceof ICoreItem))
                throw new JsonParseException("result must a gear item");
            return new GearCrafting.Shapeless(recipe, (ICoreItem) item);
        }

        @Override
        public GearCrafting.Shapeless read(ResourceLocation recipeId, PacketBuffer buffer) {
            // TODO: Waiting for fix on Forge issue #5577, remove try-catch afterwards
//            try {
//                ShapelessRecipe recipe = RecipeSerializers.CRAFTING_SHAPELESS.read(recipeId, buffer);
//                Item item = recipe.getRecipeOutput().getItem();
//                if (!(item instanceof ICoreItem))
//                    throw new IllegalStateException("result must a gear item");
//                return new GearCrafting.Shapeless(recipe, (ICoreItem) item);
//            } catch (DecoderException ex) {
//                SilentGear.LOGGER.warn("Failed to read recipe '{}' from PacketBuffer", recipeId);
                return new GearCrafting.Shapeless(new DummyRecipe(recipeId), ModItems.axe);
//            }
        }

        @Override
        public void write(PacketBuffer buffer, GearCrafting.Shapeless recipe) {
            // TODO: Waiting for fix on Forge issue #5577, uncomment afterwards
//            RecipeSerializers.CRAFTING_SHAPELESS.write(buffer, (ShapelessRecipe) recipe.recipe);
        }

        @Override
        public ResourceLocation getName() {
            return NAME;
        }
    }

    /**
     * Shaped gear crafting serializer
     */
    public static final class ShapedSerializer implements IRecipeSerializer<GearCrafting.Shaped> {
        public static final ShapedSerializer INSTANCE = new ShapedSerializer();
        static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "shaped_gear_crafting");

        ShapedSerializer() {}

        @Override
        public GearCrafting.Shaped read(ResourceLocation recipeId, JsonObject json) {
            ShapedRecipe recipe = RecipeSerializers.CRAFTING_SHAPED.read(recipeId, json);
            Item item = recipe.getRecipeOutput().getItem();
            if (!(item instanceof ICoreItem))
                throw new JsonParseException("result must a gear item");
            return new GearCrafting.Shaped(recipe, (ICoreItem) item);
        }

        @Override
        public GearCrafting.Shaped read(ResourceLocation recipeId, PacketBuffer buffer) {
            // TODO: Waiting for fix on Forge issue #5577, remove try-catch afterwards
//            try {
//                ShapedRecipe recipe = RecipeSerializers.CRAFTING_SHAPED.read(recipeId, buffer);
//                Item item = recipe.getRecipeOutput().getItem();
//                if (!(item instanceof ICoreItem))
//                    throw new IllegalStateException("result must a gear item");
//                return new GearCrafting.Shaped(recipe, (ICoreItem) item);
//            } catch (DecoderException ex) {
//                SilentGear.LOGGER.warn("Failed to read recipe '{}' from PacketBuffer", recipeId);
                return new GearCrafting.Shaped(new DummyRecipe(recipeId), ModItems.axe);
//            }
        }

        @Override
        public void write(PacketBuffer buffer, GearCrafting.Shaped recipe) {
            // TODO: Waiting for fix on Forge issue #5577, uncomment afterwards
//            RecipeSerializers.CRAFTING_SHAPED.write(buffer, (ShapedRecipe) recipe.recipe);
        }

        @Override
        public ResourceLocation getName() {
            return NAME;
        }
    }
}
