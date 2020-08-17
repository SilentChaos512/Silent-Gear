package net.silentchaos512.gear.api.traits;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TraitInstance implements ITraitInstance {
    private final ITrait trait;
    private final int level;
    private final ImmutableList<ITraitCondition> conditions;

    private TraitInstance(ITrait trait, int level, ITraitCondition... conditions) {
        this.trait = trait;
        this.level = level;
        this.conditions = ImmutableList.<ITraitCondition>builder().add(conditions).build();
    }

    /**
     * Gets a trait instance. Will return a {@link TraitInstance} if the trait is loaded, or a
     * {@link LazyTraitInstance} if not.
     *
     * @param trait      The trait
     * @param level      The trait level
     * @param conditions Optional trait conditions
     * @return Trait instance
     */
    public static ITraitInstance of(DataResource<ITrait> trait, int level, ITraitCondition... conditions) {
        if (trait.isPresent()) {
            return of(trait.get(), level, conditions);
        }
        return lazy(trait.getId(), level, conditions);
    }

    public static TraitInstance of(ITrait trait, int level, ITraitCondition... conditions) {
        return new TraitInstance(trait, level, conditions);
    }

    public static LazyTraitInstance lazy(ResourceLocation traitId, int level, ITraitCondition... conditions) {
        return new LazyTraitInstance(traitId, level, conditions);
    }

    @Override
    public ResourceLocation getTraitId() {
        return trait.getId();
    }

    @Nonnull
    @Override
    public ITrait getTrait() {
        return trait;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Collection<ITraitCondition> getConditions() {
        return conditions;
    }

    public IFormattableTextComponent getDisplayName() {
        IFormattableTextComponent text = this.trait.getDisplayName(this.level).deepCopy();
        if (!conditions.isEmpty()) {
            text.appendString("*");
        }
        return text;
    }

    public void addInformation(List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (!this.trait.showInTooltip(flag)) return;

        // Display name
        IFormattableTextComponent displayName = this.getDisplayName().mergeStyle(TextFormatting.ITALIC);
        tooltip.add(trait.isHidden() ? TextUtil.withColor(displayName, TextFormatting.DARK_GRAY) : displayName);

        // Description (usually not shown)
        if (KeyTracker.isAltDown()) {
            ITextComponent description = TextUtil.withColor(this.trait.getDescription(level), TextFormatting.DARK_GRAY);
            tooltip.add(new StringTextComponent("    ").append(description));
        }
    }

    public static TraitInstance deserialize(JsonObject json) {
        // Trait; this should already exist
        ResourceLocation traitId = new ResourceLocation(JSONUtils.getString(json, "name"));
        ITrait trait = TraitManager.get(traitId);
        if (trait == null) {
            throw new JsonSyntaxException("Unknown trait: " + traitId);
        }

        // Level; clamp to valid values
        int level = MathHelper.clamp(JSONUtils.getInt(json, "level", 1), 1, trait.getMaxLevel());

        // Conditions (if available)
        List<ITraitCondition> conditions = new ArrayList<>();
        if (json.has("conditions")) {
            JsonArray array = json.getAsJsonArray("conditions");
            for (JsonElement j : array) {
                conditions.add(TraitSerializers.deserializeCondition(j.getAsJsonObject()));
            }
        }

        return of(trait, level, conditions.toArray(new ITraitCondition[0]));
    }

    public static TraitInstance read(PacketBuffer buffer) {
        ResourceLocation traitId = buffer.readResourceLocation();
        ITrait trait = TraitManager.get(traitId);
        if (trait == null) {
            throw new IllegalStateException("Unknown trait: " + traitId);
        }
        int level = buffer.readByte();

        ITraitCondition[] conditions = new ITraitCondition[buffer.readByte()];
        for (int i = 0; i < conditions.length; ++i) {
            conditions[i] = TraitSerializers.readCondition(buffer);
        }

        return of(trait, level, conditions);
    }

    public void write(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.getTraitId());
        buffer.writeByte(this.level);
        buffer.writeByte(this.conditions.size());
        this.conditions.forEach(condition -> TraitSerializers.writeCondition(condition, buffer));
    }
}
