package com.plusls.MasaGadget.compat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.gui.MyWidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(value = MyWidgetDropDownList.class, remap = false)
public abstract class MixinMyWidgetDropDownList<T> extends WidgetDropDownList<T> {

    public MixinMyWidgetDropDownList(int x, int y, int width, int height, int maxHeight, int maxVisibleEntries, List<T> entries) {
        super(x, y, width, height, maxHeight, maxVisibleEntries, entries);
    }

    /**
     * @author plusls
     * @reason compat 1.15
     */
    @Overwrite
    public void superRender(int mouseX, int mouseY, boolean selected, PoseStack matrixStack) {
        super.render(mouseX, mouseY, selected);
    }
}
