package com.plusls.MasaGadget.mixin.malilib.fastSwitchConfig;

import com.plusls.MasaGadget.gui.MyWidgetDropDownList;
import com.plusls.MasaGadget.malilib.fastSwitchConfig.MasaGuiUtil;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GuiConfigsBase.class, remap = false)
public abstract class MixinGuiConfigBase extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements IKeybindConfigGui {

    @Shadow
    protected Screen parentScreen;

    protected MixinGuiConfigBase(int listX, int listY) {
        super(listX, listY);
    }

    private final MyWidgetDropDownList<ConfigScreenFactory<?>> masaModGuiList = new MyWidgetDropDownList<>(GuiUtils.getScaledWindowWidth() - 145, 10, 120, 18, 200, 10,
            MasaGuiUtil.masaGuiData.keySet().stream().toList(), MasaGuiUtil.masaGuiData::get,
            configScreenFactory -> GuiBase.openGui(configScreenFactory.create(this.parentScreen)));


    @Override
    public void init() {
        super.init();
        masaModGuiList.setSelectedEntry(MasaGuiUtil.masaGuiClassData.get(this.getClass()));
        this.addWidget(masaModGuiList);
    }

    @Override
    public void resize(MinecraftClient mc, int width, int height) {
        super.resize(mc, width, height);
        masaModGuiList.setX(GuiUtils.getScaledWindowWidth() - 125);
    }

}
