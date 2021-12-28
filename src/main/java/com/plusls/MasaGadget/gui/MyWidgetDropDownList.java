package com.plusls.MasaGadget.gui;

import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.interfaces.IStringRetriever;
import fi.dy.masa.malilib.render.RenderUtils;
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
            RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.push();
            matrixStack.translate(0.0, 0.0, 1.0);
            List<T> list = this.filteredEntries;
            int visibleEntries = Math.min(this.maxVisibleEntries, list.size());
            RenderUtils.drawOutlinedBox(this.x + 1, this.y, this.width - 2, this.height - 1, -15724528, -4144960);
            String str = this.getDisplayString(this.getSelectedEntry());
            int txtX = this.x + 4;
            int txtY = this.y + this.height / 2 - this.fontHeight / 2;
            this.drawString(txtX, txtY, -2039584, str, matrixStack);
            txtY += this.height + 1;
            int scrollWidth = 10;
            if (this.isOpen) {
                if (!this.searchBar.getTextField().getText().isEmpty()) {
                    this.searchBar.draw(mouseX, mouseY, matrixStack);
                }

                int y = this.y + this.height + 1;
                int startIndex = Math.max(0, this.scrollBar.getValue());
                int max = Math.min(startIndex + this.maxVisibleEntries, list.size());

                for (int i = startIndex; i < max; ++i) {
                    int bg = (i & 1) != 0 ? 0xff4e4e4e : 0xff6e6e6e;
                    if (mouseX >= this.x && mouseX < this.x + this.width - scrollWidth && mouseY >= y && mouseY < y + this.height) {
                        bg = 0xff888888;
                    }

                    RenderUtils.drawRect(this.x, y, this.width - scrollWidth, this.height, bg);
                    str = this.getDisplayString((T) list.get(i));
                    this.drawString(txtX, txtY, -2039584, str, matrixStack);
                    y += this.height;
                    txtY += this.height;
                }
                RenderUtils.drawOutline(this.x, this.y + this.height, this.width, visibleEntries * this.height + 2, -2039584);

                int x = this.x + this.width - this.scrollbarWidth - 1;
                y = this.y + this.height + 1;
                int h = visibleEntries * this.height;
                int totalHeight = Math.max(h, list.size() * this.height);
                this.scrollBar.render(mouseX, mouseY, 0.0F, x, y, this.scrollbarWidth, h, totalHeight);
            } else {
                this.bindTexture(MaLiLibIcons.TEXTURE);
                MaLiLibIcons i = MaLiLibIcons.ARROW_DOWN;
                RenderUtils.drawTexturedRect(this.x + this.width - 16, this.y + 2, i.getU() + i.getWidth(), i.getV(), i.getWidth(), i.getHeight());
            }

            matrixStack.pop();
        }
    }

}