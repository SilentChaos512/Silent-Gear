package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public final class ModTags {
    private ModTags() {}

    public static final class Blocks {
        public static final Tag<Block> NETHERWOOD_SOIL = new BlockTags.Wrapper(nameMod("netherwood_soil"));
        public static final Tag<Block> ORES_CRIMSON_IRON = new BlockTags.Wrapper(nameForge("ores/crimson_iron"));

        private Blocks() {}
    }

    public static final class Items {
        public static final Tag<Item> ORES_CRIMSON_IRON = new ItemTags.Wrapper(nameForge("ores/crimson_iron"));

        private Items() {}
    }

    private static ResourceLocation nameForge(String path) {
        return name("forge", path);
    }

    private static ResourceLocation nameMod(String path) {
        return name(SilentGear.MOD_ID, path);
    }

    private static ResourceLocation name(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static void init() {
        // Mostly here so TagGenerator calls are done at the right time.
        // TagGenerator should generate JSON files in dev only.

        // REMOVED: no longer needed
    }
}
