package com.plusls.MasaGadget.mixin.mod_tweak.malilib.optimizeConfigWidgetSearch;

import com.google.common.collect.ImmutableList;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.DependencyType;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.platform.PlatformType;

import java.util.List;

@Dependencies(
        require = {
                @Dependency(value = ModId.malilib, versionPredicates = "<0.11.0"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FABRIC_LIKE)
        }
)
@Dependencies(
        require = {
                @Dependency(value = ModId.minecraft, versionPredicates = "<1.18-"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FORGE_LIKE)
        }
)
@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {
    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    @Inject(method = "getEntryStringsForFilter*", at = @At("HEAD"), cancellable = true)
    private void preGetEntryStringsForFilter(GuiConfigsBase.ConfigOptionWrapper entry, CallbackInfoReturnable<List<String>> cir) {
        if (!Configs.optimizeConfigWidgetSearch.getBooleanValue()) {
            return;
        }

        IConfigBase config = entry.getConfig();

        if (config != null) {
            if (config instanceof IConfigResettable && ((IConfigResettable) config).isModified()) {
                cir.setReturnValue(ImmutableList.of(config.getConfigGuiDisplayName().toLowerCase(), config.getName().toLowerCase(), "modified"));
            } else {
                cir.setReturnValue(ImmutableList.of(config.getConfigGuiDisplayName().toLowerCase(), config.getName().toLowerCase()));
            }
        }
    }

    // TODO: Compat
    // Fix upper case when search Disable Hotkeys
    @Override
    protected boolean matchesFilter(List<String> entryStrings, String filterText) {
        if (!Configs.optimizeConfigWidgetSearch.getBooleanValue()) {
            return super.matchesFilter(entryStrings, filterText);
        }

        filterText = filterText.toLowerCase();

        if (filterText.isEmpty()) {
            return true;
        }

        for (String str : entryStrings) {
            if (this.matchesFilter(str, filterText)) {
                return true;
            }
        }

        return false;
    }
}