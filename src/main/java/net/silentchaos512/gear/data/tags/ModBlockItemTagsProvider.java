package net.silentchaos512.gear.data.tags;

import net.neoforged.neoforge.common.Tags;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.lib.data.tag.LibBlockItemTagsProvider;

public abstract class ModBlockItemTagsProvider extends LibBlockItemTagsProvider {
    @Override
    public void run() {
        // Common

        tag(SgTags.Blocks.ORES_BORT, SgTags.Items.ORES_BORT).addTag(SgTags.Blocks.ORES_BORT);
        tag(SgTags.Blocks.ORES_CRIMSON_IRON, SgTags.Items.ORES_CRIMSON_IRON).addTag(SgTags.Blocks.ORES_CRIMSON_IRON);
        tag(SgTags.Blocks.ORES_AZURE_SILVER, SgTags.Items.ORES_AZURE_SILVER).addTag(SgTags.Blocks.ORES_AZURE_SILVER);
        tag(Tags.Blocks.ORES, Tags.Items.ORES).addTag(Tags.Blocks.ORES);

        // TODO

        // Silent Gear

        tag(SgTags.Blocks.FLUFFY_BLOCKS, SgTags.Items.FLUFFY_BLOCKS).addTag(SgTags.Blocks.FLUFFY_BLOCKS);
        tag(SgTags.Blocks.NETHERWOOD_LOGS, SgTags.Items.NETHERWOOD_LOGS).addTag(SgTags.Blocks.NETHERWOOD_LOGS);
    }
}
