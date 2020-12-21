package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.NetworkDirection;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.SimpleTrait;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.network.ClientOutputCommandPacket;
import net.silentchaos512.gear.network.Network;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public final class TraitsCommand {
    private static final SuggestionProvider<CommandSource> TRAIT_ID_SUGGESTIONS = (ctx, builder) ->
            ISuggestionProvider.func_212476_a(TraitManager.getValues().stream().map(ITrait::getId), builder);
    private static final String TRAITS_DATA_PATH = "https://github.com/SilentChaos512/Silent-Gear/tree/1.16.x/src/generated/resources/data/silentgear/silentgear_traits/";

    private TraitsCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sgear_traits")
                .then(Commands.literal("describe")
                        .then(Commands.argument("traitID", ResourceLocationArgument.resourceLocation())
                                .suggests(TRAIT_ID_SUGGESTIONS)
                                .executes(context -> runDescribe(context, ResourceLocationArgument.getResourceLocation(context, "traitID")))
                        )
                )
                .then(Commands.literal("dump_md")
                        .executes(TraitsCommand::runDumpMd)
                )
                .then(Commands.literal("list")
                        .executes(TraitsCommand::runList)
                ));
    }

    private static int runDescribe(CommandContext<CommandSource> context, ResourceLocation traitId) {
        ITrait trait = TraitManager.get(traitId);
        if (trait == null) {
            context.getSource().sendErrorMessage(new TranslationTextComponent("command.silentgear.traits.traitNotFound", traitId));
            return 0;
        }

        context.getSource().sendFeedback(trait.getDisplayName(0), true);
        context.getSource().sendFeedback(trait.getDescription(1), true);
        context.getSource().sendFeedback(new TranslationTextComponent("command.silentgear.traits.maxLevel", trait.getMaxLevel()), true);
        context.getSource().sendFeedback(new StringTextComponent("Object: " + trait), true);
        context.getSource().sendFeedback(new StringTextComponent("Serializer: " + trait.getSerializer()), true);

        return 1;
    }

    private static int runList(CommandContext<CommandSource> context) {
        String listStr = TraitManager.getValues().stream()
                .map(trait -> trait.getId().toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendFeedback(new StringTextComponent(listStr), true);
        context.getSource().sendFeedback(new StringTextComponent("Total: " + TraitManager.getValues().size()), true);

        return 1;
    }

    private static int runDumpMd(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        SilentGear.LOGGER.info("Send traits wiki dump packet to client {}", player.getScoreboardName());
        ClientOutputCommandPacket message = new ClientOutputCommandPacket(ClientOutputCommandPacket.Type.TRAITS, true);
        Network.channel.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        return 1;
    }

    public static void runDumpMdClient() {
        PlayerEntity player = SilentGear.PROXY.getClientPlayer();
        if (player == null) {
            SilentGear.LOGGER.error("TraitsCommand#runDumpMcClient: player is null?");
            return;
        }

        String fileName = "traits_list.md";
        String dirPath = "output/silentgear";
        File output = new File(dirPath, fileName);
        File directory = output.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            player.sendMessage(new StringTextComponent("Could not create directory: " + output.getParent()), Util.DUMMY_UUID);
            return;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            writer.write("# Traits\n\n");
            writer.write("Generated in-game by `sgear_traits dump_md` command on " + getCurrentDateTime() + "\n\n");
            writer.write("This data may or may not be accurate depending on the mod pack you are playing and the mods or data packs installed.\n\n");

            writer.write("## Data Sources\n\n");
            writer.write("The following mods and data packs have added traits to the output. Running the dump command yourself may produce different results.\n\n");
            writer.write(getDataSources() + "\n");

            writer.write("## Trait Types\n\n");
            writer.write("These are trait serializers. You can define custom instances of these types using data packs.\n");
            writer.write("Code for traits and their serializers can be found in `net.silentchaos512.gear.gear.trait`.\n\n");
            writer.write("Note that \"simple\" traits are often used where custom code is required.\n");
            writer.write("They are not especially useful when just defined by a data pack.\n\n");

            for (ITraitSerializer<?> serializer : TraitSerializers.getSerializers()) {
                String typeName = serializer instanceof SimpleTrait.Serializer ? ((SimpleTrait.Serializer) serializer).getTypeName() : "";
                writer.write("- `" + serializer.getName() + "`");
                if (!typeName.isEmpty()) {
                    writer.write(" _(" + typeName + ")_");
                }
                writer.write("\n");
            }

            writer.write("\n## List of Traits");

            List<ResourceLocation> ids = new ArrayList<>(TraitManager.getKeys());
            ids.sort(Comparator.comparing(id -> Objects.requireNonNull(TraitManager.get(id)).getDisplayName(0).getString()));

            for (ResourceLocation id : ids) {
                ITrait trait = TraitManager.get(id);
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
                writer.write("- ID: `" + id + "`\n");
                writer.write("- Type: `" + trait.getSerializer().getName() + "`\n");
                writer.write("- Max Level: " + trait.getMaxLevel() + "\n");

                Collection<String> cancelsWithSet = trait.getCancelsWithSet().stream().map(s -> "`" + s + "`").collect(Collectors.toList());
                if (!cancelsWithSet.isEmpty()) {
                    writer.write("- Cancels With: " + String.join(", ", cancelsWithSet) + "\n");
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
            ITextComponent fileNameText = (new StringTextComponent(output.getAbsolutePath())).mergeStyle(TextFormatting.UNDERLINE).modifyStyle(style ->
                    style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, output.getAbsolutePath())));
            player.sendMessage(new StringTextComponent("Wrote to ").append(fileNameText), Util.DUMMY_UUID);
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

    private static String getMaterialsWithTrait(ITrait trait) {
        StringBuilder str = new StringBuilder();
        boolean foundAny = false;

        for (IMaterial material : MaterialManager.getValues(false)) {
            Collection<PartType> typesWithTrait = new ArrayList<>();

            for (PartType partType : PartType.getValues()) {
                Collection<TraitInstance> traits = material.getTraits(partType);

                for (TraitInstance inst : traits) {
                    if (inst.getTrait().equals(trait) && material.isVisible(partType)) {
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
                        .append(material.getDisplayName(PartType.MAIN).getString())
                        .append("**")
                        .append(" _(")
                        .append(typesWithTrait.stream().map(pt ->
                                pt.getDisplayName(0).getString()).collect(Collectors.joining(", ")))
                        .append(")_");
            }
        }

        return str.toString();
    }

    private static String getPartsWithTrait(ITrait trait) {
        StringBuilder str = new StringBuilder();
        boolean foundAny = false;

        for (IGearPart part : PartManager.getValues()) {
            PartData partData = PartData.of(part);
            for (TraitInstance inst : partData.getTraits()) {
                if (inst.getTrait().equals(trait) && part.isVisible()) {
                    if (foundAny) {
                        str.append(", ");
                    }
                    foundAny = true;

                    str.append("**").append(partData.getDisplayName(ItemStack.EMPTY).getString()).append("**");
                }
            }
        }

        return str.toString();
    }

    private static String getDataSources() {
        Set<String> sourceSet = new LinkedHashSet<>();
        for (ITrait trait : TraitManager.getValues()) {
            sourceSet.add(trait.getId().getNamespace());
        }

        StringBuilder ret = new StringBuilder();
        for (String id : sourceSet) {
            ret.append("- ");
            Optional<? extends ModContainer> container = ModList.get().getModContainerById(id);
            if (container.isPresent()) {
                ret.append(container.get().getModInfo().getDisplayName()).append(" (").append(id).append(")\n");
            } else {
                ret.append(id);
            }
        }

        return ret.toString();
    }
}
