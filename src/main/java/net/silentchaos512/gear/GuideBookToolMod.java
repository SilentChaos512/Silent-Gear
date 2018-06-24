package net.silentchaos512.gear;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.chapter.GuideChapter;
import net.silentchaos512.lib.guidebook.entry.GuideEntry;
import net.silentchaos512.lib.guidebook.page.PageTextOnly;

public class GuideBookToolMod extends GuideBook {

    private GuideEntry entryTest;

    public GuideBookToolMod() {

        super(SilentGear.MOD_ID);
        // TODO: textures?

        edition = SilentGear.BUILD_NUM;
    }

    @Override
    public void initEntries() {

        entryTest = new GuideEntry(this, "test").setImportant();
    }

    @Override
    public void initChapters() {

        new GuideChapter(this, "test", entryTest, new ItemStack(ModItems.blueprint), 0,
                new PageTextOnly(this, 1),
                new PageTextOnly(this, 2),
                new PageTextOnly(this, 3),
                new PageTextOnly(this, 4));
    }

    private static final String[] QUOTES = {
            "Test"
    };

    @Override
    public String[] getQuotes() {

        return QUOTES;
    }

    @Override
    public GuiScreen getConfigScreen(GuiScreen parent) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GuiScreen getAchievementScreen(GuiScreen parent) {

        // TODO Auto-generated method stub
        return null;
    }

}
