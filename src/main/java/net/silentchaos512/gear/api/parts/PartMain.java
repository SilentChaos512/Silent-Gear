package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import javax.annotation.Nonnull;
import java.util.List;

public final class PartMain extends ItemPart {
    @Deprecated
    public PartMain(ResourceLocation name) {
        super(name, false);
    }

    @Deprecated
    public PartMain(ResourceLocation name, boolean userDefined) {
        super(name, userDefined);
    }

    public PartMain(ResourceLocation name, PartOrigins origin) {
        super(name, origin);
    }

    @Override
    public PartType getType() {
        return PartType.MAIN;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.HEAD;
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String toolClass, IPartPosition position, int animationFrame) {
        String frameStr = "bow".equals(toolClass) && animationFrame == 3 ? "_3" : "";
        String partPosition = position.getTexturePrefix();
        String subtypePrefix = partPosition + (partPosition.isEmpty() ? "" : "_");
        String path = "items/" + toolClass + "/" + subtypePrefix + this.textureSuffix + frameStr;
        return new ResourceLocation(this.registryName.getNamespace(), path);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack stack, String toolClass, int animationFrame) {
        return getTexture(part, stack, toolClass, PartPositions.HEAD, animationFrame);
    }

    @Override
    public ResourceLocation getBrokenTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position) {
        return new ResourceLocation(SilentGear.MOD_ID, "items/" + gearClass + "/_broken");
    }

    @Override
    public void addInformation(ItemPartData part, ItemStack gear, World world, @Nonnull List<String> tooltip, boolean advanced) {
        // Nothing
    }

    @Override
    public String getTypeName() {
        return "main";
    }

    @Override
    public StatInstance.Operation getDefaultStatOperation(ItemStat stat) {
        return stat == CommonItemStats.HARVEST_LEVEL ? StatInstance.Operation.MAX : StatInstance.Operation.AVG;
    }
}
