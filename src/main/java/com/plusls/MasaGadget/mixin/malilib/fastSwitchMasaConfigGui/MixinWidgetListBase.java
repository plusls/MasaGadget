package com.plusls.MasaGadget.mixin.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.gui.IDropdownRenderer;
import com.plusls.MasaGadget.mixin.accessor.AccessorWidgetListConfigOptions;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;
import top.hendrixshen.magiclib.util.MiscUtil;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC > 11502
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

/*
 * Modified from TweakerMore
 * https://github.com/Fallen-Breath/tweakermore/blob/stable/src/main/java/me/fallenbreath/tweakermore/mixins/core/gui/WidgetListBaseMixin.java
 */
@Dependencies(and = @Dependency(ModInfo.MODMENU_MOD_ID))
@Mixin(value = WidgetListBase.class, priority = 1100)
public abstract class MixinWidgetListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>> {
    private boolean masa_gadget$shouldRenderDropdownListAgain = false;

    @Inject(method = "drawContents", at = @At("HEAD"), remap = false)
    private void drawTweakerMoreConfigGuiDropDownListSetFlag(CallbackInfo ci) {
        this.masa_gadget$shouldRenderDropdownListAgain = true;
    }
    @Inject(
            method = "drawContents",
            at = @At(
                    value = "INVOKE",
                    //#if MC > 11904
                    //$$ target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZLnet/minecraft/client/gui/GuiGraphics;)V",
                    //$$ remap = true
                    //#elseif MC > 11502
                    target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZLcom/mojang/blaze3d/vertex/PoseStack;)V",
                    remap = true
                    //#else
                    //$$ target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZ)V",
                    //$$ remap = false
                    //#endif
            ),
            remap = false
    )
    //#if MC > 11904
    //$$ private void drawDropDownListAgainBeforeHover(GuiGraphics gui, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
    //$$     this.masa_gadget$renderDropdownListAgain(gui, mouseX, mouseY);
    //#elseif MC > 11502
    private void drawDropDownListAgainBeforeHover(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        this.masa_gadget$renderDropdownListAgain(poseStack, mouseX, mouseY);
    //#else
    //$$ private void drawTweakerMoreConfigGuiDropDownListAgainBeforeHover(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
    //$$     this.masa_gadget$renderDropdownListAgain(mouseX, mouseY);
    //#endif
    }

    @Inject(
            method = "drawContents",
            at = @At(
                    "TAIL"
            ),
            remap = false
    )
    //#if MC > 11904
    //$$ private void drawDropDownListAgainAfterHover(GuiGraphics gui, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
    //$$     this.masa_gadget$renderDropdownListAgain(gui, mouseX, mouseY);
    //#elseif MC > 11502
    private void drawDropDownListAgainAfterHover(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        this.masa_gadget$renderDropdownListAgain(poseStack, mouseX, mouseY);
    //#else
    //$$ private void drawDropDownListAgainAfterHover(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
    //$$     this.masa_gadget$renderDropdownListAgain(mouseX, mouseY);
    //#endif
    }

    @SuppressWarnings("ConstantConditions")
    //#if MC > 11904
    //$$ private void masa_gadget$renderDropdownListAgain(GuiGraphics gui, int mouseX, int mouseY) {
    //#elseif MC > 11502
    private void masa_gadget$renderDropdownListAgain(PoseStack poseStack, int mouseX, int mouseY) {
    //#else
    //$$ private void masa_gadget$renderDropdownListAgain(int mouseX, int mouseY) {
    //#endif
        if (this.masa_gadget$shouldRenderDropdownListAgain) {
            if (!(MiscUtil.cast(this) instanceof WidgetListConfigOptions)) {
                return;
            }
            GuiConfigsBase guiConfig = ((AccessorWidgetListConfigOptions) this).getParent();

            //#if MC > 11904
            //$$ ((IDropdownRenderer)guiConfig).masa_gad_get$renderHovered(gui ,mouseX, mouseY);
            //#elseif MC > 11502
            ((IDropdownRenderer)guiConfig).masa_gad_get$renderHovered(poseStack ,mouseX, mouseY);
            //#else
            //$$ ((IDropdownRenderer)guiConfig).masa_gad_get$renderHovered(mouseX, mouseY);
            //#endif
            this.masa_gadget$shouldRenderDropdownListAgain = false;
        }
    }
}
