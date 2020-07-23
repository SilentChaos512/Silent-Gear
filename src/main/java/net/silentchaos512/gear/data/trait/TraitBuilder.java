package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.traits.SimpleTrait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class TraitBuilder {
    final ResourceLocation traitId;
    private final int maxLevel;
    private final ResourceLocation type;

    private ITextComponent name;
    private ITextComponent description;

    private final Collection<ResourceLocation> cancelsList = new ArrayList<>();
    private final Collection<ResourceLocation> overridesList = new ArrayList<>();

    private Consumer<JsonObject> extraData = json -> {};

    public TraitBuilder(ResourceLocation traitId, int maxLevel, ITraitSerializer<?> serializer) {
        this.traitId = traitId;
        this.type = serializer.getName();
        this.maxLevel = maxLevel;

        this.name = new TranslationTextComponent(Util.makeTranslationKey("trait", traitId));
        this.description = new TranslationTextComponent(Util.makeTranslationKey("trait", traitId) + ".desc");
    }

    public static TraitBuilder simple(ResourceLocation traitId, int maxLevel) {
        return new TraitBuilder(traitId, maxLevel, SimpleTrait.SERIALIZER);
    }

    public TraitBuilder setName(ITextComponent text) {
        this.name = text;
        return this;
    }

    public TraitBuilder setDescription(ITextComponent text) {
        this.description = text;
        return this;
    }

    public TraitBuilder cancelsWith(ResourceLocation trait) {
        this.cancelsList.add(trait);
        return this;
    }

    public TraitBuilder overridesTrait(ResourceLocation trait) {
        this.overridesList.add(trait);
        return this;
    }

    /**
     * The "I don't feel like extending the class for this basic trait" method. Use it to append
     * small amounts of extra properties to JSON.
     *
     * @param consumer The consumer
     * @return The trait builder
     */
    public TraitBuilder extraData(Consumer<JsonObject> consumer) {
        this.extraData = consumer;
        return this;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.addProperty("type", this.type.toString());
        json.addProperty("max_level", this.maxLevel);
        json.add("name", ITextComponent.Serializer.toJsonTree(this.name));
        json.add("description", ITextComponent.Serializer.toJsonTree(this.description));

        if (!this.cancelsList.isEmpty()) {
            JsonArray cancelsArray = new JsonArray();
            this.cancelsList.forEach(id -> cancelsArray.add(id.toString()));
            json.add("cancels_with", cancelsArray);
        }

        if (!this.overridesList.isEmpty()) {
            JsonArray overridesArray = new JsonArray();
            this.overridesList.forEach(id -> overridesArray.add(id.toString()));
            json.add("overrides", overridesArray);
        }

        this.extraData.accept(json);

        return json;
    }
}
