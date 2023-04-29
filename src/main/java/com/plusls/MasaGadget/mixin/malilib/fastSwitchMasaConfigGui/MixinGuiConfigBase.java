package com.plusls.MasaGadget.mixin.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.gui.IDropdownRenderer;
import com.plusls.MasaGadget.gui.MyWidgetDropDownList;
import com.plusls.MasaGadget.malilib.fastSwitchMasaConfigGui.MasaGuiUtil;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.util.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.compat.modmenu.ModMenuCompatApi;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Dependencies(and = @Dependency(ModInfo.MODMENU_MOD_ID))
@Mixin(value = GuiConfigsBase.class, remap = false, priority = 1100)
public abstract class MixinGuiConfigBase extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements IDropdownRenderer {
    protected MixinGuiConfigBase(int listX, int listY) {
        super(listX, listY);
    }

    private WidgetDropDownList<ModMenuCompatApi.ConfigScreenFactoryCompat<?>> masa_gadget$masaModGuiList;

    @SuppressWarnings({"MixinAnnotationTarget" ,"UnresolvedMixinReference"})
    @Inject(
            method = "initGui",
            at = @At(
                    value = "RETURN"
            )
    )
    public void postInitGui(CallbackInfo ci) {
        // 在其他地方初始化会导致其它 mod 爆炸
        MasaGuiUtil.initMasaModScreenList();
        this.masa_gadget$masaModGuiList = new MyWidgetDropDownList<>(
                GuiUtils.getScaledWindowWidth() - 155, 13, 130, 18, 200, 10,
                MasaGuiUtil.masaGuiConfigScreenFactorys,
                MasaGuiUtil.masaGuiData::get,
                configScreenFactory -> GuiBase.openGui(configScreenFactory.create(this.getParent())),
                configScreenFactory -> Configs.fastSwitchMasaConfigGui);
        this.masa_gadget$masaModGuiList.setSelectedEntry(MasaGuiUtil.masaGuiClassData.get(this.getClass()));

        this.addWidget(this.masa_gadget$masaModGuiList);
    }

    @Override
    //#if MC > 11904
    //$$ public void masa_gad_get$renderHovered(GuiGraphics gui, int mouseX, int mouseY) {
    //#elseif MC > 11502
    public void masa_gad_get$renderHovered(PoseStack poseStack, int mouseX, int mouseY) {
    //#else
    //$$ public void masa_gad_get$renderHovered(int mouseX, int mouseY) {
    //#endif
        if (this.masa_gadget$masaModGuiList == null) {
            return;
        }
        //#if MC > 11904
        //$$ this.masa_gadget$masaModGuiList.render(mouseX, mouseY, false, gui);
        //#elseif MC > 11502
        this.masa_gadget$masaModGuiList.render(mouseX, mouseY, false, poseStack);
        //#else
        //$$ this.masa_gadget$masaModGuiList.render(mouseX, mouseY, false);
        //#endif
        if (this.masa_gadget$masaModGuiList.isMouseOver(mouseX, mouseY)) {
            this.hoveredWidget = this.masa_gadget$masaModGuiList;
        }
        //#if MC > 11904
        //$$ this.drawHoveredWidget(mouseX, mouseY, gui);
        //#elseif MC > 11502
        this.drawHoveredWidget(mouseX, mouseY, poseStack);
        //#else
        //$$ this.drawHoveredWidget(mouseX, mouseY);
        //#endif
    }
}
