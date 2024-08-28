package net.silentchaos512.gear.gear.trait.effect;

import com.google.common.primitives.Floats;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.*;
import java.util.function.Supplier;

public class AttributeTraitEffect extends TraitEffect {
    public static final MapCodec<AttributeTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.unboundedMap(Key.CODEC, Codec.list(ModifierData.CODEC))
                            .fieldOf("attribute_modifiers")
                            .forGetter(e -> e.modifiers)
            ).apply(instance, AttributeTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    Key.STREAM_CODEC,
                    ModifierData.STREAM_CODEC.apply(ByteBufCodecs.list())
            ), e -> e.modifiers,
            AttributeTraitEffect::new
    );

    private final Map<Key, List<ModifierData>> modifiers = new HashMap<>();

    public AttributeTraitEffect(Map<Key, List<ModifierData>> map) {
        this.modifiers.putAll(map);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.ATTRIBUTE.get();
    }

    @Override
    public void onGetAttributeModifiers(TraitActionContext context, ItemAttributeModifiers.Builder builder) {
        int traitLevel = context.traitLevel();
        for (Map.Entry<Key, List<ModifierData>> entry : this.modifiers.entrySet()) {
            var key = entry.getKey();
            List<ModifierData> mods = entry.getValue();

            if (gearMatchesKey(context.gear(), key)) {
                for (ModifierData mod : mods) {
                    float modValue = mod.values.get(Mth.clamp(traitLevel - 1, 0, mod.values.size() - 1));
                    builder.add(
                            mod.attribute,
                            new AttributeModifier(
                                    mod.getModId(key, context),
                                    modValue,
                                    mod.operation
                            ),
                            key.group
                    );
                }
            }
        }
    }

    private static boolean gearMatchesKey(ItemStack gear, Key key) {
        GearType gearType = GearHelper.getType(gear);
        return gearType.matches(key.gearType());
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        this.modifiers.forEach((type, list) -> {
            ret.add("  - " + type);
            list.forEach(mod -> {
                ret.add("    - " + mod.getWikiLine());
            });
        });
        return ret;
    }

    public static class ModifierData {
        public static final Codec<ModifierData> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(d -> d.attribute),
                        Codec.list(Codec.FLOAT).fieldOf("values").forGetter(d -> d.values),
                        AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(d -> d.operation)
                ).apply(instance, ModifierData::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ModifierData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE), d -> d.attribute,
                ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()), d -> d.values,
                AttributeModifier.Operation.STREAM_CODEC, d -> d.operation,
                ModifierData::new
        );

        private final Holder<Attribute> attribute;
        private final List<Float> values;
        private final AttributeModifier.Operation operation;

        public ModifierData(Holder<Attribute> attribute, List<Float> values, AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.values = values;
            this.operation = operation;
        }

        @SuppressWarnings("TypeMayBeWeakened")
        public static ModifierData of(Holder<Attribute> attribute, AttributeModifier.Operation operation, Float... values) {
            return new ModifierData(attribute, Arrays.stream(values).toList(), operation);
        }

        public ResourceLocation getModId(Key key, TraitActionContext context) {
            var itemId = BuiltInRegistries.ITEM.getKey(context.gear().getItem());
            var primaryPart = GearData.getConstruction(context.gear()).getPrimaryPart();
            var primaryMaterial = primaryPart != null ? primaryPart.getPrimaryMaterial() : null;
            return ResourceLocation.fromNamespaceAndPath(
                    itemId.getNamespace(),
                    itemId.getPath()
                            + "/" + key.group.getSerializedName()
                            + (primaryMaterial != null ? "/" + primaryMaterial.getId() : "")
            );
        }

        private String getWikiLine() {
            String[] valueText = new String[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                valueText[i] = Float.toString(values.get(i));
            }
            var name = BuiltInRegistries.ATTRIBUTE.getKey(this.attribute.value());
            return name + ": " + operation.name() + " [" + String.join(", ", valueText) + "]";
        }
    }

    public record Key(
            GearType gearType,
            EquipmentSlotGroup group
    ) {
        public static final Codec<Key> CODEC = Codec.STRING
                .comapFlatMap(Key::tryParseKey, Key::key)
                .stable();
        public static final StreamCodec<RegistryFriendlyByteBuf, Key> STREAM_CODEC = StreamCodec.of(
                (buf, val) -> buf.writeUtf(val.key()),
                buf -> tryParseKey(buf.readUtf()).getOrThrow()
        );

        public static Key of(GearType gearType, EquipmentSlotGroup group) {
            return new Key(gearType, group);
        }

        private String key() {
            return SilentGear.shortenId(SgRegistries.GEAR_TYPE.getKey(gearType))
                    + "/"
                    + group.getSerializedName();
        }

        private static DataResult<Key> tryParseKey(String str) {
            var split = str.split("/");
            if (split.length != 2) {
                return DataResult.error(() -> "Invalid key: " + str);
            }
            var gearTypeId = SilentGear.getIdWithDefaultNamespace(split[0]);
            var gearType = SgRegistries.GEAR_TYPE.get(gearTypeId);
            if (gearType == null || gearType == GearTypes.NONE.get()) {
                return DataResult.error(() -> "Unknown gear type: " + gearTypeId);
            }
            var nameLookup = StringRepresentable.createNameLookup(EquipmentSlotGroup.values(), s -> s);
            var group = nameLookup.apply(split[1]);
            return DataResult.success(new Key(gearType, group));
        }
    }

    public static class Builder {
        private final Map<Key, List<ModifierData>> mods = new LinkedHashMap<>();

        public Builder add(Supplier<GearType> gearType, EquipmentSlotGroup group, Holder<Attribute> attribute, AttributeModifier.Operation operation, float... values) {
            this.mods.computeIfAbsent(Key.of(gearType.get(), group), k -> new ArrayList<>())
                    .add(new ModifierData(attribute, Floats.asList(values), operation));
            return this;
        }

        public Builder addAnySlot(Supplier<GearType> gearType, Holder<Attribute> attribute, AttributeModifier.Operation operation, float... values) {
            return add(gearType, EquipmentSlotGroup.ANY, attribute, operation, values);
        }

        public Builder addArmorSlots(Holder<Attribute> attribute, AttributeModifier.Operation operation, float... values) {
            add(GearTypes.ARMOR, EquipmentSlotGroup.HEAD, attribute, operation, values);
            add(GearTypes.ARMOR, EquipmentSlotGroup.CHEST, attribute, operation, values);
            add(GearTypes.ARMOR, EquipmentSlotGroup.LEGS, attribute, operation, values);
            add(GearTypes.ARMOR, EquipmentSlotGroup.FEET, attribute, operation, values);
            return this;
        }

        public AttributeTraitEffect build() {
            return new AttributeTraitEffect(this.mods);
        }
    }
}
