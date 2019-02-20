package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.lib.collection.StackList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class GearCrafting implements IRecipe {
    private final ShapelessRecipe recipe;
    private final ICoreItem item;

    private GearCrafting(ShapelessRecipe recipeTemplate, ICoreItem item) {
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

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements IRecipeSerializer<GearCrafting> {
        public static final Serializer INSTANCE = new Serializer();
        static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "gear_crafting");

        Serializer() {}

        @Override
        public GearCrafting read(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe recipe = RecipeSerializers.CRAFTING_SHAPELESS.read(recipeId, json);
            Item item = recipe.getRecipeOutput().getItem();
            if (!(item instanceof ICoreItem))
                throw new JsonParseException("result must a gear item");
            return new GearCrafting(recipe, (ICoreItem) item);
        }

        @Override
        public GearCrafting read(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapelessRecipe recipe = RecipeSerializers.CRAFTING_SHAPELESS.read(recipeId, buffer);
            Item item = recipe.getRecipeOutput().getItem();
            if (!(item instanceof ICoreItem))
                throw new IllegalStateException("result must a gear item");
            return new GearCrafting(recipe, (ICoreItem) item);
        }

        @Override
        public void write(PacketBuffer buffer, GearCrafting recipe) {}

        @Override
        public ResourceLocation getName() {
            return NAME;
        }
    }
}
