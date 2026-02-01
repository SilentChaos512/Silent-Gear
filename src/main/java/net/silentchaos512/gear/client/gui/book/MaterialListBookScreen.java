package net.silentchaos512.gear.client.gui.book;

import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.client.gui.book.page.SectionBuilder;
import net.silentchaos512.gear.client.gui.book.page.element.MaterialEntryPageElement;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MaterialListBookScreen extends AbstractMaterialBookScreen {
    public static final Comparator<Material> MATERIAL_SORT_BY_DISPLAY_NAME = (m1, m2) -> {
        var partType = PartTypes.MAIN.get();
        var name1 = m1.getDisplayName(MaterialInstance.of(m1), partType).getString().toLowerCase(Locale.ROOT);
        var name2 = m2.getDisplayName(MaterialInstance.of(m2), partType).getString().toLowerCase(Locale.ROOT);
        return name1.compareTo(name2);
    };
    public static final Comparator<Material> MATERIAL_SORT_BY_ID = (m1, m2) -> {
        var name1 = SgRegistries.MATERIAL.getKey(m1).toString();
        var name2 = SgRegistries.MATERIAL.getKey(m2).toString();
        return name1.compareTo(name2);
    };

    public MaterialListBookScreen(@Nullable MaterialBookScreen previousScreen, List<Material> materials, Comparator<Material> materialSortMethod) {
        super(previousScreen, createPages(materials, materialSortMethod));
    }

    private static SectionBuilder createPages(List<Material> materials, Comparator<Material> sortingMethod) {
        var sortedList = new ArrayList<>(materials);
        sortedList.sort(sortingMethod);

        SectionBuilder builder = new SectionBuilder();
        for (Material material : sortedList) {
            builder.add(new MaterialEntryPageElement(material));
        }
        return builder;
    }
}
