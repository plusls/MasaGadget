package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.api.gui.MasaGadgetDropdownList;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.fastSwitchMasaConfigGui.FastMasaGuiSwitcher;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.interfaces.IStringValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.impl.malilib.config.gui.SelectorDropDownList;

//#if MC > 11902
//$$ import org.spongepowered.asm.mixin.Intrinsic;
//#endif

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Dependencies(
        require = {
                @Dependency(ModId.malilib),
                @Dependency(ModId.mod_menu)
        }
)
@Mixin(value = GuiConfigsBase.class, remap = false)
public abstract class MixinGuiConfigBase extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements MasaGadgetDropdownList {
    protected MixinGuiConfigBase(int listX, int listY) {
        super(listX, listY);
    }

    @Unique
    private SelectorDropDownList<IStringValue> masa_gadget$masaModGuiList;

    //#if MC > 11902
    //$$ //@Intrinsic
    //$$ //@Override
    //$$ //public void initGui() {
    //$$ //    super.initGui();
    //$$ //}
    //$$
    //$$ @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    //#endif
    @Inject(
            method = "initGui",
            at = @At(
                    value = "RETURN"
            )
    )
    public void postInitGui(CallbackInfo ci) {
        if (Configs.fastSwitchMasaConfigGui.getBooleanValue()) {
            this.masa_gadget$masaModGuiList = new SelectorDropDownList<>(
                    this.width - 111, 10, 100, 16, 200, 5,
                    FastMasaGuiSwitcher.getInstance().getModNameList());
            this.masa_gadget$masaModGuiList.setSelectedEntry(FastMasaGuiSwitcher.getInstance().getModName(this.getClass()));
            this.masa_gadget$masaModGuiList.setEntryChangeListener(entry ->
                    GuiBase.openGui(FastMasaGuiSwitcher.getInstance().getConfigScreenFactory(entry)
                            .create(this.getParent())));
            this.addWidget(this.masa_gadget$masaModGuiList);
        }
    }

    @Override
    public void masa_gad_get$renderHovered(
            //#if MC > 11904
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
                mouseX,
                mouseY,
                false
                //#if MC > 11502
                , poseStackOrGuiGraphics
                //#endif
        );

        if (this.masa_gadget$masaModGuiList.isMouseOver(mouseX, mouseY)) {
            this.hoveredWidget = this.masa_gadget$masaModGuiList;
        }

        this.drawHoveredWidget(
                mouseX,
                mouseY
                //#if MC > 11502
                , poseStackOrGuiGraphics
                //#endif
        );
    }
}
