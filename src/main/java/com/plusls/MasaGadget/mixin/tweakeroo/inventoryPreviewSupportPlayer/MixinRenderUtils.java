package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportPlayer;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.TraceUtil;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Dependencies(dependencyList = @Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    ordinal = 0, remap = false), ordinal = 0)
    private static Inventory modifyInv(Inventory inv) {
        Inventory ret = inv;
        Entity traceEntity = TraceUtil.getTraceEntity();
        if (Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_PLAYER.getBooleanValue() && ret == null &&
                traceEntity instanceof PlayerEntity playerEntity) {
            ret = playerEntity.getInventory();

            int x = GuiUtils.getScaledWindowWidth() / 2 - 88;
            int y = GuiUtils.getScaledWindowHeight() / 2 + 10;
            int slotOffsetX = 8;
            int slotOffsetY = 8;
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.GENERIC;
            DyeColor dye = DyeColor.GRAY;
            float[] colors = dye.getColorComponents();

            fi.dy.masa.malilib.render.RenderUtils.color(colors[0], colors[1], colors[2], 1.0F);
            InventoryOverlay.renderInventoryBackground(type, x, y, 9, 27, MinecraftClient.getInstance());
            InventoryOverlay.renderInventoryStacks(type, playerEntity.getEnderChestInventory(), x + slotOffsetX, y + slotOffsetY, 9, 0, 27, MinecraftClient.getInstance());
            fi.dy.masa.malilib.render.RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        return ret;
    }
}
