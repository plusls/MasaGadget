package com.plusls.MasaGadget.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.interfaces.IStringRetriever;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MyWidgetDropDownList<T> extends WidgetDropDownList<T> {
    private final Consumer<T> selectedCallback;
    private final Predicate<T> shouldRender;

    public MyWidgetDropDownList(int x, int y, int width, int height, int maxHeight, int maxVisibleEntries,
                                List<T> entries, @Nullable IStringRetriever<T> stringRetriever,
                                Consumer<T> selectedCallback, Predicate<T> shouldRender) {
        super(x, y, width, height, maxHeight, maxVisibleEntries, entries, stringRetriever);
        this.selectedCallback = selectedCallback;
        this.shouldRender = shouldRender;
    }

    @Override
    protected void setSelectedEntry(int index) {
        super.setSelectedEntry(index);
        selectedCallback.accept(this.getSelectedEntry());
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
        if (shouldRender.test(this.getSelectedEntry())) {
            super.render(mouseX, mouseY, selected, matrixStack);
        }
    }

}