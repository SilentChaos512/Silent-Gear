/*
 * Silent Gear -- GearStatistics
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.silentchaos512.gear.SilentGear;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//@Mod.EventBusSubscriber
public class GearStatistics {
    public static final String BLOCKS_MINED = "silentgear.blocks_mined";

    private static final LoadingCache<UUIDStack, Data> CACHE = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(GearStatistics::onCacheRemoval)
            .build(
                    new CacheLoader<UUIDStack, Data>() {
                        @Override
                        public Data load(@Nonnull UUIDStack key) {
                            return new Data(key);
                        }
                    }
            );

    public static int getStat(ItemStack stack, String statName) {
        try {
            return CACHE.get(UUIDStack.from(stack)).get(statName);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void incrementStat(ItemStack stack, String statName) {
        incrementStat(stack, statName, 1);
    }

    public static void incrementStat(ItemStack stack, String statName, int amount) {
        try {
            CACHE.get(UUIDStack.from(stack)).increment(statName, amount);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void saveToNBT(ItemStack stack, Data data) {
        NBTTagCompound tags = GearData.getStatisticsCompound(stack);
        data.stats.forEach(tags::setInteger);

        // debug
        data.stats.forEach((stat, value) -> SilentGear.log.debug("    {} = {}", stat, value));
    }

    private static void onCacheRemoval(RemovalNotification<UUIDStack, Data> notification) {
        saveToNBT(notification.getKey().stack, notification.getValue());
    }

//    @SubscribeEvent
//    public static void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
//        if (event.player.ticksExisted % 600 == 0) {
//            SilentGear.log.debug("Saving gear statistics for player {}", event.player.getName());
//            for (ItemStack stack : PlayerHelper.getNonEmptyStacks(event.player, stack -> stack.getItem() instanceof ICoreItem)) {
//                try {
//                    Data data = CACHE.get(UUIDStack.from(stack));
//                    if (data != null) saveToNBT(stack, data);
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private static final class Data {
        private final Map<String, Integer> stats = new HashMap<>();

        private Data(UUIDStack key) {
            NBTTagCompound tags = GearData.getStatisticsCompound(key.stack);
            for (String nbtKey : tags.getKeySet()) {
                if (tags.hasKey(nbtKey, 3)) {
                    stats.put(nbtKey, tags.getInteger(nbtKey));
                }
            }
        }

        private boolean has(String stat) {
            return stats.containsKey(stat);
        }

        private int get(String stat) {
            return has(stat) ? stats.get(stat) : 0;
        }

        private void set(String stat, int value) {
            stats.put(stat, value);
            SilentGear.log.debug("Data.set: {} {}", stat, value);
        }

        private void increment(String stat, int amount) {
            set(stat, get(stat) + amount);
        }
    }

    private static final class UUIDStack {
        private final UUID uuid;
        private final ItemStack stack;

        private static UUIDStack from(ItemStack stack) {
            return from(GearData.getUUID(stack), stack);
        }

        private static UUIDStack from(UUID uuid, ItemStack stack) {
            // TODO: cache these?
            return new UUIDStack(uuid, stack);
        }

        private UUIDStack(UUID uuid, ItemStack stack) {
            this.uuid = uuid;
            this.stack = stack;
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UUID)) return false;
            return uuid.equals(obj);
        }
    }

    public static void getDebugText(List<String> lines) {
        lines.add("GearStatistics#CACHE size=" + CACHE.size());
    }
}
