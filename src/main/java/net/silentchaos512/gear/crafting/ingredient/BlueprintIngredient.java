package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.item.GearItemSet;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.setup.SgIngredientTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public final class BlueprintIngredient implements ICustomIngredient, IGearIngredient {
    public static final MapCodec<BlueprintIngredient> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    PartType.CODEC.fieldOf("part_type").forGetter(BlueprintIngredient::getPartType),
                    GearType.CODEC.fieldOf("gear_type").forGetter(BlueprintIngredient::getGearType)
            ).apply(instance, BlueprintIngredient::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintIngredient> STREAM_CODEC = StreamCodec.composite(
            PartType.STREAM_CODEC, ingredient -> ingredient.partType,
            GearType.STREAM_CODEC, ingredient -> ingredient.gearType,
            BlueprintIngredient::new
    );

    private final PartType partType;
    private final GearType gearType;

    @Nullable
    private ItemStack[] itemStacks;

    private BlueprintIngredient(PartType partType, GearType gearType) {
        this.partType = partType;
        this.gearType = gearType;
    }

    public static <T extends Item & IBlueprint> BlueprintIngredient of(T item) {
        ItemStack stack = new ItemStack(item);
        return new BlueprintIngredient(item.getPartType(stack), item.getGearType(stack));
    }

    public static BlueprintIngredient of(GearItemSet<?> gearItemSet) {
        return of(gearItemSet.blueprint());
    }

    @Override
    public IngredientType<?> getType() {
        return SgIngredientTypes.BLUEPRINT.get();
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
    public Stream<ItemStack> getItems() {
        this.dissolve();
        //noinspection AssignmentOrReturnOfFieldWithMutableType,ConstantConditions
        return Arrays.stream(this.itemStacks);
    }

    @Override
    public boolean isSimple() {
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
