package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportPlayer;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.player.PlayerCompat;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

// CHECKSTYLE.OFF: ImportOrder
//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif
// CHECKSTYLE.ON: ImportOrder

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// CHECKSTYLE.OFF: ImportOrder
//#if MC > 11904
//$$ import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.Local;
//#endif
// CHECKSTYLE.ON: ImportOrder

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc1.20: subproject 1.16.5 (main project)        &lt;--------</li>
 * <li>mc1.21+        : subproject 1.21.1 [dummy]</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {
    @ModifyVariable(
            method = "renderInventoryOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    remap = false
            )
    )
    private static Container modifyInv(
            // CHECKSTYLE.OFF: NoWhitespaceBefore
            // CHECKSTYLE.OFF: SeparatorWrap
            Container inv
            //#if MC > 11904
            //$$ , @Local(argsOnly = true) GuiGraphics guiGraphics
            //#endif
            // CHECKSTYLE.ON: SeparatorWrap
            // CHECKSTYLE.ON: NoWhitespaceBefore
    ) {
        Container ret = inv;
        Entity traceEntity = HitResultHandler.getInstance().getHitEntity().orElse(null);

        if (Configs.inventoryPreviewSupportPlayer.getBooleanValue() && ret == null && traceEntity instanceof Player) {
            Player player = (Player) traceEntity;
            PlayerCompat playerCompat = PlayerCompat.of(player);
            ret = playerCompat.getInventory();
            int x = GuiUtils.getScaledWindowWidth() / 2 - 88;
            int y = GuiUtils.getScaledWindowHeight() / 2 + 10;
            int slotOffsetX = 8;
            int slotOffsetY = 8;
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.GENERIC;
            DyeColor dye = DyeColor.GRAY;
            //#if MC < 12106
            //#if MC > 12006
            //$$ float[] colors = fi.dy.masa.malilib.render.RenderUtils.getColorComponents(dye.getTextureDiffuseColor());
            //#else
            float[] colors = dye.getTextureDiffuseColors();
            //#endif
            fi.dy.masa.malilib.render.RenderUtils.color(colors[0], colors[1], colors[2], 1.0F);
            //#endif
            InventoryOverlay.renderInventoryBackground(
                    // CHECKSTYLE.OFF: NoWhitespaceBefore
                    // CHECKSTYLE.OFF: SeparatorWrap
                    //#if MC >= 12106
                    //$$ guiGraphics,
                    //#endif
                    type,
                    x,
                    y,
                    9,
                    27,
                    //#if MC >= 12106
                    //$$ dye.getTextureDiffuseColor(),
                    //#endif
                    Minecraft.getInstance()
                    //#if 12106 > MC && MC > 12104
                    //$$ , guiGraphics
                    //#endif
                    // CHECKSTYLE.OFF: NoWhitespaceBefore
                    // CHECKSTYLE.OFF: SeparatorWrap
            );
            InventoryOverlay.renderInventoryStacks(
                    // CHECKSTYLE.OFF: NoWhitespaceBefore
                    // CHECKSTYLE.OFF: SeparatorWrap
                    //#if MC >= 12106
                    //$$ guiGraphics,
                    //#endif
                    type,
                    player.getEnderChestInventory(),
                    x + slotOffsetX,
                    y + slotOffsetY,
                    9,
                    0,
                    27,
                    Minecraft.getInstance()
                    //#if 12106 > MC && MC > 11904
                    //$$ , guiGraphics
                    //#endif
                    // CHECKSTYLE.OFF: NoWhitespaceBefore
                    // CHECKSTYLE.OFF: SeparatorWrap
            );
            //#if MC < 12106
            fi.dy.masa.malilib.render.RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
            //#endif
        }

        return ret;
    }
}
