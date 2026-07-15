package com.plusls.MasaGadget.api.gui;

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

public interface MasaGadgetDropdownList {
    void masa_gad_get$renderHovered(
            //#if MC >= 1.21.11
            //$$ GuiContext guiContext,
            //#elseif MC > 11904
            //$$ GuiGraphics poseStackOrGuiGraphics,
            //#elseif MC > 11502
            PoseStack poseStackOrGuiGraphics,
            //#endif
            int mouseX,
            int mouseY
    );
}
