package com.plusls.MasaGadget.mixin.malilib.showOriginalConfigName;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.gui.ScalableWidgetLabel;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WidgetConfigOption.class, remap = false)
public abstract class MixinWidgetConfigOption extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper> {

    public MixinWidgetConfigOption(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @Inject(method = "addConfigOption", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addLabel(IIIII[Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void myAddLable(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci) {
        String displayName = MiscUtil.getStringWithoutFormat(config.getConfigGuiDisplayName());
        if (Configs.Malilib.SHOW_ORIGINAL_CONFIG_NAME.getBooleanValue() && !displayName.equals(config.getName())) {
            if (this.subWidgets.get(this.subWidgets.size() - 1).getClass() == WidgetLabel.class) {
                this.subWidgets.remove(this.subWidgets.size() - 1);
                this.addLabel(x, y, width, height, -1, config.getConfigGuiDisplayName(), "");
                ScalableWidgetLabel label = new ScalableWidgetLabel(x, y + 7, width, height, -1, 0.65f, String.format("§7%s§r", config.getName()));
                this.addWidget(label);
            }
        }
    }
}
