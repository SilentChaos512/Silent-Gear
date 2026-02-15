package net.silentchaos512.gear.client.gui.book;

import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.NumberPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.gui.book.page.SectionBuilder;
import net.silentchaos512.gear.client.gui.book.page.element.MaterialEntryPageElement;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MaterialListBookScreen extends AbstractMaterialBookScreen {
    public static final Comparator<Material> MATERIAL_SORT_BY_DISPLAY_NAME = (m1, m2) -> {
        var partType = PartTypes.MAIN.get();
        var name1 = m1.getSimpleName().getString().toLowerCase(Locale.ROOT);
        var name2 = m2.getSimpleName().getString().toLowerCase(Locale.ROOT);
        return name1.compareTo(name2);
    };
    public static final Comparator<Material> MATERIAL_SORT_BY_ID = (m1, m2) -> {
        var name1 = SgRegistries.MATERIAL.getKey(m1).toString();
        var name2 = SgRegistries.MATERIAL.getKey(m2).toString();
        return name1.compareTo(name2);
    };

    public MaterialListBookScreen(@Nullable MaterialBookScreen previousScreen, Component title, List<Material> materials, Comparator<Material> materialSortMethod) {
        super(previousScreen, title, createPages(materials, materialSortMethod));
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

    public static Comparator<Material> compareByProperty(GearProperty<Float, NumberPropertyValue> property) {
        return (m1, m2) -> {
            float val1 = m1.getProperty(MaterialInstance.of(m1), PartTypes.MAIN, PropertyKey.of(property, GearTypes.ALL.get()));
            float val2 = m2.getProperty(MaterialInstance.of(m2), PartTypes.MAIN, PropertyKey.of(property, GearTypes.ALL.get()));
            return Float.compare(val2, val1);
        };
    }
}
