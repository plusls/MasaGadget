/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.api.gui.MasaGadgetDropdownList;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.mixin.malilib.accessor.WidgetListConfigOptionsAccessor;
import top.hendrixshen.magiclib.util.MiscUtil;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC > 11502
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

/**
 * Reference to <a href="https://github.com/Fallen-Breath/tweakermore/blob/10e1a937aadcefb1f2d9d9bab8badc873d4a5b3d/src/main/java/me/fallenbreath/tweakermore/mixins/core/gui/panel/dropDownListRedraw/WidgetListBaseMixin.java">TweakerMore</a>
 */
@Dependencies(
        require = {
                @Dependency(ModId.malilib),
                @Dependency(ModId.mod_menu)
        }
)
@Mixin(value = WidgetListBase.class, remap = false, priority = 1100)
public abstract class MixinWidgetListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>> {
    // To make sure it only once gets rendered
    @Unique
    private boolean masa_gadget_mod$shouldRenderMagicConfigGuiDropDownList = false;

    @SuppressWarnings("ConstantConditions")
    @Unique
    private void masa_gadget_mod$drawMagicConfigGuiDropDownListAgain(
            //#if MC > 11904
            //$$ GuiGraphics poseStackOrGuiGraphics,
            //#elseif MC > 11502
            PoseStack poseStackOrGuiGraphics,
            //#endif
            int mouseX,
            int mouseY
    ) {
        if (this.masa_gadget_mod$shouldRenderMagicConfigGuiDropDownList) {
            if (!(MiscUtil.cast(this) instanceof WidgetListConfigOptions)) {
                return;
            }

            GuiConfigsBase guiConfig = ((WidgetListConfigOptionsAccessor) this).magiclib$getParent();

            // Render it again to make sure it's on the top but below hovering widgets.
            ((MasaGadgetDropdownList) guiConfig).masa_gad_get$renderHovered(
                    //#if MC > 11600
                    poseStackOrGuiGraphics,
                    //#endif
                    mouseX,
                    mouseY
            );

            this.masa_gadget_mod$shouldRenderMagicConfigGuiDropDownList = false;
        }
    }

    @Inject(
            method = "drawContents",
            at = @At(
                    "HEAD"
            )
    )
    private void drawMagicConfigGuiDropDownListSetFlag(CallbackInfo ci) {
        this.masa_gadget_mod$shouldRenderMagicConfigGuiDropDownList = true;
    }

    //#if MC < 11904
    @Inject(
            method = "drawContents",
            at = @At(
                    value = "INVOKE",
                    //#if MC > 11502
                    target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZLcom/mojang/blaze3d/vertex/PoseStack;)V",
                    remap = true
                    //#else
                    //$$ target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZ)V"
                    //#endif
            )
    )
    private void drawMagicConfigGuiDropDownListAgainBeforeHover(
            //#if MC > 11502
            PoseStack poseStack,
            //#endif
            int mouseX,
            int mouseY,
            float partialTicks,
            CallbackInfo ci
    ) {
        this.masa_gadget_mod$drawMagicConfigGuiDropDownListAgain(
                //#if MC > 11502
                poseStack,
                //#endif
                mouseX,
                mouseY
        );
    }
    //#endif

    @Inject(
            method = "drawContents",
            at = @At(
                    value = "TAIL"
            )
    )
    private void drawMagicConfigGuiDropDownListAgainAfterHover(
            //#if MC > 11904
            //$$ GuiGraphics poseStackOrGuiGraphics,
            //#elseif MC > 11502
            PoseStack poseStackOrGuiGraphics,
            //#endif
            int mouseX,
            int mouseY,
            float partialTicks,
            CallbackInfo ci
    ) {
        this.masa_gadget_mod$drawMagicConfigGuiDropDownListAgain(
                //#if MC > 11502
                poseStackOrGuiGraphics,
                //#endif
                mouseX,
                mouseY
        );
    }
}
