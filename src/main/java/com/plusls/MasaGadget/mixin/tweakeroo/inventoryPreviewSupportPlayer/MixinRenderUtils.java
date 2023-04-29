package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportPlayer;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.HitResultUtil;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {
    //#if MC > 11904
    //$$ private static GuiGraphics masa_gadget$gui;
    //$$
    //$$ @Inject(method = "renderInventoryOverlay", at = @At("HEAD"))
    //$$ private static void intercept(Minecraft mc, GuiGraphics gui, CallbackInfo ci) {
    //$$     masa_gadget$gui = gui;
    //$$ }
    //#endif

    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    ordinal = 0, remap = false), ordinal = 0)
    private static Container modifyInv(Container inv) {
        Container ret = inv;
        Entity traceEntity = HitResultUtil.getHitEntity();
        if (Configs.inventoryPreviewSupportPlayer && ret == null &&
                traceEntity instanceof Player) {
            Player playerEntity = (Player) traceEntity;
            ret = playerEntity.getInventory();

            int x = GuiUtils.getScaledWindowWidth() / 2 - 88;
            int y = GuiUtils.getScaledWindowHeight() / 2 + 10;
            int slotOffsetX = 8;
            int slotOffsetY = 8;
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.GENERIC;
            DyeColor dye = DyeColor.GRAY;
            float[] colors = dye.getTextureDiffuseColors();

            fi.dy.masa.malilib.render.RenderUtils.color(colors[0], colors[1], colors[2], 1.0F);
            InventoryOverlay.renderInventoryBackground(type, x, y, 9, 27, Minecraft.getInstance());
            InventoryOverlay.renderInventoryStacks(type, playerEntity.getEnderChestInventory(), x + slotOffsetX,
                    //#if MC > 11904
                    //$$ y + slotOffsetY, 9, 0, 27, Minecraft.getInstance(), masa_gadget$gui);
                    //#else
                    y + slotOffsetY, 9, 0, 27, Minecraft.getInstance());
                    //#endif
            fi.dy.masa.malilib.render.RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        return ret;
    }
}
