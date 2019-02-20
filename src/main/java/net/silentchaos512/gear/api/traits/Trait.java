/*
 * Silent Gear -- Trait
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

package net.silentchaos512.gear.api.traits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.lib.ResourceOrigin;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Trait {
    private static final Gson GSON = (new GsonBuilder()).create();

    @Getter private float activationChance;
    @Getter private int maxLevel;
    @Getter private ResourceLocation name;
    @Getter private TextFormatting nameColor = TextFormatting.GRAY;
    @Getter private ResourceOrigin origin = ResourceOrigin.BUILTIN_CORE;
    private final Set<String> cancelsWith = new HashSet<>();

    public Trait(ResourceLocation name, ResourceOrigin origin) {
        this.name = name;
        this.origin = origin;

        if (!this.origin.validate(this.name, SilentGear.MOD_ID)) {
            throw new IllegalArgumentException(String.format("Trait '%s' has origin %s, but should be %s",
                    this.name, this.origin, ResourceOrigin.BUILTIN_ADDON));
        }
    }

    public Trait(ResourceLocation name, int maxLevel, TextFormatting nameColor, float activationChance) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.nameColor = nameColor;
        this.activationChance = activationChance;
    }

    public final int getCanceledLevel(int level, Trait other, int otherLevel) {
        if (willCancelWith(other)) {
            final int diff = level - otherLevel;

            int newLevel;
            if (diff < 0)
                newLevel = MathHelper.clamp(diff, -other.maxLevel, 0);
            else
                newLevel = MathHelper.clamp(diff, 0, this.maxLevel);

            return newLevel;
        }
        return level;
    }

    public final boolean willCancelWith(Trait other) {
        return cancelsWith.contains(other.name.toString());
    }

    @Deprecated
    public String getTranslatedName(int level) {
        return "wrong method!";
    }

    public ITextComponent getDisplayName(int level) {
        ITextComponent textName = new TextComponentTranslation("trait." + name.getNamespace() + "." + name.getPath());
        ITextComponent textLevel = new TextComponentTranslation("enchantment.level." + level);
        return new TextComponentTranslation("trait.silentgear.displayFormat", textName, textLevel);
    }

    @Override
    public String toString() {
        return "Trait{" +
                "name=" + name +
                ", origin=" + origin +
                ", maxLevel=" + maxLevel +
                '}';
    }

    public NBTTagCompound writeToNBT(int level) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("Name", name.toString());
        tagCompound.setByte("Level", (byte) level);
        return tagCompound;
    }

    //region JSON handling

    /**
     * Get the location of the resource file that contains material information
     */
    private String getResourceFileLocation() {
        return String.format("assets/%s/traits/%s.json", this.name.getNamespace(), this.name.getPath());
    }

    void loadJsonResources() {
        // Main resource file in JAR
        String path = getResourceFileLocation();
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
        if (resourceAsStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"))) {
                readResourceFile(reader);
            } catch (Exception e) {
                SilentGear.LOGGER.warn("Error reading trait file '{}'", path);
                SilentGear.LOGGER.catching(e);
            }
        } else if (origin.isBuiltin()) {
            SilentGear.LOGGER.error("Trait '{}' is missing its data file!", this.name);
        }

        // Override in config folder
        /*
        File file = new File(Config.INSTANCE.getDirectory().getPath(), "traits/" + this.name.getPath() + ".json");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            readResourceFile(reader);
        } catch (FileNotFoundException e) {
            // Ignore, overrides are not required
        } catch (Exception e) {
            SilentGear.LOGGER.warn("Error reading trait override '{}'", file.getAbsolutePath());
            SilentGear.LOGGER.catching(e);
        }
        */
    }

    private void readResourceFile(BufferedReader reader) {
        JsonElement je = GSON.fromJson(reader, JsonElement.class);
        JsonObject json = je.getAsJsonObject();
        Loader.processJson(this, json);
        processExtraJson(json);
    }

    protected void processExtraJson(JsonObject json) { }

    //endregion

    //region Handlers

    protected boolean shouldActivate(int level, ItemStack gear) {
        if (activationChance == 0) return false;
        return MathUtils.tryPercentage(activationChance * level);
    }

    public float onAttackEntity(@Nullable EntityPlayer player, EntityLivingBase target, int level, ItemStack gear, float baseValue) {
        return baseValue;
    }

    public float onDurabilityDamage(@Nullable EntityPlayer player, int level, ItemStack gear, int damageTaken) {
        return damageTaken;
    }

    public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value, float damageRatio) {
        return value;
    }

    public void onUpdate(@Nullable EntityPlayer player, int level, ItemStack gear) {}

    //endregion

    private static class Loader {
        private static void processJson(Trait trait, JsonObject json) {
            trait.activationChance = JsonUtils.getFloat(json, "activation_chance", trait.activationChance);
            trait.maxLevel = JsonUtils.getInt(json, "max_level", trait.maxLevel);

            if (json.has("name_color")) {
                TextFormatting format = TextFormatting.getValueByName(JsonUtils.getString(json, "name_color"));
                trait.nameColor = format != null ? format : trait.nameColor;
            }

            processCancelsWithArray(trait, json);
        }

        private static void processCancelsWithArray(Trait trait, JsonObject json) {
            if (json.has("cancels_with")) {
                JsonElement elemCancelsWith = json.get("cancels_with");

                if (elemCancelsWith.isJsonArray()) {
                    trait.cancelsWith.clear();

                    for (JsonElement element : elemCancelsWith.getAsJsonArray()) {
                        if (element.isJsonPrimitive()) {
                            String str = element.getAsString();
                            if (!str.contains(":")) {
                                str = SilentGear.RESOURCE_PREFIX + str;
                            }
                            trait.cancelsWith.add(str);
                        }
                    }
                }
            }
        }
    }
}
