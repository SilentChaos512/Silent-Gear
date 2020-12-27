package net.silentchaos512.gear.data.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.material.MaterialLayerList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.*;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.client.material.MaterialDisplay;
import net.silentchaos512.gear.client.material.PartGearKey;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.gear.part.PartTextureSet;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("WeakerAccess")
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
    private ITextComponent name;
    @Nullable private ITextComponent namePrefix;

    private final Map<PartType, StatModifierMap> stats = new LinkedHashMap<>();
    private final Map<PartType, List<ITraitInstance>> traits = new LinkedHashMap<>();
    private final Map<PartGearKey, MaterialLayerList> display = new LinkedHashMap<>();

    public MaterialBuilder(ResourceLocation id, int tier, ResourceLocation tag) {
        this(id, tier, Ingredient.fromTag(ItemTags.makeWrapperTag(tag.toString())));
    }

    public MaterialBuilder(ResourceLocation id, int tier, ITag<Item> tag) {
        this(id, tier, Ingredient.fromTag(tag));
    }

    public MaterialBuilder(ResourceLocation id, int tier, IItemProvider... items) {
        this(id, tier, Ingredient.fromItems(items));
    }

    public MaterialBuilder(ResourceLocation id, int tier, Ingredient ingredient) {
        this.id = id;
        this.tier = tier;
        this.ingredient = ingredient;
        //noinspection DynamicRegexReplaceableByCompiledPattern
        this.name = new TranslationTextComponent(String.format("material.%s.%s",
                this.id.getNamespace(),
                this.id.getPath().replace("/", ".")));
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

    public MaterialBuilder partSubstitute(PartType partType, IItemProvider item) {
        return partSubstitute(partType, Ingredient.fromItems(item));
    }

    public MaterialBuilder partSubstitute(PartType partType, ITag<Item> tag) {
        return partSubstitute(partType, Ingredient.fromTag(tag));
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

    public MaterialBuilder name(ITextComponent text) {
        this.name = text;
        return this;
    }

    public MaterialBuilder namePrefix(ITextComponent text) {
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
            else if (partType == PartType.BOWSTRING)
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
        display(PartType.BOWSTRING,
                new MaterialLayer(PartTextures.BOWSTRING_STRING, color),
                new MaterialLayer(PartTextures.ARROW, Color.VALUE_WHITE)
        );
        display(PartType.BOWSTRING, GearType.PART,
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

    public MaterialBuilder display(PartType partType, GearType gearType, MaterialLayer... layers) {
        return display(partType, gearType, new MaterialLayerList(layers));
    }

    public MaterialBuilder display(PartType partType, GearType gearType, MaterialLayerList layers) {
        this.display.put(PartGearKey.of(gearType, partType), layers);
        if (partType == PartType.MAIN && !this.display.containsKey(PartGearKey.of(GearType.FRAGMENT, partType))) {
            // Generate fragment model info if missing
            displayFragment(PartTextures.METAL, layers.getPrimaryColor());
        }
        return this;
    }

    public MaterialBuilder noStats(PartType partType) {
        // Put an empty map for the part type, because the part type can only be supported if in the stats object
        stats.computeIfAbsent(partType, pt -> new StatModifierMap());
        return this;
    }

    public MaterialBuilder stat(PartType partType, IItemStat stat, float value) {
        return stat(partType, stat, value, StatInstance.Operation.AVG);
    }

    public MaterialBuilder stat(PartType partType, IItemStat stat, float value, StatInstance.Operation operation) {
        StatInstance mod = StatInstance.of(value, operation);
        StatModifierMap map = stats.computeIfAbsent(partType, pt -> new StatModifierMap());
        map.put(stat, mod);
        return this;
    }

    public MaterialBuilder stat(PartType partType, ResourceLocation statId, float value) {
        return stat(partType, statId, value, StatInstance.Operation.AVG);
    }

    public MaterialBuilder stat(PartType partType, ResourceLocation statId, float value, StatInstance.Operation operation) {
        return stat(partType, LazyItemStat.of(statId), value, operation);
    }

    public MaterialBuilder mainStatsCommon(float toolDurability, float armorDurability, float enchantability, float rarity) {
        stat(PartType.MAIN, ItemStats.DURABILITY, toolDurability);
        stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, armorDurability);
        stat(PartType.MAIN, ItemStats.ENCHANTABILITY, enchantability);
        stat(PartType.MAIN, ItemStats.RARITY, rarity);
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

    public MaterialBuilder mainStatsArmor(float armor, float toughness, float magicArmor) {
        stat(PartType.MAIN, ItemStats.ARMOR, armor);
        stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, toughness);
        stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, magicArmor);
        return this;
    }

    public MaterialBuilder trait(PartType partType, DataResource<ITrait> trait, int level, ITraitCondition... conditions) {
        ITraitInstance inst = TraitInstance.of(trait, level, conditions);
        List<ITraitInstance> list = traits.computeIfAbsent(partType, pt -> new ArrayList<>());
        list.add(inst);
        return this;
    }

    public MaterialBuilder trait(PartType partType, ResourceLocation traitId, int level, ITraitCondition... conditions) {
        ITraitInstance inst = TraitInstance.lazy(traitId, level, conditions);
        List<ITraitInstance> list = traits.computeIfAbsent(partType, pt -> new ArrayList<>());
        list.add(inst);
        return this;
    }

    public JsonObject serializeModel() {
        MaterialDisplay model = MaterialDisplay.of(this.display);
        return model.serialize();
    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public JsonObject serialize() {
        JsonObject json = new JsonObject();

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
            craftingItems.add("main", this.ingredient.serialize());
        }
        if (!this.partSubstitutes.isEmpty()) {
            JsonObject subs = new JsonObject();
            this.partSubstitutes.forEach((type, ing) -> subs.add(SilentGear.shortenId(type.getName()), ing.serialize()));
            craftingItems.add("subs", subs);
        }
        json.add("crafting_items", craftingItems);

        if (this.name != null) {
            json.add("name", ITextComponent.Serializer.toJsonTree(this.name));
        }

        if (this.namePrefix != null) {
            json.add("name_prefix", ITextComponent.Serializer.toJsonTree(this.namePrefix));
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

        return json;
    }
}
