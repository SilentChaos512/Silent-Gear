package net.silentchaos512.gear.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgIngredientTypes;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PartMaterialIngredient implements ICustomIngredient, IGearIngredient {
    public static final MapCodec<PartMaterialIngredient> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    PartType.CODEC.fieldOf("part_type").forGetter(PartMaterialIngredient::getPartType),
                    GearType.CODEC.optionalFieldOf("gear_type").forGetter(ing -> Optional.of(ing.gearType)),
                    MaterialGrade.CODEC.optionalFieldOf("min_grade", MaterialGrade.NONE).forGetter(ing -> ing.minGrade),
                    MaterialGrade.CODEC.optionalFieldOf("max_grade", MaterialGrade.NONE).forGetter(ing -> ing.maxGrade),
                    DataResource.MATERIAL_CODEC.optionalFieldOf("material").forGetter(ing -> Optional.ofNullable(ing.material)),
                    MaterialCategories.CODEC.listOf().optionalFieldOf("categories", Collections.emptyList()).forGetter(ing -> ImmutableList.copyOf(ing.categories))
            ).apply(instance, (pt, gt, minGrade, maxGrade, material, categories) -> {
                return new PartMaterialIngredient(pt, gt.orElse(GearTypes.NONE.get()), minGrade, maxGrade, material.orElse(null), categories);
            })
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, PartMaterialIngredient> STREAM_CODEC = StreamCodec.of(
            (buf, ing) -> {
                PartType.STREAM_CODEC.encode(buf, ing.partType);
                GearType.STREAM_CODEC.encode(buf, ing.gearType);
                MaterialGrade.STREAM_CODEC.encode(buf, ing.minGrade);
                MaterialGrade.STREAM_CODEC.encode(buf, ing.maxGrade);
                buf.writeBoolean(ing.material != null);
                if (ing.material != null) {
                    DataResource.MATERIAL_STREAM_CODEC.encode(buf, ing.material);
                }
                CodecUtils.encodeList(buf, ing.categories, MaterialCategories.STREAM_CODEC);
            },
            buf -> {
                var partType = PartType.STREAM_CODEC.decode(buf);
                var gearType = GearType.STREAM_CODEC.decode(buf);
                var minGrade = MaterialGrade.STREAM_CODEC.decode(buf);
                var maxGrade = MaterialGrade.STREAM_CODEC.decode(buf);
                var material = buf.readBoolean() ? DataResource.MATERIAL_STREAM_CODEC.decode(buf) : null;
                List<IMaterialCategory> categories = CodecUtils.decodeList(buf, MaterialCategories.STREAM_CODEC);
                return new PartMaterialIngredient(partType, gearType, minGrade, maxGrade, material, categories);
            }
    );

    private final PartType partType;
    private final GearType gearType;
    private final MaterialGrade minGrade;
    private final MaterialGrade maxGrade;
    @Nullable
    private final DataResource<Material> material;
    private final Set<IMaterialCategory> categories = new LinkedHashSet<>();

    public PartMaterialIngredient(PartType partType, GearType gearType,
                                  MaterialGrade minGrade, MaterialGrade maxGrade,
                                  @Nullable DataResource<Material> material,
                                  Collection<IMaterialCategory> categories
    ) {
        this.partType = partType;
        this.gearType = gearType;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
        this.material = material;
        this.categories.addAll(categories);
    }

    private PartMaterialIngredient(PartType partType, GearType gearType) {
        this(partType, gearType, MaterialGrade.NONE, MaterialGrade.NONE, null, Collections.emptySet());
    }

    public static PartMaterialIngredient of(PartType partType) {
        return of(partType, GearTypes.TOOL.get());
    }

    public static PartMaterialIngredient of(PartType partType, IMaterialCategory... categories) {
        return of(partType, GearTypes.TOOL.get(), categories);
    }

    public static PartMaterialIngredient of(PartType partType, GearType gearType) {
        return new PartMaterialIngredient(partType, gearType);
    }

    public static PartMaterialIngredient of(PartType partType, GearType gearType, IMaterialCategory... categories) {
        PartMaterialIngredient ret = new PartMaterialIngredient(partType, gearType);
        ret.categories.addAll(Arrays.asList(categories));
        return ret;
    }

    public static Builder builder(PartType partType) {
        return builder(partType, GearTypes.TOOL.get());
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
            MutableComponent any = TextUtil.translate("material.group", "any");
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
                && gradesMatch(MaterialGrade.fromStack(stack));
    }

    private boolean gradesMatch(MaterialGrade grade) {
        return grade.ordinal() >= this.minGrade.ordinal()
                && (this.maxGrade == MaterialGrade.NONE || grade.ordinal() <= this.maxGrade.ordinal());
    }

    @Override
    public Stream<ItemStack> getItems() {
        Collection<Material> materials = SgRegistries.MATERIAL.getValues(true);
        if (!materials.isEmpty()) {
            return materials.stream()
                    .map(MaterialInstance::of)
                    .filter(mat -> mat.get().isCraftingAllowed(mat, partType, gearType))
                    .filter(mat -> this.material == null || this.material.getId().equals(mat.getId()))
                    .filter(mat -> categories.isEmpty() || mat.hasAnyCategory(categories))
                    .flatMap(mat -> Stream.of(mat.get().getIngredient().getItems()))
                    .filter(stack -> !stack.isEmpty())
                    .map(stack -> this.minGrade != MaterialGrade.NONE ? this.minGrade.copyWithGrade(stack) : stack);
        }
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    public static class Builder {
        private final PartType partType;
        private final GearType gearType;
        private MaterialGrade minGrade = MaterialGrade.NONE;
        private MaterialGrade maxGrade = MaterialGrade.NONE;
        private DataResource<Material> material;
        private final Set<IMaterialCategory> categories = new LinkedHashSet<>();

        public Builder(PartType partType, GearType gearType) {
            this.partType = partType;
            this.gearType = gearType;
        }

        public Builder withCategories(IMaterialCategory... categories) {
            this.categories.addAll(Arrays.asList(categories));
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

        public Builder withMaterial(DataResource<Material> material) {
            this.material = material;
            return this;
        }

        public PartMaterialIngredient build() {
            return new PartMaterialIngredient(partType, gearType, minGrade, maxGrade, material, categories);
        }
    }
}
