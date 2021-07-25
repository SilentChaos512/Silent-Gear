package net.silentchaos512.gear.compat.gamestages;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IGearPart;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public final class GameStagesCompatProxy {
    private GameStagesCompatProxy() {throw new IllegalAccessError("Utility class");}

    public static boolean canCraft(IGearPart part, CraftingContainer inv) {
        if (!ModList.get().isLoaded("gamestages")) return true;

        if (FMLEnvironment.dist == Dist.CLIENT) {
            Player player = SilentGear.PROXY.getClientPlayer();
            if (player == null || player instanceof FakePlayer) {
                return true;
            }
            return canCraft(part, player);
        }
        return canCraft(part, getPlayerUsingInventory(inv));
    }

    public static boolean canCraft(GearType gearType, CraftingContainer inv) {
        if (!ModList.get().isLoaded("gamestages")) return true;

        if (FMLEnvironment.dist == Dist.CLIENT) {
            Player player = SilentGear.PROXY.getClientPlayer();
            if (player == null || player instanceof FakePlayer) {
                return true;
            }
            return canCraft(gearType, player);
        }
        return canCraft(gearType, getPlayerUsingInventory(inv));
    }

    public static boolean canCraft(IGearPart part, Player player) {
        if (!ModList.get().isLoaded("gamestages")) return true;
        return GameStagesCompat.canCraft(part, player);
    }

    public static boolean canCraft(@Nullable GearType gearType, Player player) {
        if (!ModList.get().isLoaded("gamestages") || gearType == null) return true;
        return GameStagesCompat.canCraft(gearType, player);
    }

    @Nullable
    private static ServerPlayer getPlayerUsingInventory(CraftingContainer inv) {
        MinecraftServer server = SilentGear.PROXY.getServer();
        if(server != null) {
            PlayerList manager = server.getPlayerList();
            Field containerField = ObfuscationReflectionHelper.findField(CraftingContainer.class, "eventHandler");
            containerField.setAccessible(true);
            AbstractContainerMenu container = null;
            try {
                container = (AbstractContainerMenu) containerField.get(inv);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(container == null) {
                return null;
            }
            ServerPlayer foundPlayer = null;
            for (ServerPlayer player : manager.getPlayers()) {
                if (player.containerMenu == container && container.stillValid(player) && container.isSynched(player)) {
                    if (foundPlayer != null) {
                        return null;
                    }
                    foundPlayer = player;
                }
            }
            return foundPlayer;
        }
        return null;
    }
}
