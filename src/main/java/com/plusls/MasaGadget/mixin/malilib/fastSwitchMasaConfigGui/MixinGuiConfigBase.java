package com.plusls.MasaGadget.mixin.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.compat.modmenu.ConfigScreenFactoryCompat;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.gui.MyWidgetDropDownList;
import com.plusls.MasaGadget.malilib.fastSwitchMasaConfigGui.MasaGuiUtil;
import fi.dy.masa.malilib.gui.GuiBase;
//#if MC >= 11903;
import fi.dy.masa.malilib.gui.GuiListBase;
//#else
//$$ import fi.dy.masa.malilib.gui.GuiConfigsBase;
//$$ import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
//$$ import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
//$$ import net.minecraft.client.gui.screens.Screen;
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.util.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.MODMENU_MOD_ID))
//#if MC >= 11903
@Mixin(value = GuiListBase.class, remap = false)
public abstract class MixinGuiConfigBase extends GuiBase {
//#else
//$$ @Mixin(value = GuiConfigsBase.class, remap = false)
//$$ public abstract class MixinGuiConfigBase extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> {
//$$
//$$     @Shadow
//$$     protected Screen parentScreen;
//$$
//$$     protected MixinGuiConfigBase(int listX, int listY) {
//$$         super(listX, listY);
//$$     }
//#endif
    private WidgetDropDownList<ConfigScreenFactoryCompat<?>> masaModGuiList;

    @Inject(method = "initGui", at = @At(value = "RETURN"))
    public void postInitGui(CallbackInfo ci) {
        // 在其他地方初始化会导致其它 mod 爆炸
        MasaGuiUtil.initMasaModScreenList();

        this.masaModGuiList = new MyWidgetDropDownList<>(
                GuiUtils.getScaledWindowWidth() - 155, 13, 130, 18, 200, 10,
                MasaGuiUtil.masaGuiConfigScreenFactorys,
                MasaGuiUtil.masaGuiData::get,
                configScreenFactory -> GuiBase.openGui(configScreenFactory.create(this.getParent())),
                configScreenFactory -> Configs.fastSwitchMasaConfigGui);

        masaModGuiList.setSelectedEntry(MasaGuiUtil.masaGuiClassData.get(this.getClass()));
        this.addWidget(masaModGuiList);
    }

}
