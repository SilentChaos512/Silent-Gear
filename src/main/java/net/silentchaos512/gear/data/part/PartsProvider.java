package net.silentchaos512.gear.data.part;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.ToolHeadItem;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.lib.util.NameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class PartsProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public PartsProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String getName() {
        return "Silent Gear - Parts";
    }

    protected Collection<PartBuilder> getParts() {
        Collection<PartBuilder> ret = new ArrayList<>();

        ret.add(part("binding", PartType.BINDING, PartPositions.BINDING, ModItems.BINDING));
        ret.add(part("grip", PartType.GRIP, PartPositions.GRIP, ModItems.GRIP));
        ret.add(part("long_rod", PartType.ROD, PartPositions.ROD, ModItems.LONG_ROD));
        ret.add(part("rod", PartType.ROD, PartPositions.ROD, ModItems.ROD));
        ret.add(part("tip", PartType.TIP, PartPositions.TIP, ModItems.TIP));

        Registration.getItems(ToolHeadItem.class).forEach(item -> {
            PartPositions position = item.getGearType() == GearType.ARMOR ? PartPositions.ARMOR : PartPositions.HEAD;
            ret.add(part(NameUtils.fromItem(item).getPath(), item.getPartType(), position, item));
        });

        return ret;
    }

    private PartBuilder part(String name, PartType partType, PartPositions position, IItemProvider item) {
        return new PartBuilder(SilentGear.getId(name), partType, position, item)
                .name(new TranslationTextComponent("part.silentgear." + name));
    }

    @Override
    public void act(DirectoryCache cache) {
        Path outputFolder = this.generator.getOutputFolder();

        for (PartBuilder builder : getParts()) {
            try {
                String jsonStr = GSON.toJson(builder.serialize());
                String hashStr = HASH_FUNCTION.hashUnencodedChars(jsonStr).toString();
                Path path = outputFolder.resolve(String.format("data/%s/silentgear_parts/%s.json", builder.id.getNamespace(), builder.id.getPath()));
                if (!Objects.equals(cache.getPreviousHash(outputFolder), hashStr) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                        writer.write(jsonStr);
                    }
                }

                cache.recordHash(path, hashStr);
            } catch (IOException ex) {
                LOGGER.error("Could not save parts to {}", outputFolder, ex);
            }
        }
    }
}
