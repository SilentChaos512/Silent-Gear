package net.silentchaos512.gear.api.traits;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.TooltipFlag;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TraitInstance implements ITraitInstance {
    private final DataResource<ITrait> trait;
    private final int level;
    private final ImmutableList<ITraitCondition> conditions;

    private TraitInstance(ITrait trait, int level, ITraitCondition... conditions) {
        this(DataResource.trait(trait.getId()), level, conditions);
    }

    private TraitInstance(DataResource<ITrait> trait, int level, ITraitCondition... conditions) {
        this.trait = trait;
        this.level = level;
        this.conditions = ImmutableList.<ITraitCondition>builder()
                .add(trait.isPresent() ? trait.get().getConditions().toArray(new ITraitCondition[0]) : new ITraitCondition[0])
                .add(conditions)
                .build();
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
        return trait.get();
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Collection<ITraitCondition> getConditions() {
        return conditions;
    }

    public MutableComponent getDisplayName() {
        MutableComponent text = this.trait.get().getDisplayName(this.level).copy();
        if (!conditions.isEmpty()) {
            text.append("*");
        }
        return text;
    }

    public void addInformation(List<Component> tooltip, TooltipFlag flag) {
        if (!this.trait.get().showInTooltip(flag)) return;

        // Display name
        MutableComponent displayName = this.getDisplayName().withStyle(ChatFormatting.ITALIC);
        tooltip.add(trait.get().isHidden() ? TextUtil.withColor(displayName, ChatFormatting.DARK_GRAY) : displayName);

        // Description (usually not shown)
        if (KeyTracker.isAltDown()) {
            Component description = TextUtil.withColor(this.trait.get().getDescription(level), ChatFormatting.DARK_GRAY);
            tooltip.add(Component.literal("    ").append(description));
        }
    }

    public static TraitInstance deserialize(JsonObject json) {
        // Trait; this should already exist
        ResourceLocation traitId = new ResourceLocation(GsonHelper.getAsString(json, "name"));
        ITrait trait = TraitManager.get(traitId);
        if (trait == null) {
            throw new JsonSyntaxException("Unknown trait: " + traitId);
        }

        // Level; clamp to valid values
        int level = Mth.clamp(GsonHelper.getAsInt(json, "level", 1), 1, trait.getMaxLevel());

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

    public static TraitInstance read(FriendlyByteBuf buffer) {
        DataResource<ITrait> trait = DataResource.trait(buffer.readResourceLocation());
        int level = buffer.readByte();

        ITraitCondition[] conditions = new ITraitCondition[buffer.readByte()];
        for (int i = 0; i < conditions.length; ++i) {
            conditions[i] = TraitSerializers.readCondition(buffer);
        }

        return new TraitInstance(trait, level, conditions);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.getTraitId());
        buffer.writeByte(this.level);
        buffer.writeByte(this.conditions.size());
        this.conditions.forEach(condition -> TraitSerializers.writeCondition(condition, buffer));
    }
}
