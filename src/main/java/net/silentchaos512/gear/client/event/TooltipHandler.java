package net.silentchaos512.gear.client.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.block.charger.ChargerBlockEntity;
import net.silentchaos512.gear.block.grader.GraderBlockEntity;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.tooltip.GearComponentTooltips;
import net.silentchaos512.gear.client.tooltip.MaterialTooltips;
import net.silentchaos512.gear.client.tooltip.PartTooltips;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.AbstractGearPart;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.lib.util.Color;

import java.util.ArrayList;
import java.util.List;

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
        if (part != null) {
            onPartTooltip(event, part);
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
        boolean propertiesKeyHeld = KeyTracker.isDisplayPropertiesDown();

        if (event.getFlags().isAdvanced()) {
            MaterialTooltips.advancedInfo(event.getToolTip(), material);
        }

        if (!Config.Client.showMaterialTooltips.get()) {
            return;
        }

        MaterialTooltips.materialHeader(event.getToolTip(), material, propertiesKeyHeld);

        MaterialTooltips.materialModifierLines(event.getToolTip(), material);

        if (propertiesKeyHeld) {
            MaterialTooltips.materialCategories(event.getToolTip(), material);

            List<PartType> partTypes = GearComponentTooltips.getSortedPartTypes(material.getPartTypes());
            if (!partTypes.isEmpty()) {
                int index = KeyTracker.getMaterialCycleIndex(partTypes.size());
                PartType selectedPartType = partTypes.get(index);
                MaterialTooltips.partTypesPagesHeader(event.getToolTip(), partTypes, selectedPartType);

                MaterialTooltips.propertiesHeader(event.getToolTip(), material);
                MaterialTooltips.propertiesLines(event.getToolTip(), event.getFlags().isAdvanced(), true, FormatColorScheme.LIGHT, selectedPartType, material);
            }
        } else if (event.getFlags().isAdvanced()) {
            MaterialTooltips.addJeiSearchTerms(event.getToolTip(), material);
        }
    }

    private static void onPartTooltip(ItemTooltipEvent event, PartInstance part) {
        if (event.getFlags().isAdvanced()) {
            event.getToolTip().add(Component.literal("* Part ID: " + part.getId()).withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.literal("* Part data pack: " + part.get().getPackName()).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (!Config.Client.showPartTooltips.get()) {
            return;
        }

        // Type
        event.getToolTip().add(TextUtil.withColor(part.getType().getDisplayName(), Color.AQUAMARINE));

        // Traits
        /*List<TraitInstance> traits = new ArrayList<>();
        for (TraitInstance traitInstance : part.getTraits(PartGearKey.of(part.getGearType(), part.getType()))) {
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
        }*/

        // Properties
        if (KeyTracker.isDisplayPropertiesDown()) {
            event.getToolTip().add(Component.translatable("misc.silentgear.tooltip.properties")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(" (Silent Gear)")
                            .withStyle(ChatFormatting.RESET)
                            .withStyle(ChatFormatting.ITALIC)));
            getPartStatLines(event, part);
        } else {
            event.getToolTip().add(TextUtil.withColor(TextUtil.misc("tooltip.properties"), Color.GOLD)
                    .append(Component.literal(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_PROPERTIES), ChatFormatting.GRAY))));
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

    private static void getPartStatLines(ItemTooltipEvent event, PartInstance part) {
        GearType gearType = getPartGearType(part);
        TextListBuilder builder = new TextListBuilder();

        for (GearProperty<?, ?> property : getPartRelevantProperties(part, gearType)) {
            var modifiers = new ArrayList<GearPropertyValue<?>>(part.getPropertyModifiers(part.getType(), PropertyKey.of(property, gearType)));
            PartTooltips.propertyLine(event.getFlags().isAdvanced(), true, FormatColorScheme.LIGHT, part.getGearType(), property, modifiers).ifPresent(builder::add);
        }
        event.getToolTip().addAll(builder.build());
    }

    private static Iterable<GearProperty<?, ?>> getPartRelevantProperties(PartInstance part, GearType gearType) {
        // Narrow down properties for main parts, display everything for other part types
        if (part.getType().is(PartTypes.MAIN)) {
            return GearPropertyGroups.getSortedRelevantProperties(gearType.relevantPropertyGroups());
        }
        return SgRegistries.GEAR_PROPERTY;
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
}
