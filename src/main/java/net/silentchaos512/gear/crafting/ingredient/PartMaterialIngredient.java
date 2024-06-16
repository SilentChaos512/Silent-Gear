package net.silentchaos512.gear.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.setup.SgIngredientTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PartMaterialIngredient extends Ingredient implements IGearIngredient {
    public static final Codec<PartMaterialIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PartType.CODEC.fieldOf("part_type").forGetter(PartMaterialIngredient::getPartType),
            GearType.CODEC.optionalFieldOf("gear_type", GearType.NONE).forGetter(PartMaterialIngredient::getGearType),
            Codec.INT.optionalFieldOf("min_tier", 0).forGetter(ing -> ing.minTier),
            Codec.INT.optionalFieldOf("max_tier", Integer.MAX_VALUE).forGetter(ing -> ing.maxTier),
            MaterialGrade.CODEC.optionalFieldOf("min_grade", MaterialGrade.NONE).forGetter(ing -> ing.minGrade),
            MaterialGrade.CODEC.optionalFieldOf("max_grade", MaterialGrade.NONE).forGetter(ing -> ing.maxGrade),
            DataResource.MATERIAL_CODEC.optionalFieldOf("material", null).forGetter(ing -> ing.material),
            MaterialCategories.CODEC.listOf().optionalFieldOf("categories", Collections.emptyList()).forGetter(ing -> ImmutableList.copyOf(ing.categories))
    ).apply(instance, PartMaterialIngredient::new));

    private final PartType partType;
    private final GearType gearType;
    private final int minTier;
    private final int maxTier;
    private final MaterialGrade minGrade;
    private final MaterialGrade maxGrade;
    @Nullable private final DataResource<IMaterial> material;
    private final Set<IMaterialCategory> categories = new LinkedHashSet<>();

    public PartMaterialIngredient(PartType partType, GearType gearType, int minTier, int maxTier,
                                  MaterialGrade minGrade, MaterialGrade maxGrade,
                                  @Nullable DataResource<IMaterial> material,
                                  Collection<IMaterialCategory> categories
    ) {
        super(Stream.of());
        this.partType = partType;
        this.gearType = gearType;
        this.minTier = minTier;
        this.maxTier = maxTier;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
        this.material = material;
        this.categories.addAll(categories);
    }

    private PartMaterialIngredient(PartType partType, GearType gearType, int minTier, int maxTier) {
        this(partType, gearType, minTier, maxTier, MaterialGrade.NONE, MaterialGrade.NONE, null, Collections.emptySet());
    }

    public static PartMaterialIngredient of(PartType partType) {
        return of(partType, GearType.TOOL);
    }

    public static PartMaterialIngredient of(PartType partType, IMaterialCategory... categories) {
        return of(partType, GearType.TOOL, categories);
    }

    public static PartMaterialIngredient of(PartType partType, GearType gearType) {
        return new PartMaterialIngredient(partType, gearType, 0, Integer.MAX_VALUE);
    }

    public static PartMaterialIngredient of(PartType partType, GearType gearType, IMaterialCategory... categories) {
        return of(partType, gearType, 0, Integer.MAX_VALUE, categories);
    }

    public static PartMaterialIngredient of(PartType partType, GearType gearType, int minTier, int maxTier) {
        return new PartMaterialIngredient(partType, gearType, minTier, maxTier);
    }

    public static PartMaterialIngredient of(PartType partType, GearType gearType, int minTier, int maxTier, IMaterialCategory... categories) {
        PartMaterialIngredient ret = new PartMaterialIngredient(partType, gearType, minTier, maxTier);
        ret.categories.addAll(Arrays.asList(categories));
        return ret;
    }

    public static Builder builder(PartType partType) {
        return builder(partType, GearType.TOOL);
    }

    public static Builder builder(PartType partType, GearType gearType) {
        return new Builder(partType, gearType);
    }

    @Override
    public IngredientType<?> getType() {
        return SgIngredientTypes.MATERIAL.get();
    }

    @Override
    public PartType getPartType() {
        return partType;
    }

    @Override
    public GearType getGearType() {
        return gearType;
    }

    @Override
    public Optional<Component> getJeiHint() {
        MutableComponent text;
        if (!this.categories.isEmpty()) {
            MutableComponent cats = TextUtil.separatedList(categories.stream()
                    .map(IMaterialCategory::getDisplayName)
                    .collect(Collectors.toList())
            );
            text = TextUtil.withColor(cats, Color.INDIANRED);
        } else {
            MutableComponent any = TextUtil.translate("material.category", "any");
            text = TextUtil.withColor(any, Color.LIGHTGREEN);
        }

        PartGearKey key = PartGearKey.of(this.gearType, this.partType);
        text.append(TextUtil.misc("spaceBrackets", key.getDisplayName()).withStyle(ChatFormatting.GRAY));

        return Optional.of(TextUtil.translate("jei", "materialType", text));
    }

    public Set<IMaterialCategory> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        MaterialInstance mat = MaterialInstance.from(stack);
        if (mat == null) return false;

        return mat.get().isCraftingAllowed(mat, partType, gearType)
                && (categories.isEmpty() || mat.hasAnyCategory(categories))
                && (this.material == null || this.material.getId().equals(mat.getId()))
                && tierMatches(mat.getTier(this.partType))
                && gradesMatch(MaterialGrade.fromStack(stack));
    }

    private boolean tierMatches(int tier) {
        return tier >= this.minTier && tier <= this.maxTier;
    }

    private boolean gradesMatch(MaterialGrade grade) {
        return grade.ordinal() >= this.minGrade.ordinal()
                && (this.maxGrade == MaterialGrade.NONE || grade.ordinal() <= this.maxGrade.ordinal());
    }

    @Override
    public ItemStack[] getItems() {
        Collection<IMaterial> materials = MaterialManager.getValues();
        if (!materials.isEmpty()) {
            return materials.stream()
                    .map(MaterialInstance::of)
                    .filter(mat -> mat.get().isCraftingAllowed(mat, partType, gearType))
                    .filter(mat -> this.material == null || this.material.getId().equals(mat.getId()))
                    .filter(mat -> categories.isEmpty() || mat.hasAnyCategory(categories))
                    .filter(mat -> tierMatches(mat.getTier(this.partType)))
                    .flatMap(mat -> Stream.of(mat.get().getIngredient().getItems()))
                    .filter(stack -> !stack.isEmpty())
                    .map(stack -> this.minGrade != MaterialGrade.NONE ? this.minGrade.copyWithGrade(stack) : stack)
                    .toArray(ItemStack[]::new);
        }
        return super.getItems();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static class Builder {
        private final PartType partType;
        private final GearType gearType;
        private int minTier = 0;
        private int maxTier = Integer.MAX_VALUE;
        private MaterialGrade minGrade = MaterialGrade.NONE;
        private MaterialGrade maxGrade = MaterialGrade.NONE;
        private DataResource<IMaterial> material;
        private final Set<IMaterialCategory> categories = new LinkedHashSet<>();

        public Builder(PartType partType, GearType gearType) {
            this.partType = partType;
            this.gearType = gearType;
        }

        public Builder withCategories(IMaterialCategory... categories) {
            this.categories.addAll(Arrays.asList(categories));
            return this;
        }

        public Builder withTier(int min, int max) {
            this.minTier = min;
            this.maxTier = max;
            return this;
        }

        public Builder withGrade(@Nullable MaterialGrade min, @Nullable MaterialGrade max) {
            if (min != null) {
                this.minGrade = min;
            }
            if (max != null) {
                this.maxGrade = max;
            }
            return this;
        }

        public Builder withMaterial(DataResource<IMaterial> material) {
            this.material = material;
            return this;
        }

        public PartMaterialIngredient build() {
            return new PartMaterialIngredient(partType, gearType, minTier, maxTier, minGrade, maxGrade, material, categories);
        }
    }
}
