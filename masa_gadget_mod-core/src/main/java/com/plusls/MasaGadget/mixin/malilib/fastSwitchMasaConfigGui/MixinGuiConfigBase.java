package com.plusls.MasaGadget.mixin.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.gui.MyWidgetDropDownList;
import com.plusls.MasaGadget.malilib.fastSwitchMasaConfigGui.MasaGuiUtil;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.MODMENU_MOD_ID))
@Mixin(value = GuiConfigsBase.class, remap = false)
public abstract class MixinGuiConfigBase extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements IKeybindConfigGui {

    @Shadow
    protected Screen parentScreen;
    private WidgetDropDownList<ConfigScreenFactory<?>> masaModGuiList;

    protected MixinGuiConfigBase(int listX, int listY) {
        super(listX, listY);
    }


    @Inject(method = "initGui", at = @At(value = "RETURN"))
    public void postInitGui(CallbackInfo ci) {

        // 在其他地方初始化会导致其它 mod 爆炸
        MasaGuiUtil.initMasaModScreenList();

        this.masaModGuiList = new MyWidgetDropDownList<>(
                GuiUtils.getScaledWindowWidth() - 155, 13, 130, 18, 200, 10,
                MasaGuiUtil.masaGuiConfigScreenFactorys,
                MasaGuiUtil.masaGuiData::get,
                configScreenFactory -> GuiBase.openGui(configScreenFactory.create(this.parentScreen)),
                configScreenFactory -> Configs.fastSwitchMasaConfigGui);

        masaModGuiList.setSelectedEntry(MasaGuiUtil.masaGuiClassData.get(this.getClass()));
        this.addWidget(masaModGuiList);
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        if (masaModGuiList == null) {
            return;
        }
        masaModGuiList.setX(GuiUtils.getScaledWindowWidth() - 155);
    }

}
