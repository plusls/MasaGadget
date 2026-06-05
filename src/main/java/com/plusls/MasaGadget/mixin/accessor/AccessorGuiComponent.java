package com.plusls.MasaGadget.mixin.accessor;

import net.minecraft.client.gui.GuiComponent;

// CHECKSTYLE.OFF: ImportOrder
//#if MC > 11502
import com.mojang.blaze3d.vertex.PoseStack;
//#endif
// CHECKSTYLE.ON: ImportOrder

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc1.19.4: subproject 1.16.5 (main project)        &lt;--------</li>
 * <li>mc1.20+          : subproject 1.20.1 [dummy]</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Mixin(GuiComponent.class)
public interface AccessorGuiComponent {
    @Invoker("fillGradient")
    void masa_gadget_mod$fillGradient(
            //#if MC > 11502
            PoseStack poseStack,
            //#endif
            int startX,
            int startY,
            int endX,
            int endY,
            int colorStart,
            int colorEnd
    );
}
