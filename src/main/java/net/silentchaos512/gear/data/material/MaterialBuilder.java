package net.silentchaos512.gear.data.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.IMaterialSerializer;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.material.MaterialLayerList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.*;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.client.material.MaterialDisplay;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.gear.material.MaterialSerializers;
import net.silentchaos512.gear.gear.part.PartTextureSet;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "OverlyComplexClass"})
public class MaterialBuilder {
    final ResourceLocation id;
    private final int tier;
    private boolean canSalvage = true;
    private final Ingredient ingredient;
    private final Map<PartType, Ingredient> partSubstitutes = new LinkedHashMap<>();
    private boolean visible = true;
    private Collection<String> gearBlacklist = new ArrayList<>();
    private final Collection<ICondition> loadConditions = new ArrayList<>();
    @Nullable private ResourceLocation parent;
    private final Collection<IMaterialCategory> categories = new ArrayList<>();
    private Component name;
    @Nullable private Component namePrefix;
    private IMaterialSerializer<?> serializer = MaterialSerializers.STANDARD;
    private boolean simple = true;

    private final Map<PartType, StatModifierMap> stats = new LinkedHashMap<>();
    private final Map<PartType, List<ITraitInstance>> traits = new LinkedHashMap<>();
    private final Map<PartGearKey, MaterialLayerList> display = new LinkedHashMap<>();
    private boolean hasModels = true;

    public MaterialBuilder(ResourceLocation id, int tier, ResourceLocation ingredientTagName) {
        this(id, tier, Ingredient.of(ItemTags.create(ingredientTagName)));
    }

    public MaterialBuilder(ResourceLocation id, int tier, TagKey<Item> ingredient) {
        this(id, tier, Ingredient.of(ingredient));
    }

    public MaterialBuilder(ResourceLocation id, int tier, ItemLike... ingredients) {
        this(id, tier, Ingredient.of(ingredients));
    }

    public MaterialBuilder(ResourceLocation id, int tier, Ingredient ingredient) {
        this.id = id;
        this.tier = tier;
        this.ingredient = ingredient;
        this.name = Component.translatable(String.format("material.%s.%s",
                this.id.getNamespace(),
                this.id.getPath().replace("/", ".")));
    }

    public MaterialBuilder type(IMaterialSerializer<?> serializer, boolean simple) {
        this.serializer = serializer;
        this.simple = simple;
        return this;
    }

    public MaterialBuilder loadConditionTagExists(ResourceLocation tagId) {
        return loadCondition(new NotCondition(new TagEmptyCondition(tagId)));
    }

    public MaterialBuilder loadCondition(ICondition condition) {
        this.loadConditions.add(condition);
        return this;
    }

    public MaterialBuilder parent(ResourceLocation parent) {
        this.parent = parent;
        return this;
    }

    public MaterialBuilder categories(IMaterialCategory... categories) {
        this.categories.addAll(Arrays.asList(categories));
        return this;
    }

    public MaterialBuilder canSalvage(boolean value) {
        this.canSalvage = value;
        return this;
    }

    public MaterialBuilder partSubstitute(PartType partType, ItemLike item) {
        return partSubstitute(partType, Ingredient.of(item));
    }

    public MaterialBuilder partSubstitute(PartType partType, TagKey<Item> tag) {
        return partSubstitute(partType, Ingredient.of(tag));
    }

    public MaterialBuilder partSubstitute(PartType partType, Ingredient ingredient) {
        this.partSubstitutes.put(partType, ingredient);
        return this;
    }

    public MaterialBuilder visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public MaterialBuilder blacklistGearType(GearType gearType) {
        return blacklistGearType(gearType.getName());
    }

    public MaterialBuilder blacklistGearType(String gearType) {
        this.gearBlacklist.add(gearType);
        return this;
    }

    public MaterialBuilder name(Component text) {
        this.name = text;
        return this;
    }

    public MaterialBuilder namePrefix(Component text) {
        this.namePrefix = text;
        return this;
    }

    public MaterialBuilder displayAll(PartTextureSet texture, int color) {
        if (this.stats.isEmpty()) {
            throw new IllegalStateException("Must build stats map first!");
        }
        for (PartType partType : this.stats.keySet()) {
            // Remove highlight layer from non-mains
            PartTextureSet targetTexture = texture == PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT && partType != PartType.MAIN
                    ? PartTextureSet.HIGH_CONTRAST
                    : texture;

            if (partType == PartType.ADORNMENT)
                displayAdornment(targetTexture, color);
            else if (partType == PartType.CORD)
                displayBowstring(color);
            else if (partType == PartType.TIP)
                displayTip(targetTexture.getLayers(partType).get(0), color);
            else if (partType == PartType.COATING)
                displayCoating(targetTexture, color);
            else if (partType == PartType.LINING)
                displayLining(targetTexture, color);
            else
                display(partType, targetTexture, color);
        }
        return this;
    }

    public MaterialBuilder displayAdornment(PartTextureSet textures, int color) {
        display(PartType.ADORNMENT, GearType.ALL, new MaterialLayerList(PartType.ADORNMENT, textures, color));
        display(PartType.ADORNMENT, GearType.PART,
                new MaterialLayer(SilentGear.getId("adornment"), color),
                new MaterialLayer(SilentGear.getId("adornment_highlight"), Color.VALUE_WHITE)
        );
        return this;
    }

    public MaterialBuilder displayBowstring(int color) {
        display(PartType.CORD,
                new MaterialLayer(PartTextures.BOWSTRING_STRING, color),
                new MaterialLayer(PartTextures.ARROW, Color.VALUE_WHITE) // FIXME: Doesn't quite make sense to have this here
        );
        display(PartType.CORD, GearType.PART,
                new MaterialLayer(SilentGear.getId("bowstring"), color)
        );
        return this;
    }

    public MaterialBuilder displayCoating(PartTextureSet textures, int color) {
        display(PartType.COATING, GearType.ALL, new MaterialLayerList(PartType.MAIN, textures, color));
        display(PartType.COATING, GearType.PART,
                new MaterialLayer(SilentGear.getId("coating_material"), color),
                new MaterialLayer(SilentGear.getId("coating_jar"), Color.VALUE_WHITE)
        );
        return this;
    }

    public MaterialBuilder displayLining(PartTextureSet textures, int color) {
        display(PartType.LINING, GearType.PART, new MaterialLayerList(PartType.LINING, textures, color));
        return this;
    }

    public MaterialBuilder displayMain(PartTextureSet textures, int color) {
        return display(PartType.MAIN, GearType.ALL, new MaterialLayerList(PartType.MAIN, textures, color));
    }

    public MaterialBuilder displayTip(PartTextures texture, int color) {
        display(PartType.TIP, GearType.ALL,
                new MaterialLayer(texture, color)
        );
        display(PartType.TIP, GearType.PART,
                new MaterialLayer(SilentGear.getId("tip_base"), Color.VALUE_WHITE),
                new MaterialLayer(SilentGear.getId("tip"), color),
                new MaterialLayer(SilentGear.getId("tip_shine"), Color.VALUE_WHITE)
        );
        return this;
    }

    public MaterialBuilder displayFragment(PartTextures texture, int color) {
        display(PartType.MAIN, GearType.FRAGMENT, new MaterialLayer(texture, color));
        return this;
    }

    public MaterialBuilder display(PartType partType, PartTextureSet texture, int color) {
        display(partType, GearType.ALL, texture, color);

        // Compound part models
        if (partType != PartType.MAIN) {
            display(partType, GearType.PART, new MaterialLayer(partType.getName(), color));
        }

        return this;
    }

    public MaterialBuilder display(PartType partType, GearType gearType, PartTextureSet texture, int color) {
        MaterialLayerList materialLayerList = new MaterialLayerList(partType, texture, color);
        return display(partType, gearType, materialLayerList);
    }

    public MaterialBuilder display(PartType partType, MaterialLayer... layers) {
        return display(partType, GearType.ALL, layers);
    }

    public MaterialBuilder display(PartType partType, GearType gearType, MaterialLayer... layers) { // TODO: use PartGearKey parameter?
        return display(partType, gearType, new MaterialLayerList(layers));
    }

    public MaterialBuilder display(PartType partType, GearType gearType, MaterialLayerList layers) {
        this.display.put(PartGearKey.of(gearType, partType), layers);
        if (partType == PartType.MAIN && !this.display.containsKey(PartGearKey.of(GearType.FRAGMENT, partType))) {
            // Generate fragment model info if missing
            int color = layers.getLayers().isEmpty() ? Color.VALUE_WHITE : layers.getLayers().get(0).getColor();
            displayFragment(PartTextures.METAL, color);
        }
        return this;
    }

    public MaterialBuilder noModels() {
        this.hasModels = false;
        return this;
    }

    public MaterialBuilder noStats(PartType partType) {
        // Put an empty map for the part type, because the part type can only be supported if in the stats object
        stats.computeIfAbsent(partType, pt -> new StatModifierMap());
        return this;
    }

    public MaterialBuilder stat(PartType partType, IItemStat stat, float value) {
        return stat(partType, stat, GearType.ALL, value, StatInstance.Operation.AVG);
    }

    public MaterialBuilder stat(PartType partType, IItemStat stat, GearType gearType, float value) { // TODO: use PartGearKey parameter?
        return stat(partType, stat, gearType, value, StatInstance.Operation.AVG);
    }

    public MaterialBuilder stat(PartType partType, IItemStat stat, float value, StatInstance.Operation operation) {
        return stat(partType, stat, GearType.ALL, value, operation);
    }

    public MaterialBuilder stat(PartType partType, IItemStat stat, GearType gearType, float value, StatInstance.Operation operation) {
        StatGearKey key = StatGearKey.of(stat, gearType);
        StatInstance mod = StatInstance.of(value, operation, key);
        StatModifierMap map = stats.computeIfAbsent(partType, pt -> new StatModifierMap());
        map.put(stat, gearType, mod);
        return this;
    }

    public MaterialBuilder stat(PartType partType, ResourceLocation statId, float value) {
        return stat(partType, statId, value, StatInstance.Operation.AVG);
    }

    public MaterialBuilder stat(PartType partType, ResourceLocation statId, float value, StatInstance.Operation operation) {
        return stat(partType, LazyItemStat.of(statId), value, operation);
    }

    @Deprecated
    public MaterialBuilder mainStatsCommon(float toolDurability, float armorDurability, float enchantability, float rarity) {
        stat(PartType.MAIN, ItemStats.DURABILITY, toolDurability);
        stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, armorDurability);
        stat(PartType.MAIN, ItemStats.ENCHANTMENT_VALUE, enchantability);
        stat(PartType.MAIN, ItemStats.RARITY, rarity);
        return this;
    }

    public MaterialBuilder mainStatsCommon(float toolDurability, float armorDurability, float enchantability, float rarity, float chargeability) {
        stat(PartType.MAIN, ItemStats.DURABILITY, toolDurability);
        stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, armorDurability);
        stat(PartType.MAIN, ItemStats.ENCHANTMENT_VALUE, enchantability);
        stat(PartType.MAIN, ItemStats.RARITY, rarity);
        stat(PartType.MAIN, ItemStats.CHARGING_VALUE, chargeability);
        return this;
    }

    public MaterialBuilder mainStatsHarvest(int harvestLevel, float harvestSpeed) {
        stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, harvestLevel);
        stat(PartType.MAIN, ItemStats.HARVEST_SPEED, harvestSpeed);
        return this;
    }

    public MaterialBuilder mainStatsMelee(float attackDamage, float magicDamage, float attackSpeed) {
        stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, attackDamage);
        if (magicDamage > 0) {
            stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, magicDamage);
        }
        stat(PartType.MAIN, ItemStats.ATTACK_SPEED, attackSpeed);
        return this;
    }

    public MaterialBuilder mainStatsRanged(float rangedDamage, float rangedSpeed) {
        stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, rangedDamage);
        stat(PartType.MAIN, ItemStats.RANGED_SPEED, rangedSpeed);
        return this;
    }

    public MaterialBuilder mainStatsRanged(float rangedDamage, float rangedSpeed, float projectileSpeed, float projectileAccuracy) {
        stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, rangedDamage);
        stat(PartType.MAIN, ItemStats.RANGED_SPEED, rangedSpeed);
        stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, projectileSpeed);
        stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, projectileAccuracy);
        return this;
    }

    public MaterialBuilder mainStatsProjectile(float projectileSpeed, float projectileAccuracy) {
        stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, projectileSpeed);
        stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, projectileAccuracy);
        return this;
    }

    @Deprecated
    public MaterialBuilder mainStatsArmor(float armor, float toughness, float magicArmor) {
        stat(PartType.MAIN, ItemStats.ARMOR, armor);
        stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, toughness);
        stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, magicArmor);
        return this;
    }

    @SuppressWarnings({"MethodWithTooManyParameters", "OverlyComplexMethod"})
    public MaterialBuilder mainStatsArmor(float head, float chest, float legs, float feet, float toughness, float magicArmor) {
        if (this.stats.get(PartType.MAIN).containsKey(StatGearKey.of(ItemStats.ARMOR, GearType.ALL))) {
            throw new IllegalStateException("Called mainStatsArmor when armor stat is already defined");
        }

        if (head > 0 && chest > 0 && legs > 0 && feet > 0) {
            float sum = head + chest + legs + feet;
            stat(PartType.MAIN, ItemStats.ARMOR, sum);
        }

        if (head > 0)
            stat(PartType.MAIN, ItemStats.ARMOR, GearType.HELMET, head);
        if (chest > 0)
            stat(PartType.MAIN, ItemStats.ARMOR, GearType.CHESTPLATE, chest);
        if (legs > 0)
            stat(PartType.MAIN, ItemStats.ARMOR, GearType.LEGGINGS, legs);
        if (feet > 0)
            stat(PartType.MAIN, ItemStats.ARMOR, GearType.BOOTS, feet);
        if (toughness > 0)
            stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, toughness);
        if (magicArmor > 0)
            stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, magicArmor);

        return this;
    }

    public MaterialBuilder trait(PartType partType, DataResource<ITrait> trait, int level, ITraitCondition... conditions) {
        ITraitInstance inst = TraitInstance.of(trait, level, conditions);
        List<ITraitInstance> list = traits.computeIfAbsent(partType, pt -> new ArrayList<>());
        list.add(inst);
        return this;
    }

    @Deprecated
    public MaterialBuilder trait(PartType partType, ResourceLocation traitId, int level, ITraitCondition... conditions) {
        ITraitInstance inst = TraitInstance.lazy(traitId, level, conditions);
        List<ITraitInstance> list = traits.computeIfAbsent(partType, pt -> new ArrayList<>());
        list.add(inst);
        return this;
    }

    private void validate() {
        if (this.hasModels) {
            for (PartType type : this.stats.keySet()) {
                if (this.display.keySet().stream().noneMatch(key -> key.getPartType().equals(type))) {
                    throw new NullPointerException(String.format("Material builder %s has no model data for part type %s", this.id, type.getName()));
                }
            }
        }
    }

    public JsonObject serializeModel() {
        MaterialDisplay model = MaterialDisplay.of(id, this.display);
        return model.serialize();
    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public JsonObject serialize() {
        validate();

        JsonObject json = new JsonObject();

        json.addProperty("type", this.serializer.getName().toString());
        json.addProperty("simple", this.simple);

        if (this.parent != null) {
            json.addProperty("parent", this.parent.toString());
        }

        if (!this.loadConditions.isEmpty()) {
            JsonArray array = new JsonArray();
            for (ICondition condition : this.loadConditions) {
                array.add(CraftingHelper.serialize(condition));
            }
            json.add("conditions", array);
        }

        JsonObject availability = new JsonObject();
        if (this.tier >= 0) {
            availability.addProperty("tier", this.tier);

            if (!this.categories.isEmpty()) {
                JsonArray array = new JsonArray();
                for (IMaterialCategory category : this.categories) {
                    array.add(category.getName());
                }
                availability.add("categories", array);
            }

            availability.addProperty("visible", this.visible);

            JsonArray array = new JsonArray();
            for (String gearType : this.gearBlacklist) {
                array.add(gearType);
            }
            availability.add("gear_blacklist", array);

            availability.addProperty("can_salvage", this.canSalvage);
        }
        if (!availability.entrySet().isEmpty()) {
            json.add("availability", availability);
        }

        JsonObject craftingItems = new JsonObject();
        if (this.ingredient != Ingredient.EMPTY) {
            craftingItems.add("main", this.ingredient.toJson());
        }
        if (!this.partSubstitutes.isEmpty()) {
            JsonObject subs = new JsonObject();
            this.partSubstitutes.forEach((type, ing) -> subs.add(SilentGear.shortenId(type.getName()), ing.toJson()));
            craftingItems.add("subs", subs);
        }
        json.add("crafting_items", craftingItems);

        if (this.name != null) {
            json.add("name", Component.Serializer.toJsonTree(this.name));
        }

        if (this.namePrefix != null) {
            json.add("name_prefix", Component.Serializer.toJsonTree(this.namePrefix));
        }

        /*if (!this.display.isEmpty()) {
            JsonObject displayObj = new JsonObject();
            this.display.forEach((key, materialLayerList) -> displayObj.add(key, materialLayerList.serializeTypes()));
            json.add("display", displayObj);
        }*/

        if (!this.stats.isEmpty()) {
            JsonObject statsJson = new JsonObject();
            this.stats.forEach((partType, map) -> statsJson.add(SilentGear.shortenId(partType.getName()), map.serialize()));
            json.add("stats", statsJson);
        }

        if (!this.traits.isEmpty()) {
            JsonObject traitsJson = new JsonObject();
            this.traits.forEach((partType, list) -> {
                JsonArray array = new JsonArray();
                list.forEach(t -> array.add(t.serialize()));
                traitsJson.add(SilentGear.shortenId(partType.getName()), array);
            });
            json.add("traits", traitsJson);
        }

        json.add("model", serializeModel());

        return json;
    }
}
