package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
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

    private TraitsCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_traits")
                .then(Commands.literal("describe")
                        .then(Commands.argument("traitID", ResourceLocationArgument.id())
                                .suggests(TRAIT_ID_SUGGESTIONS)
                                .executes(context -> runDescribe(context, ResourceLocationArgument.getId(context, "traitID")))
                        )
                )
                .then(Commands.literal("dump_md")
                        .executes(TraitsCommand::runDumpMd)
                )
                .then(Commands.literal("list")
                        .executes(TraitsCommand::runList)
                ));
    }

    private static int runDescribe(CommandContext<CommandSourceStack> context, ResourceLocation traitId) {
        Trait trait = SgRegistries.TRAIT.get(traitId);
        if (trait == null) {
            context.getSource().sendFailure(Component.translatable("command.silentgear.traits.traitNotFound"));
            return 0;
        }

        context.getSource().sendSuccess(() -> trait.getDisplayName(0), true);
        context.getSource().sendSuccess(() -> trait.getDescription(1), true);
        context.getSource().sendSuccess(() -> Component.translatable("command.silentgear.traits.maxLevel", trait.getMaxLevel()), true);
        context.getSource().sendSuccess(() -> Component.literal("Object: " + trait), true);
        // Effects
        if (!trait.getEffects().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("Effects:"), true);
        }
        for (var effect : trait.getEffects()) {
            context.getSource().sendSuccess(() -> Component.literal("- " + SgRegistries.TRAIT_EFFECT_TYPE.getKey(effect.type())), true);
        }

        return 1;
    }

    private static int runList(CommandContext<CommandSourceStack> context) {
        String listStr = SgRegistries.TRAIT.stream()
                .map(trait -> SgRegistries.TRAIT.getKey(trait).toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendSuccess(() -> Component.literal(listStr), true);
        context.getSource().sendSuccess(() -> Component.literal("Total: " + SgRegistries.TRAIT.keySet().size()), true);

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

            // FIXME: Replace with a list of trait effects
            writer.write("## Trait Effects\n\n");
            writer.write("This part of the command was not coded! Please bug SilentChaos512 to fix it. :)\n");
            /*writer.write("## Trait Types\n\n");
            writer.write("These are trait serializers. You can define custom instances of these types using data packs.\n");
            writer.write("Code for traits and their serializers can be found in `net.silentchaos512.gear.gear.trait`.\n\n");
            writer.write("Note that \"simple\" traits are often used where custom code is required.\n");
            writer.write("They are not especially useful when just defined by a data pack.\n\n");

            for (ITraitSerializer<?> serializer : TraitSerializers.getSerializers()) {
                String typeName = serializer instanceof Trait.Serializer ? ((Trait.Serializer) serializer).getTypeName() : "";
                writer.write("- `" + serializer.getName() + "`");
                if (!typeName.isEmpty()) {
                    writer.write(" _(" + typeName + ")_");
                }
                writer.write("\n");
            }*/

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
                    writer.write("  - `" + SgRegistries.TRAIT_EFFECT_TYPE.getKey(effect.type()) + "`");
                }

                /*Collection<String> cancelsWithSet = trait.getCancelsWithSet().stream().map(s -> "`" + s + "`").collect(Collectors.toList());
                if (!cancelsWithSet.isEmpty()) {
                    writer.write("- Cancels With: " + String.join(", ", cancelsWithSet) + "\n");
                }*/

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
                    if (inst.getTrait().equals(trait)) {
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
                if (inst.getTrait().equals(trait) && part.isVisible()) {
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
