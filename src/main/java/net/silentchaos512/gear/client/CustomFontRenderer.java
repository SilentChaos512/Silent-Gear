package net.silentchaos512.gear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

// Copied from somewhere... unused
public class CustomFontRenderer extends FontRenderer {

    public static CustomFontRenderer INSTANCE = new CustomFontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine);

    private boolean dropShadow;
    private int state = 0;
    private int red;
    private int green;
    private int blue;

    public CustomFontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn) {

        super(gameSettingsIn, location, textureManagerIn, true);
    }

    @Nonnull
    @Override
    public List<String> listFormattedStringToWidth(@Nonnull String str, int wrapWidth) {

        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    protected String wrapFormattedStringToWidth(String str, int wrapWidth) {

        int i = this.sizeStringToWidth(str, wrapWidth);

        if (str.length() <= i) {
            return str;
        } else {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == 32 || c0 == 10;
            String s1 = getCustomFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
            return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
        }
    }

    private int sizeStringToWidth(String str, int wrapWidth) {

        int i = str.length();
        int j = 0;
        int k = 0;
        int l = -1;

        for (boolean flag = false; k < i; ++k) {
            char c0 = str.charAt(k);

            switch (c0) {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += this.getCharWidth(c0);

                    if (flag) {
                        ++j;
                    }

                    break;
                case '\u00a7':

                    if (k < i - 1) {
                        ++k;
                        char c1 = str.charAt(k);

                        if (c1 != 'l' && c1 != 'L') {
                            if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
                                flag = false;
                            }
                        } else {
                            flag = true;
                        }
                    }
            }

            if (c0 == '\n') {
                ++k;
                l = k;
                break;
            }

            if (j > wrapWidth) {
                break;
            }
        }

        return k != i && l != -1 && l < k ? l : k;
    }

    private static boolean isFormatColor(char colorChar) {

        return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
    }

    public static String getCustomFormatFromString(String text) {

        String s = "";
        int i = 0;
        int j = text.length();

        while ((i < j - 1)) {
            char c = text.charAt(i);
            // vanilla formatting
            if (c == 167) {

                char c0 = text.charAt(i + 1);

                if (c0 >= 48 && c0 <= 57 || c0 >= 97 && c0 <= 102 || c0 >= 65 && c0 <= 70) {
                    s = "\u00a7" + c0;
                    i++;
                } else if (c0 >= 107 && c0 <= 111 || c0 >= 75 && c0 <= 79 || c0 == 114 || c0 == 82) {
                    s = s + "\u00a7" + c0;
                    i++;
                }
            }
            // custom formatting
            else if ((int) c >= CustomFontColor.MARKER && (int) c <= CustomFontColor.MARKER + 0xFF) {
                s = String.format("%s%s%s", c, text.charAt(i + 1), text.charAt(i + 2));
                i += 2;
            }
            i++;
        }

        return s;
    }

    // @Override
    // public int renderString(@Nonnull String text, float x, float y, int color, boolean dropShadow) {
    //
    // this.dropShadow = dropShadow;
    // return super.renderString(text, x, y, color, dropShadow);
    // }

    @Override
    protected float renderUnicodeChar(char letter, boolean italic) {

        // special color settings through char code
        // we use \u2700 to \u27FF, where the lower byte represents the Hue instance the color
        if ((int) letter >= CustomFontColor.MARKER && (int) letter <= CustomFontColor.MARKER + 0xFF) {
            int value = letter & 0xFF;
            switch (state) {
                case 0:
                    red = value;
                    break;
                case 1:
                    green = value;
                    break;
                case 2:
                    blue = value;
                    break;
                default:
                    this.setColor(1f, 1f, 1f, 1f);
                    return 0;
            }

            state = ++state % 3;

            int color = (red << 16) | (green << 8) | blue | (0xff << 24);
            if ((color & -67108864) == 0) {
                color |= -16777216;
            }

            if (dropShadow) {
                color = (color & 16579836) >> 2 | color & -16777216;
            }

            this.setColor(((color >> 16) & 255) / 255f,
                    ((color >> 8) & 255) / 255f,
                    ((color >> 0) & 255) / 255f,
                    ((color >> 24) & 255) / 255f);
            return 0;
        }

        // invalid sequence encountered
        if (state != 0) {
            state = 0;
            this.setColor(1f, 1f, 1f, 1f);
        }

        return super.renderUnicodeChar(letter, italic);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

        super.onResourceManagerReload(resourceManager);
        setUnicodeFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode() || Minecraft.getMinecraft().gameSettings.forceUnicodeFont);
        setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
    }
}
