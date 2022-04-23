package com.plusls.MasaGadget.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetHoverInfo;
import fi.dy.masa.malilib.render.RenderUtils;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class WidgetIconToggleButton extends WidgetHoverInfo {
    protected final TooltipSupplier tooltipSupplier;
    protected final Consumer<Boolean> onPress;
    protected final Predicate<WidgetIconToggleButton> shouldEnable;
    protected final IGuiIcon icon;
    protected boolean status;

    public WidgetIconToggleButton(int x, int y, IGuiIcon icon, boolean defaultStatus,
                                  Consumer<Boolean> onPress, TooltipSupplier tooltipSupplier,
                                  Predicate<WidgetIconToggleButton> shouldEnable, Object... args) {
        super(x, y, icon.getWidth(), icon.getHeight(), tooltipSupplier.onTooltip(defaultStatus), args);
        this.status = defaultStatus;
        this.tooltipSupplier = tooltipSupplier;
        this.onPress = onPress;
        this.shouldEnable = shouldEnable;
        this.icon = icon;
    }


    @Override
    //#if MC > 11502
    public void render(int mouseX, int mouseY, boolean selected, PoseStack matrixStack) {
        //#else
        //$$ public void render(int mouseX, int mouseY, boolean selected) {
        //$$ PoseStack matrixStack = new PoseStack();
        //#endif
        if (!shouldEnable.test(this)) {
            return;
        }
        RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture(icon.getTexture());
        icon.renderAt(this.x, this.y, (float) this.zLevel, this.status, this.isMouseOver(mouseX, mouseY));

        if (this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawOutlinedBox(this.x, this.y, this.width, this.height, 549503168, -520093697);
        }
    }

    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton) {
        if (!shouldEnable.test(this)) {
            return false;
        }
        this.status = !this.status;
        this.getLines().clear();
        this.setInfoLines(this.tooltipSupplier.onTooltip(this.status));
        this.onPress.accept(this.status);
        return true;
    }

    @Override
    //#if MC > 11502
    public void postRenderHovered(int mouseX, int mouseY, boolean selected, PoseStack matrixStack) {
        //#else
        //$$ public void postRenderHovered(int mouseX, int mouseY, boolean selected) {
        //#endif
        if (shouldEnable.test(this)) {
            //#if MC > 11502
            super.postRenderHovered(mouseX, mouseY, selected, matrixStack);
            //#else
            //$$ super.postRenderHovered(mouseX, mouseY, selected);
            //#endif
        }
    }

    public interface TooltipSupplier {
        String onTooltip(boolean status);
    }
}
