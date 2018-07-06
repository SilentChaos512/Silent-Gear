package net.silentchaos512.gear.command;

import com.google.common.collect.ImmutableList;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;
import java.util.List;

public class CommandSilentGear extends CommandBase {

    enum SubCommand {
        RESET_MODEL_CACHES, REGISTRY_ANALYZE, BREAK_ITEM_IN_HAND, REPAIR_ITEM_IN_HAND;

        @Nullable
        static SubCommand fromArgs(String arg) {
            for (SubCommand val : values())
                if (val.name().equalsIgnoreCase(arg))
                    return val;
            return null;
        }

        static String[] getKeys() {
            String[] ret = new String[values().length];
            for (int i = 0; i < ret.length; ++i)
                ret[i] = values()[i].name().toLowerCase();
            return ret;
        }
    }

    @Override
    public String getName() {
        return SilentGear.MOD_ID;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("sgear");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return TextFormatting.RED + "Usage: /" + getName() + "<reset_model_caches>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length < 1) {
            tell(sender, getUsage(sender), false);
        }

        SubCommand subCommand = SubCommand.fromArgs(args[0]);
        if (subCommand == SubCommand.RESET_MODEL_CACHES) {
            int total = GearClientHelper.modelCache.size();
            GearClientHelper.modelCache.clear();
            tell(sender, "Reset gear model caches, removed " + total + " objects", false);
        } else if (subCommand == SubCommand.REGISTRY_ANALYZE) {
            tell(sender, "PartRegistry.all: " + PartRegistry.getValues().size(), false);
            tell(sender, "PartRegistry.mains: " + PartRegistry.getMains().size(), false);
            tell(sender, "PartRegistry.rods: " + PartRegistry.getRods().size(), false);
            tell(sender, "PartRegistry.visibleMains: " + PartRegistry.getVisibleMains().size(), false);
            tell(sender, "PartRegistry.visibleRods: " + PartRegistry.getVisibleRods().size(), false);
        } else if (subCommand == SubCommand.BREAK_ITEM_IN_HAND && sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof ICoreItem) {
                stack.setItemDamage(stack.getMaxDamage());
                GearData.recalculateStats(stack);
                tell(sender, "(╯°□°）╯︵ ┻━┻", false);
            }
        } else if (subCommand == SubCommand.REPAIR_ITEM_IN_HAND && sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof ICoreItem) {
                stack.setItemDamage(0);
                GearData.recalculateStats(stack);
                tell(sender, "┬─┬ノ( º _ ºノ)", false);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, SubCommand.getKeys());
        else
            return ImmutableList.of();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {

        return args.length > 2 && args[1].equals("get") ? index == 3 : index == 4;
    }

    private void tell(ICommandSender sender, String key, boolean fromLocalizationFile, Object... args) {

        tell(sender, TextFormatting.RESET, key, fromLocalizationFile, args);
    }

    private void tell(ICommandSender sender, TextFormatting format, String key, boolean fromLocalizationFile, Object... args) {

        String value = fromLocalizationFile
                ? SilentGear.localization.getLocalizedString("command." + key, args)
                : key;
        sender.sendMessage(new TextComponentString(format + value));
    }
}
