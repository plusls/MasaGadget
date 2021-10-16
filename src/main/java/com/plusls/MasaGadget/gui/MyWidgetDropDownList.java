package com.plusls.MasaGadget.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.interfaces.IStringRetriever;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class MyWidgetDropDownList<T> extends WidgetDropDownList<T> {
    private final Consumer<T> selectedCallback;

    public MyWidgetDropDownList(int x, int y, int width, int height, int maxHeight, int maxVisibleEntries, List<T> entries, @Nullable IStringRetriever<T> stringRetriever, Consumer<T> selectedCallback) {
        super(x, y, width, height, maxHeight, maxVisibleEntries, entries, stringRetriever);
        this.selectedCallback = selectedCallback;
    }

    @Override
    protected void setSelectedEntry(int index) {
        super.setSelectedEntry(index);
        selectedCallback.accept(this.getSelectedEntry());
    }

}