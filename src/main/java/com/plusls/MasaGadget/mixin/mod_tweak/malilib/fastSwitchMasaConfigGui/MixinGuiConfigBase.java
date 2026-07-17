package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.api.fake.mod_tweak.malilib.favoritesSupport.GuiBaseInjector;
import com.plusls.MasaGadget.api.gui.MasaGadgetDropdownList;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.fastSwitchMasaConfigGui.FastMasaGuiSwitcher;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.interfaces.IStringValue;
import top.hendrixshen.magiclib.api.dependency.DependencyType;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.platform.PlatformType;
import top.hendrixshen.magiclib.impl.malilib.config.gui.SelectorDropDownList;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 1.21.11
//$$ import fi.dy.masa.malilib.render.GuiContext;
//#endif

//#if MC > 12006
//$$ import fi.dy.masa.malilib.gui.widgets.WidgetBase;
//#endif
// CHECKSTYLE.ON: ImportOrder

// CHECKSTYLE.OFF: ImportOrder
//#if 1.21.11 > MC && MC > 1.19.4
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

//#if 1.20.1 > MC && MC > 1.15.2
import com.mojang.blaze3d.vertex.PoseStack;
//#endif
// CHECKSTYLE.ON: ImportOrder

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

// CHECKSTYLE.OFF: ImportOrder
//#if MC > 12006
//$$ import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
//$$ import org.spongepowered.asm.mixin.Dynamic;
//$$ import org.spongepowered.asm.mixin.injection.At;
//#endif
// CHECKSTYLE.ON: ImportOrder

@Dependencies(
        require = {
                @Dependency(ModId.malilib),
                @Dependency(ModId.mod_menu),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FABRIC_LIKE)
        }
)
@Dependencies(require = @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FORGE_LIKE))
@Mixin(value = GuiConfigsBase.class, remap = false)
public abstract class MixinGuiConfigBase extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements MasaGadgetDropdownList, GuiBaseInjector {
    @Unique
    private SelectorDropDownList<IStringValue> masa_gadget$masaModGuiList;

    protected MixinGuiConfigBase(int listX, int listY) {
        super(listX, listY);
    }

    @Override
    public void masa_gadget_mod$addFastSwitcherWidget() {
        if (Configs.fastSwitchMasaConfigGui.getBooleanValue()) {
            this.masa_gadget$masaModGuiList = new SelectorDropDownList<>(
                    this.width - 111, 10, 100, 16, 200,
                    Configs.fastSwitchMasaConfigGuiVisibleEntries.getIntegerValue(),
                    FastMasaGuiSwitcher.getInstance().getModNameList());
            this.masa_gadget$masaModGuiList.setSelectedEntry(FastMasaGuiSwitcher.getInstance().getModName(this.getClass()));
            this.masa_gadget$masaModGuiList.setEntryChangeListener(entry ->
                    GuiBase.openGui(FastMasaGuiSwitcher.getInstance().getConfigScreenFactory(entry).create(this.getParent())));
            this.addWidget(this.masa_gadget$masaModGuiList);
        }
    }

    @Override
    public void masa_gad_get$renderHovered(
            //#if MC >= 1.21.11
            //$$ GuiContext guiContext,
            //#elseif MC > 11904
            //$$ GuiGraphics poseStackOrGuiGraphics,
            //#elseif MC > 11502
            PoseStack poseStackOrGuiGraphics,
            //#endif
            int mouseX,
            int mouseY
    ) {
        if (this.masa_gadget$masaModGuiList == null) {
            return;
        }

        this.masa_gadget$masaModGuiList.render(
                // CHECKSTYLE.OFF: NoWhitespaceBefore
                // CHECKSTYLE.OFF: SeparatorWrap
                //#if MC >= 1.21.11
                //$$ guiContext,
                //#elseif MC >= 12106
                //$$ poseStackOrGuiGraphics,
                //#endif
                mouseX,
                mouseY,
                false
                //#if 12106 > MC && MC > 11502
                , poseStackOrGuiGraphics
                //#endif
                // CHECKSTYLE.ON: SeparatorWrap
                // CHECKSTYLE.ON: NoWhitespaceBefore
        );

        if (this.masa_gadget$masaModGuiList.isMouseOver(mouseX, mouseY)) {
            this.hoveredWidget = this.masa_gadget$masaModGuiList;
        }

        this.drawHoveredWidget(
                // CHECKSTYLE.OFF: NoWhitespaceBefore
                // CHECKSTYLE.OFF: SeparatorWrap
                //#if MC >= 1.21.11
                //$$ guiContext,
                //#elseif MC >= 12106
                //$$ poseStackOrGuiGraphics,
                //#endif
                mouseX,
                mouseY
                //#if 12106 > MC && MC > 11502
                , poseStackOrGuiGraphics
                //#endif
                // CHECKSTYLE.ON: SeparatorWrap
                // CHECKSTYLE.ON: NoWhitespaceBefore
        );
    }

    //#if MC > 12006
    //$$ // Force blocking malilib's intrinsic dropdown list
    //$$ @Dynamic
    //$$ @WrapWithCondition(
    //$$         method = {"initGui", "buildConfigSwitcher"},
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lfi/dy/masa/malilib/gui/GuiConfigsBase;addWidget(Lfi/dy/masa/malilib/gui/widgets/WidgetBase;)Lfi/dy/masa/malilib/gui/widgets/WidgetBase;"
    //$$         ),
    //$$         require = 0
    //$$ )
    //$$ private boolean blockInherentDropdownList(GuiConfigsBase instance, WidgetBase widgetBase) {
    //$$     return !Configs.fastSwitchMasaConfigGui.getBooleanValue();
    //$$ }
    //#endif
}
