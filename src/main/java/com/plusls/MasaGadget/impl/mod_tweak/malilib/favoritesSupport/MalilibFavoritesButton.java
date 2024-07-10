package com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport;

import com.plusls.MasaGadget.impl.gui.MasaGadgetIcons;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetHoverInfo;
import fi.dy.masa.malilib.render.RenderUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC > 11502
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

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

    @Override
    public void render(
            int mouseX,
            int mouseY,
            boolean selected
            //#if MC > 11904
            //$$ , GuiGraphics gui
            //#elseif MC > 11502
            , PoseStack poseStack
            //#endif
    ) {
        RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture(icon.getTexture());
        icon.renderAt(this.x, this.y, (float) this.zLevel, this.status, this.isMouseOver(mouseX, mouseY));

        if (this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawOutlinedBox(this.x, this.y, this.width, this.height, 0x20C0C0C0, -520093697);
        }
    }

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
