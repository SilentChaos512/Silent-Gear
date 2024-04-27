package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public final class BlueprintIngredient extends Ingredient implements IGearIngredient {
    public static final Codec<BlueprintIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PartType.CODEC.fieldOf("part_type").forGetter(BlueprintIngredient::getPartType),
            GearType.CODEC.fieldOf("gear_type").forGetter(BlueprintIngredient::getGearType)
    ).apply(instance, BlueprintIngredient::new));

    private final PartType partType;
    private final GearType gearType;

    @Nullable
    private ItemStack[] itemStacks;

    private BlueprintIngredient(PartType partType, GearType gearType) {
        super(Stream.of());
        this.partType = partType;
        this.gearType = gearType;
    }

    public static <T extends Item & IBlueprint> BlueprintIngredient of(T item) {
        ItemStack stack = new ItemStack(item);
        return new BlueprintIngredient(item.getPartType(stack), item.getGearType(stack));
    }

    private void dissolve() {
        if (this.itemStacks == null) {
            this.itemStacks = BuiltInRegistries.ITEM.stream()
                    .filter(item -> item instanceof IBlueprint)
                    .map(ItemStack::new)
                    .filter(this::testBlueprint)
                    .toArray(ItemStack[]::new);
        }
    }

    private boolean testBlueprint(ItemStack stack) {
        if (stack.getItem() instanceof IBlueprint item) {
            return item.getGearType(stack) == this.gearType && item.getPartType(stack) == this.partType;
        }
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        this.dissolve();
        return this.testBlueprint(stack);
    }

    @Override
    public ItemStack[] getItems() {
        this.dissolve();
        //noinspection AssignmentOrReturnOfFieldWithMutableType,ConstantConditions
        return this.itemStacks;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public PartType getPartType() {
        return this.partType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType;
    }

    @Override
    public Optional<Component> getJeiHint() {
        PartGearKey key = PartGearKey.of(this.gearType, this.partType);
        MutableComponent keyText = key.getDisplayName().copy();
        Component text = TextUtil.withColor(keyText, Color.DODGERBLUE);
        return Optional.of(TextUtil.translate("jei", "blueprintType", text));
    }
}
