package com.plusls.MasaGadget.impl.gui;

import com.plusls.MasaGadget.SharedConstants;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.resources.ResourceLocation;

//#if MC > 12101
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

public enum MasaGadgetIcons implements IGuiIcon {
    FAVORITE(0, 0, 16, 16, 16, 16);

    public static final ResourceLocation TEXTURE = SharedConstants.id("textures/gui/gui_widgets.png");

    private final int u;
    private final int v;
    private final int w;
    private final int h;
    private final int hoverOffU;
    private final int hoverOffV;

    MasaGadgetIcons(int u, int v, int w, int h, int hoverOffU, int hoverOffV) {
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
        this.hoverOffU = hoverOffU;
        this.hoverOffV = hoverOffV;
    }

    @Override
    public int getWidth() {
        return this.w;
    }

    @Override
    public int getHeight() {
        return this.h;
    }

    @Override
    public int getU() {
        return this.u;
    }

    @Override
    public int getV() {
        return this.v;
    }

    @Override
    public void renderAt(
            int x,
            int y,
            float zLevel,
            boolean enabled,
            boolean selected
            //#if MC > 12101
            //$$ , GuiGraphics guiGraphics
            //#endif
    ) {
        int u = this.u;
        int v = this.v;

        if (selected) {
            u += this.hoverOffU;
        }

        if (!enabled) {
            v += this.hoverOffV;
        }

        RenderUtils.drawTexturedRect(
            //#if MC > 12104
            //$$ this.getTexture(),
            //#endif
            x,
            y,
            u,
            v,
            this.w,
            this.h,
            zLevel
            //#if MC > 12104
            //$$ , guiGraphics
            //#endif
        );
    }

    @Override
    public ResourceLocation getTexture() {
        return MasaGadgetIcons.TEXTURE;
    }
}