package com.plusls.MasaGadget.mixin.accessor;

import net.minecraft.client.gui.GuiComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//#if MC > 11502
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

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
