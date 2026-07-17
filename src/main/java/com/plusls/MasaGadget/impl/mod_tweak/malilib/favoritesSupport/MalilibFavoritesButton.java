package com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport;

import com.plusls.MasaGadget.impl.gui.MasaGadgetIcons;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetHoverInfo;
import fi.dy.masa.malilib.render.RenderUtils;
import org.jetbrains.annotations.NotNull;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 1.21.11
//$$ import fi.dy.masa.malilib.render.GuiContext;
//#endif
// CHECKSTYLE.ON: ImportOrder

// CHECKSTYLE.OFF: ImportOrder
//#if 1.21.11 > MC && MC > 1.19.4
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

//#if 1.20.1 > MC && MC > 1.15.2
import com.mojang.blaze3d.vertex.PoseStack;
//#endif
// CHECKSTYLE.ON: ImportOrder

import java.util.function.Consumer;

public class MalilibFavoritesButton extends WidgetHoverInfo {
    private final TooltipSupplier tooltipSupplier;
    private final Consumer<Boolean> onPress;
    private final IGuiIcon icon;
    private boolean status;

    private MalilibFavoritesButton(int x, int y, @NotNull IGuiIcon icon, boolean defaultStatus,
                                   Consumer<Boolean> onPress, @NotNull TooltipSupplier tooltipSupplier, Object... args) {
        super(x, y, icon.getWidth(), icon.getHeight(), tooltipSupplier.onTooltip(defaultStatus), args);
        this.status = defaultStatus;
        this.tooltipSupplier = tooltipSupplier;
        this.onPress = onPress;
        this.icon = icon;
    }

    public static @NotNull MalilibFavoritesButton create(int x, int y, boolean defaultStatus,
                                                         Consumer<Boolean> onPress, TooltipSupplier tooltipSupplier) {
        return new MalilibFavoritesButton(x, y, MasaGadgetIcons.FAVORITE, defaultStatus, onPress, tooltipSupplier);
    }

    //#if MC >= 12106
    //$$ @Override
    //$$ public void render(
    //$$         //#if MC >= 1.21.11
    //$$         //$$ GuiContext guiContextOrGuiGraphics,
    //$$         //#else
    //$$         GuiGraphics guiContextOrGuiGraphics,
    //$$         //#endif
    //$$         int mouseX,
    //$$         int mouseY,
    //$$         boolean selected
    //$$ ) {
    //$$     icon.renderAt(guiContextOrGuiGraphics, this.x, this.y, (float) this.zLevel, this.status, this.isMouseOver(mouseX, mouseY));
    //$$
    //$$     if (this.isMouseOver(mouseX, mouseY)) {
    //$$         RenderUtils.drawOutlinedBox(guiContextOrGuiGraphics, this.x, this.y, this.width, this.height, 0x20C0C0C0, -520093697);
    //$$     }
    //$$ }
    //#else
    @Override
    public void render(
            // CHECKSTYLE.OFF: NoWhitespaceBefore
            // CHECKSTYLE.OFF: SeparatorWrap
            int mouseX,
            int mouseY,
            boolean selected
            //#if MC > 11904
            //$$ , GuiGraphics guiGraphicsOrPoseStack
            //#elseif MC > 11502
            , PoseStack guiGraphicsOrPoseStack
            //#endif
            // CHECKSTYLE.ON: SeparatorWrap
            // CHECKSTYLE.ON: NoWhitespaceBefore
    ) {
        RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture(
                // CHECKSTYLE.OFF: NoWhitespaceBefore
                // CHECKSTYLE.OFF: SeparatorWrap
                icon.getTexture()
                //#if MC > 12104
                //$$ , guiGraphicsOrPoseStack
                //#endif
                // CHECKSTYLE.ON: SeparatorWrap
                // CHECKSTYLE.ON: NoWhitespaceBefore
        );
        icon.renderAt(
                // CHECKSTYLE.OFF: NoWhitespaceBefore
                // CHECKSTYLE.OFF: SeparatorWrap
                this.x,
                this.y,
                (float) this.zLevel,
                this.status,
                this.isMouseOver(mouseX, mouseY)
                //#if MC > 12101
                //$$ , guiGraphicsOrPoseStack
                //#endif
                // CHECKSTYLE.ON: SeparatorWrap
                // CHECKSTYLE.ON: NoWhitespaceBefore
        );

        if (this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawOutlinedBox(this.x, this.y, this.width, this.height, 0x20C0C0C0, -520093697);
        }
    }
    //#endif

    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton) {
        this.status = !this.status;
        this.getLines().clear();
        this.setInfoLines(this.tooltipSupplier.onTooltip(this.status));
        this.onPress.accept(this.status);
        return true;
    }

    @FunctionalInterface
    public interface TooltipSupplier {
        String onTooltip(boolean status);
    }
}
