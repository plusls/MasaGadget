package com.plusls.MasaGadget.mixin.mod_tweak.malilib.pinyinSouSuo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.pinyinSouSuo.PinInHelper;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WidgetListBase.class, remap = false)
public class MixinWidgetListBase {
    @WrapOperation(
            method = "matchesFilter(Ljava/lang/String;Ljava/lang/String;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;indexOf(Ljava/lang/String;)I"
            )
    )
    private int patchMatchLogic(String instance, String str, Operation<Integer> original) {
        int ret = original.call(instance, str);

        if (Configs.pinyinSouSuo.getBooleanValue() && PinInHelper.getInstance().contains(instance, str)) {
            ret += 1;
        }

        return ret;
    }
}
