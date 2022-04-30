package com.plusls.MasaGadget.mixin.malilib.showOriginalConfigName;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {
    private GuiConfigsBase.ConfigOptionWrapper mg_getMaxNameLength_wrapped;

    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    @ModifyVariable(method = "getMaxNameLengthWrapped", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I", ordinal = 0), ordinal = 0)
    private GuiConfigsBase.ConfigOptionWrapper getWrapper(GuiConfigsBase.ConfigOptionWrapper value) {
        this.mg_getMaxNameLength_wrapped = value;
        return value;
    }

    @ModifyVariable(method = "getMaxNameLengthWrapped", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Math;max(II)I", ordinal = 0), ordinal = 0)
    private int getWrapper(int width) {
        if (Configs.showOriginalConfigName) {
            String displayName = MiscUtil.getStringWithoutFormat(Objects.requireNonNull(mg_getMaxNameLength_wrapped.getConfig()).getConfigGuiDisplayName());
            String name = mg_getMaxNameLength_wrapped.getConfig().getName();
            if (!displayName.equals(name)) {
                width = Math.max(width, (int) Math.ceil(this.getStringWidth(name) * Configs.showOriginalConfigNameScale));
            }
        }
        return width;
    }
}
