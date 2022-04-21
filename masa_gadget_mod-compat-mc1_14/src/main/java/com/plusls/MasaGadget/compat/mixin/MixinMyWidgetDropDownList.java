package com.plusls.MasaGadget.compat.mixin;

import com.mojang.blaze3d.vertex.PoseStackCompat;
import com.plusls.MasaGadget.gui.MyWidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.compat.annotation.Remap;

import java.util.List;

@Mixin(value = MyWidgetDropDownList.class, remap = false)
public abstract class MixinMyWidgetDropDownList<T> extends WidgetDropDownList<T> {

    public MixinMyWidgetDropDownList(int x, int y, int width, int height, int maxHeight, int maxVisibleEntries, List<T> entries) {
        super(x, y, width, height, maxHeight, maxVisibleEntries, entries);
    }

    // overwrite old
    @Remap("superRender")
    public void superRender(int mouseX, int mouseY, boolean selected, PoseStackCompat matrixStack) {
        super.render(mouseX, mouseY, selected);
    }
}
