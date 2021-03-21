package net.silentchaos512.gear.crafting.ingredient;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PartMaterialIngredient extends Ingredient implements IGearIngredient {
    private final PartType partType;
    private final GearType gearType;
    private final int minTier;
    private final int maxTier;
    private final Set<IMaterialCategory> categories = new LinkedHashSet<>();

    private PartMaterialIngredient(PartType partType, GearType gearType, int minTier, int maxTier) {
        super(Stream.of());
        this.partType = partType;
        this.gearType = gearType;
        this.minTier = minTier;
        this.maxTier = maxTier;
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

    @Override
    public PartType getPartType() {
        return partType;
    }

    @Override
    public GearType getGearType() {
        return gearType;
    }

    @Override
    public Optional<ITextComponent> getJeiHint() {
        IFormattableTextComponent text;
        if (!this.categories.isEmpty()) {
            IFormattableTextComponent cats = new StringTextComponent(categories.stream()
                    .map(IMaterialCategory::getName)
                    .collect(Collectors.joining(", "))
            );
            text = TextUtil.withColor(cats, Color.INDIANRED);
        } else {
            IFormattableTextComponent any = new StringTextComponent("any");
            text = TextUtil.withColor(any, Color.LIGHTGREEN);
        }

        PartGearKey key = PartGearKey.of(this.gearType, this.partType);
        text.append(TextUtil.misc("spaceBrackets", key.toString()).mergeStyle(TextFormatting.GRAY));

        return Optional.of(TextUtil.translate("jei", "materialType", text));
    }

    public Set<IMaterialCategory> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        MaterialInstance material = MaterialInstance.from(stack);
        if (material == null) return false;

        int tier = material.getTier(this.partType);
        return material.get().isCraftingAllowed(material, partType, gearType)
                && (categories.isEmpty() || material.hasAnyCategory(categories))
                && tierMatches(tier);
    }

    private boolean tierMatches(int tier) {
        return tier >= this.minTier && tier <= this.maxTier;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        Collection<IMaterial> materials = MaterialManager.getValues();
        if (!materials.isEmpty()) {
            return materials.stream()
                    .map(MaterialInstance::of)
                    .filter(mat -> mat.get().isCraftingAllowed(mat, partType, gearType))
                    .filter(mat -> categories.isEmpty() || mat.hasAnyCategory(categories))
                    .filter(mat -> tierMatches(mat.getTier(this.partType)))
                    .flatMap(mat -> Stream.of(mat.get().getIngredient().getMatchingStacks()))
                    .filter(stack -> !stack.isEmpty())
                    .toArray(ItemStack[]::new);
        }
        return super.getMatchingStacks();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean hasNoMatchingItems() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", Serializer.NAME.toString());
        json.addProperty("part_type", this.partType.getName().toString());
        if (this.gearType != GearType.TOOL) {
            json.addProperty("gear_type", this.gearType.getName());
        }
        if (!this.categories.isEmpty()) {
            JsonArray array = new JsonArray();
            this.categories.forEach(cat -> array.add(cat.getName()));
            json.add("categories", array);
        }
        if (this.minTier > 0) {
            json.addProperty("min_tier", this.minTier);
        }
        if (this.maxTier < Integer.MAX_VALUE) {
            json.addProperty("max_tier", this.maxTier);
        }
        return json;
    }

    public static final class Serializer implements IIngredientSerializer<PartMaterialIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = SilentGear.getId("material");

        private Serializer() {}

        @Override
        public PartMaterialIngredient parse(PacketBuffer buffer) {
            ResourceLocation typeName = buffer.readResourceLocation();
            PartType partType = PartType.get(typeName);
            if (partType == null) {
                throw new JsonParseException("Unknown part type: " + typeName);
            }

            GearType gearType = GearType.get(buffer.readString());
            if (gearType.isInvalid()) {
                throw new JsonParseException("Unknown gear type: " + typeName);
            }

            int categoryCount = buffer.readByte();
            IMaterialCategory[] categories = new IMaterialCategory[categoryCount];
            for (int i = 0; i < categoryCount; ++i) {
                categories[i] = MaterialCategories.get(buffer.readString());
            }


            int minTier = buffer.readVarInt();
            int maxTier = buffer.readVarInt();

            return of(partType, gearType, minTier, maxTier, categories);
        }

        @Override
        public PartMaterialIngredient parse(JsonObject json) {
            String typeName = JSONUtils.getString(json, "part_type", "");
            if (typeName.isEmpty()) {
                throw new JsonSyntaxException("'part_type' is missing");
            }

            PartType type = PartType.get(Objects.requireNonNull(SilentGear.getIdWithDefaultNamespace(typeName)));
            if (type == null) {
                throw new JsonSyntaxException("part_type " + typeName + " does not exist");
            }

            String gearTypeName = JSONUtils.getString(json, "gear_type", "tool");
            GearType gearType = GearType.get(gearTypeName);
            if (gearType.isInvalid()) {
                throw new JsonSyntaxException("gear_type " + gearTypeName + " does not exist");
            }

            Collection<IMaterialCategory> categories = new ArrayList<>();
            if (json.has("categories")) {
                JsonArray array = json.getAsJsonArray("categories");
                for (JsonElement element : array) {
                    categories.add(MaterialCategories.get(element.getAsString()));
                }
            }

            int minTier = JSONUtils.getInt(json, "min_tier", 0);
            int maxTier = JSONUtils.getInt(json, "max_tier", Integer.MAX_VALUE);

            PartMaterialIngredient ret = of(type, gearType, minTier, maxTier);
            ret.categories.addAll(categories);
            return ret;
        }

        @Override
        public void write(PacketBuffer buffer, PartMaterialIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.partType.getName());
            buffer.writeString(ingredient.gearType.getName());
            buffer.writeByte(ingredient.categories.size());
            ingredient.categories.forEach(cat -> buffer.writeString(cat.getName()));
            buffer.writeVarInt(ingredient.minTier);
            buffer.writeVarInt(ingredient.maxTier);
        }
    }
}
