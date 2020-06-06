package net.silentchaos512.gear.compat.gamestages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.IGearPart;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public final class GameStagesCompatProxy {
    private GameStagesCompatProxy() {throw new IllegalAccessError("Utility class");}

    public static boolean canCraft(IGearPart part, CraftingInventory inv) {
        if (!ModList.get().isLoaded("gamestages")) return true;

        if (FMLEnvironment.dist == Dist.CLIENT) {
            PlayerEntity player = SilentGear.PROXY.getClientPlayer();
            if (player == null || player instanceof FakePlayer) {
                return true;
            }
            return canCraft(part, player);
        }
        return canCraft(part, getPlayerUsingInventory(inv));
    }

    public static boolean canCraft(@Nullable GearType gearType, CraftingInventory inv) {
        if (!ModList.get().isLoaded("gamestages")) return true;

        if (FMLEnvironment.dist == Dist.CLIENT) {
            PlayerEntity player = SilentGear.PROXY.getClientPlayer();
            if (player == null || player instanceof FakePlayer) {
                return true;
            }
            return canCraft(gearType, player);
        }
        return canCraft(gearType, getPlayerUsingInventory(inv));
    }

    public static boolean canCraft(IGearPart part, PlayerEntity player) {
        if (!ModList.get().isLoaded("gamestages")) return true;
        return GameStagesCompat.canCraft(part, player);
    }

    public static boolean canCraft(@Nullable GearType gearType, PlayerEntity player) {
        if (!ModList.get().isLoaded("gamestages") || gearType == null) return true;
        return GameStagesCompat.canCraft(gearType, player);
    }

    @Nullable
    private static ServerPlayerEntity getPlayerUsingInventory(CraftingInventory inv) {
        MinecraftServer server = SilentGear.PROXY.getServer();
        if(server != null) {
            PlayerList manager = server.getPlayerList();
            Field containerField = ObfuscationReflectionHelper.findField(CraftingInventory.class, "eventHandler");
            containerField.setAccessible(true);
            Container container = null;
            try {
                container = (Container) containerField.get(inv);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(container == null) {
                return null;
            }
            ServerPlayerEntity foundPlayer = null;
            for (ServerPlayerEntity player : manager.getPlayers()) {
                if (player.openContainer == container && container.canInteractWith(player) && container.getCanCraft(player)) {
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
