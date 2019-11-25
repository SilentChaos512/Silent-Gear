package net.silentchaos512.gear.compat.mineandslash;

import com.robertx22.mine_and_slash.api.MineAndSlashAPI;
import net.silentchaos512.gear.SilentGear;

public final class MineAndSlashCompat {
    private MineAndSlashCompat() {throw new IllegalAccessError("Utility class");}

    public static void init() {
        SilentGear.LOGGER.info("Mine and Slash compatibility is enabled!");
        MineAndSlashAPI.addCompatibleItem("silentgear:dagger", new SGearConfigItem());
        MineAndSlashAPI.addCompatibleItem("silentgear:katana", new SGearConfigItem());
        MineAndSlashAPI.addCompatibleItem("silentgear:machete", new SGearConfigItem());
        MineAndSlashAPI.addCompatibleItem("silentgear:spear", new SGearConfigItem());
        MineAndSlashAPI.addCompatibleItem("silentgear:sword", new SGearConfigItem());
        MineAndSlashAPI.addCompatibleItem("silentgear:bow", new SGearConfigItem().setType("Bow"));
        MineAndSlashAPI.addCompatibleItem("silentgear:crossbow", new SGearConfigItem().setType("Crossbow"));
        MineAndSlashAPI.addCompatibleItem("silentgear:helmet", new SGearConfigItem().setType("Helmet"));
        MineAndSlashAPI.addCompatibleItem("silentgear:chestplate", new SGearConfigItem().setType("Chest"));
        MineAndSlashAPI.addCompatibleItem("silentgear:leggings", new SGearConfigItem().setType("Pants"));
        MineAndSlashAPI.addCompatibleItem("silentgear:boots", new SGearConfigItem().setType("Boots"));
    }
}
