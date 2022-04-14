package com.plusls.MasaGadget.gui;

import com.plusls.MasaGadget.ModInfo;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.resources.ResourceLocation;

public enum MasaGadgetIcons implements IGuiIcon {
    FAVORITE(0, 0, 16, 16, 16, 16);

    public static final ResourceLocation TEXTURE = ModInfo.id("textures/gui/gui_widgets.png");

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
    public void renderAt(int x, int y, float zLevel, boolean enabled, boolean selected) {
        int u = this.u;
        int v = this.v;

        if (selected) {
            u += this.hoverOffU;
        }

        if (!enabled) {
            v += this.hoverOffV;
        }

        RenderUtils.drawTexturedRect(x, y, u, v, this.w, this.h, zLevel);
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }
}