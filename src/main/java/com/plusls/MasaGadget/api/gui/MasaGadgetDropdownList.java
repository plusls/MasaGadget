package com.plusls.MasaGadget.api.gui;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC > 11502
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public interface MasaGadgetDropdownList {
    void masa_gad_get$renderHovered(
            //#if MC > 11904
            //$$ GuiGraphics poseStackOrGuiGraphics,
            //#elseif MC > 11502
            PoseStack poseStackOrGuiGraphics,
            //#endif
            int mouseX,
            int mouseY
    );
}
