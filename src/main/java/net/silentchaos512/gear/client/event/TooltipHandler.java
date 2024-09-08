package net.silentchaos512.gear.client.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.block.charger.ChargerBlockEntity;
import net.silentchaos512.gear.block.grader.GraderBlockEntity;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.AbstractGearPart;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.lib.util.Color;

import java.util.*;
import java.util.stream.Collectors;

public final class TooltipHandler {
    public static final TooltipHandler INSTANCE = new TooltipHandler();

    // Display a single trait and cycling through the list. Main problem with this is it affects
    // JEI's tooltip cache. When disabled, you can search for parts with certain traits.
    private static final boolean TRAIT_DISPLAY_CYCLE = false;

    static {
        assert ChatFormatting.DARK_GRAY.getColor() != null;
        assert ChatFormatting.GRAY.getColor() != null;
    }

    public static final Color MC_DARK_GRAY = new Color(ChatFormatting.DARK_GRAY.getColor());
    public static final Color MC_GRAY = new Color(ChatFormatting.GRAY.getColor());

    private TooltipHandler() {
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onTooltip(ItemTooltipEvent event) {
        /*
        LoaderState state = Loader.instance().getLoaderState();
        if (state == LoaderState.INITIALIZATION || state == LoaderState.SERVER_ABOUT_TO_START || state == LoaderState.SERVER_STOPPING) {
            // Skip tooltips during block/item remapping
            // JEI tooltip caches are done in AVAILABLE, in-game is SERVER_STARTED
            return;
        }
        */

        ItemStack stack = event.getItemStack();

        if (stack.is(SgTags.Items.GRADER_CATALYSTS)) {
            onGraderCatalystTooltip(event);
        }
        if (stack.is(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS)) {
            onStarlightChargerCatalystTooltip(event);
        }

        MaterialInstance material = MaterialInstance.from(stack);
        if (material != null) {
            onMaterialTooltip(event, stack, material);
            return;
        }

        PartInstance part = PartInstance.from(stack);
        if (part != null /*&& !part.isBlacklisted(stack)*/) {
            onPartTooltip(event, stack, part);
            return;
        }

        // Nerfed gear?
        if (Config.Common.isNerfedItem(stack.getItem())) {
            List<Component> toolTip = event.getToolTip();
            toolTip.add(Math.min(1, toolTip.size()), Component.translatable("misc.silentgear.poorlyMade").withStyle(ChatFormatting.RED));
        }
    }

    private static void onGraderCatalystTooltip(ItemTooltipEvent event) {
        int tier = GraderBlockEntity.getCatalystTier(event.getItemStack());
        event.getToolTip().add(TextUtil.withColor(TextUtil.misc("graderCatalyst", tier), Color.DARKORANGE));
    }

    private static void onStarlightChargerCatalystTooltip(ItemTooltipEvent event) {
        int tier = ChargerBlockEntity.getStarlightChargerCatalystTier(event.getItemStack());
        event.getToolTip().add(TextUtil.withColor(TextUtil.misc("starlightChargerCataylst", tier), Color.REBECCAPURPLE));
    }

    private static void onMaterialTooltip(ItemTooltipEvent event, ItemStack stack, MaterialInstance material) {
        boolean keyHeld = KeyTracker.isDisplayStatsDown();

        if (event.getFlags().isAdvanced()) {
            event.getToolTip().add(Component.literal("Material ID: " + material.getId()).withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.literal("Material data pack: " + material.get().getPackName()).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (!Config.Client.showMaterialTooltips.get()) {
            return;
        }

        if (keyHeld) {
            event.getToolTip().add(TextUtil.withColor(TextUtil.misc("tooltip.material"), Color.GOLD));
        } else {
            event.getToolTip().add(TextUtil.withColor(TextUtil.misc("tooltip.material"), Color.GOLD)
                    .append(Component.literal(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_STATS), ChatFormatting.GRAY))));
        }

        if (keyHeld) {
            getMaterialModifierLines(event, material);

            getMaterialCategoriesLine(material).ifPresent(t -> event.getToolTip().add(t));

            List<PartType> partTypes = new ArrayList<>(material.getPartTypes());
            if (!partTypes.isEmpty()) {
                int index = KeyTracker.getMaterialCycleIndex(partTypes.size());
                PartType partType = partTypes.get(index);
                event.getToolTip().add(buildPartTypeHeader(partTypes, partType));

                getMaterialTraitLines(event, partType, material);

                event.getToolTip().add(Component.translatable("misc.silentgear.tooltip.properties").withStyle(ChatFormatting.GOLD));
                getMaterialStatLines(event, partType, material);
            }
        } else {
            getMaterialModifierLines(event, material);

            if (event.getFlags().isAdvanced()) {
                addJeiSearchTerms(event, material);
            }
        }
    }

    private static void addJeiSearchTerms(ItemTooltipEvent event, MaterialInstance material) {
        // Add search terms to allow advanced filtering in JEI (requires the
        // `SearchAdvancedTooltips` JEI config to be set)

        StringBuilder b = new StringBuilder();

        for (IMaterialCategory category : material.getCategories()) {
            b.append(category.getName()).append(" ");
        }

        Collection<String> traits = new HashSet<>();

        for (PartType partType : material.getPartTypes()) {
            b.append(partType.getDisplayName().getString()).append(" ");
            for (TraitInstance trait : material.getTraits(PartGearKey.ofAll(partType))) {
                traits.add(trait.getTrait().getDisplayName(0).getString());
            }
        }

        for (String str : traits) {
            b.append(str).append(" ");
        }

        event.getToolTip().add(Component.literal(b.toString().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
    }

    private static Optional<Component> getMaterialCategoriesLine(MaterialInstance material) {
        Collection<IMaterialCategory> categories = material.getCategories();
        if (!categories.isEmpty()) {
            Component text = TextUtil.separatedList(categories.stream().map(IMaterialCategory::getDisplayName).collect(Collectors.toList()))
                    .withStyle(ChatFormatting.ITALIC);
            return Optional.of(TextUtil.misc("materialCategories", text));
        }
        return Optional.empty();
    }

    private static Component buildPartTypeHeader(Collection<PartType> types, PartType selectedType) {
        MutableComponent ret = Component.literal("| ").withStyle(ChatFormatting.GRAY);
        for (PartType type : types) {
            Color color = type == selectedType ? Color.AQUAMARINE : MC_DARK_GRAY;
            Component text = TextUtil.withColor(type.getDisplayName(), color);
            ret.append(text).append(" | ");
        }

        Component keyHint = TextUtil.misc("tooltip.material.keyHint",
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_BACK), Color.AQUAMARINE),
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_NEXT), Color.AQUAMARINE));
        return ret.append(keyHint);
    }

    private static void onPartTooltip(ItemTooltipEvent event, ItemStack stack, PartInstance part) {

        if (event.getFlags().isAdvanced() && KeyTracker.isControlDown()) {
            event.getToolTip().add(Component.literal("* Part ID: " + part.getId()).withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.literal("* Part data pack: " + part.get().getPackName()).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (!Config.Client.showPartTooltips.get()) {
            return;
        }

        // Type, tier
        event.getToolTip().add(TextUtil.withColor(part.getType().getDisplayName(), Color.AQUAMARINE));

        // Traits
        List<TraitInstance> traits = new ArrayList<>();
        for (TraitInstance traitInstance : part.getTraits(PartGearKey.ofAll(part.getType()))) {
            if (traitInstance.getTrait().showInTooltip(event.getFlags())) {
                traits.add(traitInstance);
            }
        }

        int numTraits = traits.size();
        int traitIndex = getTraitDisplayIndex(numTraits);
        int i = 0;
        for (TraitInstance inst : traits) {
            if (traitIndex < 0 || traitIndex == i) {
                inst.addInformation(event.getToolTip(), event.getFlags());
            }
            ++i;
        }

        // Stats
        if (KeyTracker.isControlDown()) {
            event.getToolTip().add(Component.translatable("misc.silentgear.tooltip.properties")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(" (Silent Gear)")
                            .withStyle(ChatFormatting.RESET)
                            .withStyle(ChatFormatting.ITALIC)));
            getPartStatLines(event, stack, part);
        } else {
            event.getToolTip().add(Component.translatable("misc.silentgear.tooltip.ctrlForProperties").withStyle(ChatFormatting.GOLD));
        }

        // Gear type blacklist?
        if (part.get() instanceof AbstractGearPart) {
            List<GearType> blacklist = ((AbstractGearPart) part.get()).getBlacklistedGearTypes();
            if (!blacklist.isEmpty()) {
                int index = (ClientTicks.ticksInGame() / 20) % blacklist.size();
                GearType gearType = blacklist.get(index);
                Component blacklistedType = gearType.getDisplayName();
                event.getToolTip().add(Component.translatable("misc.silentgear.tooltip.blacklist", blacklistedType).withStyle(ChatFormatting.RED));
            }
        }
    }

    private static int getTraitDisplayIndex(int numTraits) {
        if (!TRAIT_DISPLAY_CYCLE || KeyTracker.isControlDown() || numTraits == 0)
            return -1;
        return ClientTicks.ticksInGame() / 20 % numTraits;
    }

    private static void getMaterialModifierLines(ItemTooltipEvent event, MaterialInstance material) {
        for (IMaterialModifier modifier : material.getModifiers()) {
            modifier.appendTooltip(event.getToolTip());
        }
    }

    private static void getMaterialTraitLines(ItemTooltipEvent event, PartType partType, MaterialInstance material) {
        Collection<TraitInstance> traits = material.getTraits(PartGearKey.ofAll(partType));
        if (traits.isEmpty()) return;

        MutableComponent header = TextUtil.misc("tooltip.traits").withStyle(ChatFormatting.GOLD);
        if (!KeyTracker.isDisplayTraitsDown()) {
            MutableComponent keyHint = TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_TRAITS), Color.AQUAMARINE);
            header.append(" ").append(keyHint);
        }
        event.getToolTip().add(header);

        TextListBuilder builder = new TextListBuilder();

        for (TraitInstance trait : traits) {
            builder.add(trait.getDisplayName());

            // Trait description and conditions
            if (event.getFlags().isAdvanced() || KeyTracker.isDisplayTraitsDown()) {
                builder.indent();
                builder.add(trait.getTrait().getDescription(trait.getLevel()).withStyle(ChatFormatting.DARK_GRAY));
                if (!trait.getConditions().isEmpty()) {
                    builder.add(TextUtil.withColor(trait.getConditionsText(), ChatFormatting.DARK_GRAY));
                }
                builder.unindent();
            }
        }

        event.getToolTip().addAll(builder.build());
    }

    private static void getPartStatLines(ItemTooltipEvent event, ItemStack stack, PartInstance part) {
        GearType gearType = getPartGearType(part);
        TextListBuilder builder = new TextListBuilder();

        var sortedRelevantProperties = GearPropertyGroups.getSortedRelevantProperties(part.getGearType().relevantPropertyGroups());
        for (GearProperty<?, ?> property : sortedRelevantProperties) {
            Collection<GearPropertyValue<?>> modifiers = new ArrayList<>();
            for (GearPropertyValue<?> mod : part.getPropertyModifiers(part.getType(), PropertyKey.of(property, gearType))) {
//                if (mod.getOp() == StatInstance.Operation.AVG) {
//                    float computed = stat.compute(Collections.singleton(mod));
//                    modifiers.add(StatInstance.of(computed, StatInstance.Operation.AVG, mod.getKey()));
//                } else {
                modifiers.add(mod);
//                }
            }
            getStatTooltipLine(event, part.getType(), property, modifiers).ifPresent(builder::add);
        }
        event.getToolTip().addAll(builder.build());
    }

    private static GearType getPartGearType(PartInstance part) {
        if (part.getItem().getItem() instanceof CompoundPartItem) {
            GearType gearType = ((CompoundPartItem) part.getItem().getItem()).getGearType();

            if (gearType.isGear()) {
                return gearType;
            }
        }
        return GearTypes.ALL.get();
    }

    private static void getMaterialStatLines(ItemTooltipEvent event, PartType partType, MaterialInstance material) {
        TextListBuilder builder = new TextListBuilder();

        for (GearProperty<?, ?> property : SgRegistries.GEAR_PROPERTY) {
            getMaterialStatModLines(event, partType, material, builder, property);
        }

        event.getToolTip().addAll(builder.build());
    }

    private static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> void getMaterialStatModLines(
            ItemTooltipEvent event,
            PartType partType,
            MaterialInstance material,
            TextListBuilder builder,
            P property
    ) {
        Collection<V> modsAll = material.getPropertyModifiers(partType, PropertyKey.of(property, GearTypes.ALL.get()));
        //noinspection unchecked
        Optional<MutableComponent> head = getStatTooltipLine(event, partType, property, (Collection<GearPropertyValue<?>>) modsAll);
        builder.add(head.orElseGet(() -> TextUtil.withColor(property.getDisplayName(), property.getGroup().getColor())));

        builder.indent();

        int subCount = 0;
        List<PropertyKey<?, ?>> keysForStat = material.get().getPropertyKeys(material, partType).stream()
                .filter(key -> key.property().equals(property))
                .toList();

        for (var key : keysForStat) {
            if (key.gearType() != GearTypes.ALL.get()) {
                //noinspection unchecked
                var castedKey = (PropertyKey<T, V>) key;
                Collection<V> mods = material.getPropertyModifiers(partType, castedKey);
                Optional<MutableComponent> line = getSubStatTooltipLine(event, partType, castedKey.property(), key.gearType(), mods);

                if (line.isPresent()) {
                    builder.add(line.get());
                    ++subCount;
                }
            }
        }

        if (subCount == 0 && head.isEmpty()) {
            builder.removeLast();
        }

        builder.unindent();
    }

    @SuppressWarnings("unchecked")
    private static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> Optional<MutableComponent> getStatTooltipLine(
            ItemTooltipEvent event,
            PartType partType,
            GearProperty<?, ?> propertyIn,
            Collection<GearPropertyValue<?>> modifiersIn
    ) {
        if (!modifiersIn.isEmpty()) {
            // Cast to true types
            var property = (P) propertyIn;
            var modifiers = (Collection<V>) modifiersIn;
            T value = property.compute(property.getZeroValue(), modifiers);
            boolean isZero = property.isZero(value);
            if (event.getFlags().isAdvanced() || !isZero) {
                Color nameColor = isZero ? MC_DARK_GRAY : property.getGroup().getColor();
                Color statColor = isZero ? MC_DARK_GRAY : Color.WHITE;

                MutableComponent nameStr = TextUtil.withColor(property.getDisplayName(), nameColor);
                var uncoloredFormattedText = GearPropertyMap.formatText(
                        modifiers,
                        property,
                        property.getPreferredDecimalPlaces(property.valueOf(value))
                );
                MutableComponent statListText = TextUtil.withColor(uncoloredFormattedText, statColor);

                return Optional.of(Component.translatable("property.silentgear.displayFormat", nameStr, statListText));
            }
        }

        return Optional.empty();
    }

    private static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> Optional<MutableComponent> getSubStatTooltipLine(
            ItemTooltipEvent event,
            PartType partType,
            P property,
            GearType gearType,
            Collection<V> modifiers
    ) {
        if (!modifiers.isEmpty()) {
            T value = property.compute(property.getZeroValue(), modifiers);
            boolean isZero = property.isZero(value);
            if (event.getFlags().isAdvanced() || !isZero) {
                Color color = isZero ? MC_DARK_GRAY : Color.WHITE;

                MutableComponent nameStr = TextUtil.withColor(gearType.getDisplayName().copy(), color);
                var uncoloredFormattedText = GearPropertyMap.formatText(
                        modifiers,
                        property,
                        property.getPreferredDecimalPlaces(property.valueOf(value))
                );
                MutableComponent statListText = TextUtil.withColor(uncoloredFormattedText, color);

                return Optional.of(Component.translatable("property.silentgear.displayFormat", nameStr, statListText));
            }
        }

        return Optional.empty();
    }

    public static MutableComponent harvestLevelWithHint(MutableComponent statValueText, float statValue) {
        String key = "misc.silentgear.harvestLevel." + Math.round(statValue);
        if (I18n.exists(key)) {
            return statValueText.append(TextUtil.misc("spaceBrackets", Component.translatable(key)));
        }
        return statValueText;
    }
}
