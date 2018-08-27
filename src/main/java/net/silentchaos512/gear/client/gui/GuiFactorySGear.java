/*
 * Silent Gear -- GuiFactorySGear
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

package net.silentchaos512.gear.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuiFactorySGear implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new GuiConfigSGear(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public static class GuiConfigSGear extends GuiConfig {
        GuiConfigSGear(GuiScreen parent) {
            super(parent, getAllElements(), SilentGear.MOD_ID, false, false, SilentGear.MOD_NAME + " Config");
        }

        private static List<IConfigElement> getAllElements() {
            List<IConfigElement> list = new ArrayList<>();
            Set<String> categories = Config.INSTANCE.getConfiguration().getCategoryNames();

            for (String s : categories)
                if (!s.contains("."))
                    list.add(new DummyConfigElement.DummyCategoryElement(s, "config." + SilentGear.MOD_ID + "." + s,
                            new ConfigElement(Config.INSTANCE.getCategory(s)).getChildElements()));

            return list;
        }
    }
}
