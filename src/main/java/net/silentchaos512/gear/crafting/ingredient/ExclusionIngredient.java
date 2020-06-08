package net.silentchaos512.gear.crafting.ingredient;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.silentchaos512.gear.SilentGear;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

// TODO: Move to Silent Lib?
public class ExclusionIngredient extends Ingredient {
    private final Ingredient parent;
    private final Collection<ResourceLocation> exclusions = new ArrayList<>();

    public ExclusionIngredient(Ingredient parent, Collection<ResourceLocation> exclusions) {
        super(Stream.of());
        this.parent = parent;
        this.exclusions.addAll(exclusions);
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        List<ItemStack> ret = Arrays.asList(parent.getMatchingStacks());
        exclusions.forEach(id -> ret.removeIf(stack -> isItem(id, stack)));
        return ret.toArray(new ItemStack[0]);
    }

    @Override
    public IntList getValidItemStacksPacked() {
        // FIXME?
        return super.getValidItemStacksPacked();
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return stack != null && parent.test(stack) && exclusions.stream().noneMatch(id -> isItem(id, stack));
    }

    @Override
    protected void invalidate() {
    }

    @Override
    public boolean isSimple() {
        return parent.isSimple();
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    private static boolean isItem(ResourceLocation id, ItemStack stack) {
        return id.equals(stack.getItem().getRegistryName());
    }

    public static class Serializer implements IIngredientSerializer<ExclusionIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = SilentGear.getId("exclusion");

        @Nonnull
        @Override
        public ExclusionIngredient parse(@Nonnull JsonObject json) {
            Ingredient value = Ingredient.deserialize(json.get("value"));
            List<ResourceLocation> list = new ArrayList<>();
            json.get("exclusions").getAsJsonArray().forEach(e -> list.add(new ResourceLocation(e.getAsString())));
            return new ExclusionIngredient(value, list);
        }

        @Nonnull
        @Override
        public ExclusionIngredient parse(@Nonnull PacketBuffer buffer) {
            List<ResourceLocation> list = new ArrayList<>();
            int count = buffer.readByte();
            for (int i = 0; i < count; ++i) {
                list.add(buffer.readResourceLocation());
            }
            return new ExclusionIngredient(Ingredient.read(buffer), list);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull ExclusionIngredient ingredient) {
            buffer.writeByte(ingredient.exclusions.size());
            ingredient.exclusions.forEach(buffer::writeResourceLocation);
            ingredient.parent.write(buffer);
        }

    }
}
