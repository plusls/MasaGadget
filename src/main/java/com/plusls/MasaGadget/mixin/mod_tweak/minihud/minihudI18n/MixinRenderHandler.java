package com.plusls.MasaGadget.mixin.mod_tweak.minihud.minihudI18n;

import com.google.common.collect.Lists;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.minihud.event.RenderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.i18n.I18n;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Dependencies(require = @Dependency(ModId.minihud))
@Mixin(value = RenderHandler.class, remap = false)
public class MixinRenderHandler {
    @Unique
    private static final Pattern masa_gadget_mod$textPattern = Pattern.compile("[\\w ]+: ");

    @Redirect(
            method = "addLine(Lfi/dy/masa/minihud/config/InfoToggle;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
            )
    )
    private String patchFormat(String format, Object[] args) {
        return Configs.minihudI18n.getBooleanValue() ? I18n.tr(format, args) : String.format(format, args);
    }

    @ModifyVariable(
            method = "addLine(Ljava/lang/String;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private String patchLine(String string) {
        if (!Configs.minihudI18n.getBooleanValue()) {
            return string;
        }

        Matcher matcher = masa_gadget_mod$textPattern.matcher(string);
        int start = 0;
        List<String> keys = Lists.newArrayList();

        while (matcher.find(start)) {
            keys.add(matcher.group());
            start = matcher.end();
        }

        for (String key : keys) {
            string = string.replace(key, I18n.tr(key));
        }

        return string;
    }
}