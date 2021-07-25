package net.silentchaos512.gear.gear.trait;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionResult;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleTrait implements ITrait {
    public static final Serializer<SimpleTrait> SERIALIZER = new Serializer<>(Serializer.NAME, SimpleTrait::new);

    private final ResourceLocation objId;
    private final ITraitSerializer<?> serializer;
    int maxLevel;
    ImmutableList<ITraitCondition> conditions = ImmutableList.of();
    Set<String> cancelsWith = new HashSet<>();
    Component displayName;
    Component description;
    boolean hidden;
    final Collection<Component> wikiLines = new ArrayList<>();

    @Deprecated
    public SimpleTrait(ResourceLocation id) {
        this(id, SERIALIZER);
    }

    public SimpleTrait(ResourceLocation id, ITraitSerializer<?> serializer) {
        this.objId = id;
        this.serializer = serializer;
    }

    @Override
    public ResourceLocation getId() {
        return objId;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public Collection<ITraitCondition> getConditions() {
        return conditions;
    }

    @Override
    public boolean willCancelWith(ITrait other) {
        return cancelsWith.contains(other.getId().toString());
    }

    @Override
    public Collection<String> getCancelsWithSet() {
        return Collections.unmodifiableSet(cancelsWith);
    }

    @Override
    public void retainData(@Nullable ITrait oldTrait) {
        if (oldTrait instanceof SimpleTrait) {
            this.wikiLines.addAll(((SimpleTrait) oldTrait).wikiLines);
        }
    }

    @Override
    public MutableComponent getDisplayName(int level) {
        MutableComponent text = displayName.copy();
        if (level > 0 && maxLevel > 1) {
            text.append(" ").append(new TranslatableComponent("enchantment.level." + level));
        }
        return text;
    }

    @Override
    public MutableComponent getDescription(int level) {
        return description.copy();
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public ITraitSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        return baseValue;
    }

    @Override
    public float onDurabilityDamage(TraitActionContext context, int damageTaken) {
        return damageTaken;
    }

    @Override
    public void onGearCrafted(TraitActionContext context) {
    }

    @Override
    public void onRecalculatePre(TraitActionContext context) {
    }

    @Override
    public void onRecalculatePost(TraitActionContext context) {
    }

    @Override
    public float onGetStat(TraitActionContext context, ItemStat stat, float value, float damageRatio) {
        return value;
    }

    @Override
    public void onGetAttributeModifiers(TraitActionContext context, Multimap<Attribute, AttributeModifier> modifiers, String slot) {
    }

    @Deprecated
    @Override
    public void onGetAttributeModifiers(TraitActionContext context, Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlot slot) {
    }

    @Override
    public InteractionResult onItemUse(UseOnContext context, int traitLevel) {
        return InteractionResult.PASS;
    }

    @Override
    public void onItemSwing(ItemStack stack, LivingEntity wielder, int traitLevel) {
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
    }

    @Override
    public ItemStack addLootDrops(TraitActionContext context, ItemStack stack) {
        return ItemStack.EMPTY;
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        return this.wikiLines.stream()
                .map(Component::getString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleTrait that = (SimpleTrait) o;
        return objId.equals(that.objId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objId);
    }

    public static final class Serializer<T extends SimpleTrait> implements ITraitSerializer<T> {
        private static final ResourceLocation NAME = SilentGear.getId("simple_trait");

        private final ResourceLocation serializerId;
        private final Function<ResourceLocation, T> factory;
        @Nullable private final BiConsumer<T, JsonObject> deserializeJson;
        @Nullable private final BiConsumer<T, FriendlyByteBuf> readFromNetwork;
        @Nullable private final BiConsumer<T, FriendlyByteBuf> writeToNetwork;

        public Serializer(ResourceLocation serializerId, Function<ResourceLocation, T> factory) {
            this(serializerId, factory, null, null, null);
        }

        public Serializer(ResourceLocation serializerId,
                          Function<ResourceLocation, T> factory,
                          @Nullable BiConsumer<T, JsonObject> deserializeJson,
                          @Nullable BiConsumer<T, FriendlyByteBuf> readFromNetwork,
                          @Nullable BiConsumer<T, FriendlyByteBuf> writeToNetwork) {
            this.serializerId = serializerId;
            this.factory = factory;
            this.deserializeJson = deserializeJson;
            this.readFromNetwork = readFromNetwork;
            this.writeToNetwork = writeToNetwork;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            T trait = factory.apply(id);
            trait.maxLevel = GsonHelper.getAsInt(json, "max_level", 1);
            trait.displayName = deserializeText(json.get("name"));
            trait.description = deserializeText(json.get("description"));
            trait.hidden = GsonHelper.getAsBoolean(json, "hidden", false);

            if (json.has("conditions")) {
                List<ITraitCondition> conditions = new ArrayList<>();
                JsonArray array = json.getAsJsonArray("conditions");
                for (JsonElement elem : array) {
                    conditions.add(TraitSerializers.deserializeCondition(elem.getAsJsonObject()));
                }
                trait.conditions = ImmutableList.copyOf(conditions);
            }

            if (json.has("cancels_with")) {
                JsonArray array = json.getAsJsonArray("cancels_with");
                for (JsonElement elem : array) {
                    trait.cancelsWith.add(elem.getAsString());
                }
            }

            if (json.has("extra_wiki_lines")) {
                JsonArray array = json.getAsJsonArray("extra_wiki_lines");
                for (JsonElement elem : array) {
                    trait.wikiLines.add(Component.Serializer.fromJson(elem));
                }
            }

            if (deserializeJson != null) {
                deserializeJson.accept(trait, json);
            }

            return trait;
        }

        @Override
        public T read(ResourceLocation id, FriendlyByteBuf buffer) {
            T trait = factory.apply(id);
            trait.maxLevel = buffer.readByte();
            trait.displayName = buffer.readComponent();
            trait.description = buffer.readComponent();
            trait.hidden = buffer.readBoolean();

            ITraitCondition[] conditions = new ITraitCondition[buffer.readByte()];
            for (int i = 0; i < conditions.length; ++i) {
                conditions[i] = TraitSerializers.readCondition(buffer);
            }
            trait.conditions = ImmutableList.copyOf(conditions);

            int cancelsCount = buffer.readVarInt();
            for (int i = 0; i < cancelsCount; ++i) {
                trait.cancelsWith.add(buffer.readUtf(255));
            }

            if (readFromNetwork != null) {
                readFromNetwork.accept(trait, buffer);
            }

            return trait;
        }

        @Override
        public void write(FriendlyByteBuf buffer, T trait) {
            buffer.writeByte(trait.maxLevel);
            buffer.writeComponent(trait.displayName);
            buffer.writeComponent(trait.description);
            buffer.writeBoolean(trait.hidden);

            buffer.writeByte(trait.conditions.size());
            trait.conditions.forEach(condition -> TraitSerializers.writeCondition(condition, buffer));

            buffer.writeVarInt(trait.cancelsWith.size());
            for (String str : trait.cancelsWith) {
                buffer.writeUtf(str);
            }

            if (writeToNetwork != null) {
                writeToNetwork.accept(trait, buffer);
            }
        }

        @Override
        public ResourceLocation getName() {
            return serializerId;
        }

        private static Component deserializeText(JsonElement json) {
            // Handle the old style
            if (json.isJsonObject() && json.getAsJsonObject().has("name")) {
                boolean translate = GsonHelper.getAsBoolean(json.getAsJsonObject(), "translate", false);
                String name = GsonHelper.getAsString(json.getAsJsonObject(), "name");
                return translate ? new TranslatableComponent(name) : new TextComponent(name);
            }

            // Deserialize use vanilla serializer
            return Objects.requireNonNull(Component.Serializer.fromJson(json));
        }

        private static Component readTextComponent(JsonObject json, String name) {
            JsonElement element = json.get(name);
            if (element != null && element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                final boolean translate = GsonHelper.getAsBoolean(obj, "translate", false);
                final String value = GsonHelper.getAsString(obj, "name");
                return translate
                        ? new TranslatableComponent(value)
                        : new TextComponent(value);
            } else if (element != null) {
                throw new JsonParseException("Expected '" + name + "' to be an object");
            } else {
                throw new JsonParseException("Missing required object '" + name + "'");
            }
        }

        public String getTypeName() {
            T temp = this.factory.apply(new ResourceLocation("null"));
            return temp.getClass().getCanonicalName();
        }
    }
}
