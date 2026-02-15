package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforgespi.language.IModInfo;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.gear.trait.condition.AndTraitCondition;
import net.silentchaos512.gear.network.payload.server.CommandOutputPayload;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public final class TraitsCommand {
    private static final SuggestionProvider<CommandSourceStack> TRAIT_ID_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(SgRegistries.TRAIT.stream().map(SgRegistries.TRAIT::getKey), builder);
    private static final String TRAITS_DATA_PATH = "https://github.com/SilentChaos512/Silent-Gear/tree/1.21.x/src/generated/resources/data/silentgear/silentgear_traits/";

    private TraitsCommand() {
    }

    /**
     * Creates the subcommand for use with the unified /sgear command.
     */
    public static ArgumentBuilder<CommandSourceStack, ?> createSubcommand() {
        return Commands.literal("traits")
                .executes(ctx -> showHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(buildDescribeArgument())
                .then(buildDumpMdArgument())
                .then(buildListArgument());
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> TextUtil.translate("command", "help.traits.title")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("  /sgear traits list").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.traits.list").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear traits describe <traitID>").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.traits.describe").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear traits dump_md").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.traits.dump_md").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_traits")
                .then(buildDescribeArgument())
                .then(buildDumpMdArgument())
                .then(buildListArgument())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDescribeArgument() {
        return Commands.literal("describe")
                .then(Commands.argument("traitID", ResourceLocationArgument.id())
                        .suggests(TRAIT_ID_SUGGESTIONS)
                        .executes(context -> runDescribe(context, ResourceLocationArgument.getId(context, "traitID")))
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDumpMdArgument() {
        return Commands.literal("dump_md")
                .executes(TraitsCommand::runDumpMd);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildListArgument() {
        return Commands.literal("list")
                .executes(TraitsCommand::runList);
    }

    private static int runDescribe(CommandContext<CommandSourceStack> context, ResourceLocation traitId) {
        Trait trait = SgRegistries.TRAIT.get(traitId);
        if (trait == null) {
            context.getSource().sendFailure(TextUtil.translate("command", "traits.traitNotFound"));
            return 0;
        }

        CommandSourceStack source = context.getSource();

        // Display name as title
        source.sendSuccess(() -> trait.getDisplayName(0)
                .copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);

        // Table separator
        source.sendSuccess(() -> Component.literal("─────────────────────────────────")
                .withStyle(ChatFormatting.DARK_GRAY), true);

        // ID row
        source.sendSuccess(() -> tableRow("traits.describe.id", traitId.toString()), true);

        // Max Level row
        source.sendSuccess(() -> tableRow("traits.describe.maxLevel", String.valueOf(trait.getMaxLevel())), true);

        // Description row
        String description = trait.getDescription(0).getString();
        if (!description.isEmpty()) {
            source.sendSuccess(() -> tableRow("traits.describe.description", description), true);
        }

        // Effects section
        if (!trait.getEffects().isEmpty()) {
            source.sendSuccess(() -> Component.literal("─────────────────────────────────")
                    .withStyle(ChatFormatting.DARK_GRAY), true);
            source.sendSuccess(() -> TextUtil.translate("command", "traits.describe.effectsHeader")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE), true);

            for (var effect : trait.getEffects()) {
                String effectDesc = effect.type().getWikiDescription();
                source.sendSuccess(() -> Component.literal("  " + effectDesc)
                        .withStyle(ChatFormatting.WHITE), true);
            }
        }

        return 1;
    }

    private static Component tableRow(String labelKey, String value) {
        return TextUtil.translate("command", labelKey)
                .withStyle(ChatFormatting.AQUA)
                .append(Component.literal(value)
                        .withStyle(ChatFormatting.WHITE));
    }

    private static int runList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        var traits = SgRegistries.TRAIT.keySet();

        // Header
        source.sendSuccess(() -> TextUtil.translate("command", "traits.list.header")
                .withStyle(ChatFormatting.GOLD), true);

        // List
        String listStr = traits.stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(", "));
        source.sendSuccess(() -> Component.literal(listStr), true);

        // Total
        source.sendSuccess(() -> TextUtil.translate("command", "traits.list.total", traits.size())
                .withStyle(ChatFormatting.GRAY), true);

        return 1;
    }

    private static int runDumpMd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        SilentGear.LOGGER.info("Send traits wiki dump packet to client {}", player.getScoreboardName());
        CommandOutputPayload message = CommandOutputPayload.traits();
        PacketDistributor.sendToPlayer(player, message);
        return 1;
    }

    public static void runDumpMdClient() {
        Player player = SilentGear.PROXY.getClientPlayer();
        if (player == null) {
            SilentGear.LOGGER.error("TraitsCommand#runDumpMcClient: player is null?");
            return;
        }

        String fileName = "traits_list.md";
        String dirPath = "output/silentgear";
        File output = new File(dirPath, fileName);
        File directory = output.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            player.sendSystemMessage(Component.literal("Could not create directory: " + output.getParent()));
            return;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            writer.write("# Traits\n\n");
            writer.write("Generated in-game by `sgear_traits dump_md` command on " + getCurrentDateTime() + "\n\n");
            writer.write("This data may or may not be accurate depending on the mod pack you are playing and the mods or data packs installed.\n\n");

            writer.write("## Data Sources\n\n");
            writer.write("The following mods and data packs have added traits to the output. Running the dump command yourself may produce different results.\n\n");
            writer.write(getDataSources() + "\n");

            writer.write("## Trait Effects\n\n");
            writer.write("""
                    Traits can be assigned any number of effects. Each effect type has its own codec (JSON structure)\
                     and associated code that causes the trait it is assigned to to do specific things. Mods could\
                     potentially add new effect types. This is a list of all effect types registered in this modded\
                     instance:
                    """);
            for (TraitEffectType<?> type : SgRegistries.TRAIT_EFFECT_TYPE) {
                writer.write(String.format("- `%s` - %s\n", SgRegistries.TRAIT_EFFECT_TYPE.getKey(type), type.getWikiDescription()));
            }

            writer.write("\n## List of Traits");

            List<ResourceLocation> ids = new ArrayList<>(SgRegistries.TRAIT.keySet());
            ids.sort(Comparator.comparing(id -> Objects.requireNonNull(SgRegistries.TRAIT.get(id)).getDisplayName(0).getString()));

            for (ResourceLocation id : ids) {
                Trait trait = SgRegistries.TRAIT.get(id);
                assert trait != null;

                writer.write("\n");
                writer.write("### " + getLinkToBuiltinTraitJson(id, trait.getDisplayName(0).getString()) + "\n");
                writer.write("- " + trait.getDescription(0).getString() + "\n");
                String materialsWithTrait = getMaterialsWithTrait(trait);
                writer.write("- Found On:\n  - Materials: " + (materialsWithTrait.isEmpty() ? "Nothing" : materialsWithTrait) + "\n");
                String partsWithTrait = getPartsWithTrait(trait);
                if (!partsWithTrait.isEmpty()) {
                    writer.write("  - Parts: " + partsWithTrait + "\n");
                }

                if (!trait.getConditions().isEmpty()) {
                    // Just wrap all of them inside an AND condition, since that's how the logic works anyway
                    AndTraitCondition condition = new AndTraitCondition(trait.getConditions().toArray(new ITraitCondition[0]));
                    writer.write("- Conditions: " + condition.getDisplayText().getString() + "\n");
                }

                writer.write("- ID: `" + id + "`\n");
                writer.write("- Max Level: " + trait.getMaxLevel() + "\n");
                if (!trait.getEffects().isEmpty()) {
                    writer.write("- Effects:\n");
                }
                for (var effect : trait.getEffects()) {
                    writer.write("  - `" + SgRegistries.TRAIT_EFFECT_TYPE.getKey(effect.type()) + "`\n");
                    // TODO: getExtraWikiLines would be better off with some kind of indented list builder
                    //  Consider changing TextListBuilder to have custom bullet settings and pass one in here.
                    //  Maybe create a more flexible IndentedListBuilder that can handle Strings as well?
                    for (String extraWikiLine : effect.getExtraWikiLines()) {
                        writer.write("  " + extraWikiLine + "\n");
                    }
                }

                Collection<String> wikiLines = trait.getExtraWikiLines();
                if (!wikiLines.isEmpty()) {
                    writer.write("- Extra Info:\n");
                    for (String line : wikiLines) {
                        writer.write(line + "\n");
                    }
                }
            }

            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Component fileNameText = (Component.literal(output.getAbsolutePath())).withStyle(ChatFormatting.UNDERLINE).withStyle(style ->
                    style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, output.getAbsolutePath())));
            player.sendSystemMessage(Component.literal("Wrote to ").append(fileNameText));
        }
    }

    private static String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return formatter.format(now);
    }

    private static String getLinkToBuiltinTraitJson(ResourceLocation traitId, String text) {
        if (SilentGear.MOD_ID.equals(traitId.getNamespace())) {
            return String.format("[%s](%s)", text, TRAITS_DATA_PATH + traitId.getPath() + ".json");
        }
        return text;
    }

    private static String getMaterialsWithTrait(Trait trait) {
        StringBuilder str = new StringBuilder();
        boolean foundAny = false;

        for (Material material : SgRegistries.MATERIAL.getValues(false)) {
            MaterialInstance instance = MaterialInstance.of(material);
            Collection<PartType> typesWithTrait = new ArrayList<>();

            for (PartType partType : SgRegistries.PART_TYPE) {
                Collection<TraitInstance> traits = instance.getTraits(PartGearKey.of(GearTypes.ALL.get(), partType));

                for (TraitInstance inst : traits) {
                    if (inst.getTraitId().equals(SgRegistries.TRAIT.getKey(trait))) {
                        typesWithTrait.add(partType);
                        break;
                    }
                }
            }

            if (!typesWithTrait.isEmpty()) {
                if (foundAny) {
                    str.append(", ");
                }
                foundAny = true;

                str.append("**")
                        .append(instance.getDisplayName(PartTypes.MAIN.get()).getString())
                        .append("**")
                        .append(" _(")
                        .append(typesWithTrait.stream().map(pt ->
                                pt.getDisplayName().getString()).collect(Collectors.joining(", ")))
                        .append(")_");
            }
        }

        return str.toString();
    }

    private static String getPartsWithTrait(Trait trait) {
        StringBuilder str = new StringBuilder();
        boolean foundAny = false;

        for (GearPart part : SgRegistries.PART) {
            PartInstance partData = PartInstance.of(part);
            for (TraitInstance inst : partData.getTraits(PartGearKey.of(GearTypes.ALL, PartTypes.MAIN))) {
                if (inst.isValid() && inst.getTrait().equals(trait) && part.isVisible()) {
                    if (foundAny) {
                        str.append(", ");
                    }
                    foundAny = true;

                    str.append("**").append(partData.getDisplayName(partData.getType()).getString()).append("**");
                }
            }
        }

        return str.toString();
    }

    private static String getDataSources() {
        Set<String> sourceSet = new LinkedHashSet<>();
        for (Trait trait : SgRegistries.TRAIT) {
            sourceSet.add(SgRegistries.TRAIT.getKey(trait).getNamespace());
        }

        StringBuilder ret = new StringBuilder();
        for (String id : sourceSet) {
            ret.append("- ");
            Optional<? extends ModContainer> container = ModList.get().getModContainerById(id);
            if (container.isPresent()) {
                IModInfo modInfo = container.get().getModInfo();
                ret.append(modInfo.getDisplayName())
                        .append(" (")
                        .append(id)
                        .append(") ")
                        .append(modInfo.getVersion())
                        .append("\n");
            } else {
                ret.append(id);
            }
        }

        return ret.toString();
    }
}
