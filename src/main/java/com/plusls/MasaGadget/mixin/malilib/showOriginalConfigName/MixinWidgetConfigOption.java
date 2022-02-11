package com.plusls.MasaGadget.mixin.malilib.showOriginalConfigName;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.gui.ScalableWidgetLabel;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WidgetConfigOption.class, remap = false)
public abstract class MixinWidgetConfigOption extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper> {

    private IConfigBase mg_current_config;

    public MixinWidgetConfigOption(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @Inject(method = "addConfigOption", at = @At(value = "HEAD"))
    private void getCurrentConfig(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci) {
        mg_current_config = config;
    }

    @Redirect(method = "addConfigOption", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addLabel(IIIII[Ljava/lang/String;)V", ordinal = 0))
    private void myAddLable(WidgetConfigOption instance, int x, int y, int width, int height, int textColor, String... lines) {
        if (!Configs.Malilib.SHOW_ORIGINAL_CONFIG_NAME.getBooleanValue() ||
                mg_current_config.getConfigGuiDisplayName().equals(mg_current_config.getName())) {
            this.addLabel(x, y, width, height, textColor, mg_current_config.getConfigGuiDisplayName());
        } else {
            this.addLabel(x, y, width, height, textColor, mg_current_config.getConfigGuiDisplayName(), "");
            ScalableWidgetLabel label = new ScalableWidgetLabel(x, y + 7, width, height, textColor, 0.65f, String.format("§7%s§r", mg_current_config.getName()));
            this.addWidget(label);
        }
    }
}
