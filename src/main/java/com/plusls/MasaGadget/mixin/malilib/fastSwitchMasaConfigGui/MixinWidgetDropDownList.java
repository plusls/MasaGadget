package com.plusls.MasaGadget.mixin.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.gui.MyWidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;
import top.hendrixshen.magiclib.util.MiscUtil;

/*
 * Modified from TweakerMore
 * https://github.com/Fallen-Breath/tweakermore/blob/stable/src/main/java/me/fallenbreath/tweakermore/mixins/core/gui/WidgetDropDownListMixin.java
 */
@Dependencies(and = @Dependency(ModInfo.MODMENU_MOD_ID))
@Mixin(value = WidgetDropDownList.class, remap = false)
public class MixinWidgetDropDownList {
    @SuppressWarnings({"ConstantConditions", "PointlessBitwiseExpression"})
    @ModifyArgs(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/render/RenderUtils;drawRect(IIIII)V",
                    remap = false
            ),
            remap = false
    )
    private void selectorDropDownListMakeOpaque(Args args) {
        if (MiscUtil.cast(this) instanceof MyWidgetDropDownList<?>) {
            // ensure background is opaque
            int bgColor = args.get(4);
            int a = (bgColor >> 24) & 0xFF;
            bgColor = (0xFF << 24) | (a << 16) | (a << 8) | (a << 0);
            args.set(4, bgColor);

            // show left box border
            args.set(0, (int)args.get(0) + 1);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(
            method = "onMouseScrolledImpl",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/gui/GuiScrollBar;offsetValue(I)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void fixNoReturnValueHandlingForScroll(CallbackInfoReturnable<Boolean> cir) {
        if (MiscUtil.cast(this) instanceof MyWidgetDropDownList<?>) {
            cir.setReturnValue(true);
        }
    }
}
