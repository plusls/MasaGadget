package com.plusls.MasaGadget.mixin.mod_tweak.malilib.showOriginalConfigName;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.gui.ScalableWidgetLabel;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.Local;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.Share;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

@Mixin(value = WidgetConfigOption.class, remap = false)
public abstract class MixinWidgetConfigOption extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper> {
    public MixinWidgetConfigOption(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @WrapOperation(
            method = "addConfigOption",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addLabel(IIIII[Ljava/lang/String;)V"
            )
    )
    private void readdBetterLabel(WidgetConfigOption instance, int x, int y, int zLevel, int labelWidth, int configWidth, String[] lines, Operation<Void> original, @Local IConfigBase config, @Share("showOriginalTextsThisTime") LocalBooleanRef showOriginalTextsThisTime) {
        if (!Configs.showOriginalConfigName.getBooleanValue() || lines.length != 1) {
            original.call(instance, x, y, zLevel, labelWidth, configWidth, lines);
            showOriginalTextsThisTime.set(false);
            return;
        }

        String displayName = MiscUtil.getStringWithoutFormat(config.getConfigGuiDisplayName());

        if (displayName.equals(config.getName())) {
            original.call(instance, x, y, zLevel, labelWidth, configWidth, lines);
            showOriginalTextsThisTime.set(false);
            return;
        }

        original.call(instance, x, y, zLevel, labelWidth, configWidth, lines);
        ScalableWidgetLabel label = new ScalableWidgetLabel(x, y + 3, width, height, -1,
                (float) Configs.showOriginalConfigNameScale.getDoubleValue(), config.getName());
        this.addWidget(label);
        showOriginalTextsThisTime.set(true);
    }

    @ModifyArg(
            method = "addConfigOption",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addConfigComment(IIIILjava/lang/String;)V"
            ),
            index = 1
    )
    private int tweakCommentYOffset(int y, @Share("showOriginalTextsThisTime") LocalBooleanRef showOriginalTextsThisTime) {
        if (showOriginalTextsThisTime.get()) {
            y -= 4;
        }

        return y;
    }

    @ModifyArg(
            method = "addConfigOption",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addConfigComment(IIIILjava/lang/String;)V",
                    remap = false
            ),
            index = 3,
            remap = false
    )
    private int tweakCommentHeight(int height, @Share("showOriginalTextsThisTime") LocalBooleanRef showOriginalTextsThisTime) {
        if (showOriginalTextsThisTime.get()) {
            height += 6;
        }

        return height;
    }
}
