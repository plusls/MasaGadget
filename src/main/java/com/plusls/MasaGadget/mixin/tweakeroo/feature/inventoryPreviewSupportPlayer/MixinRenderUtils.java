package com.plusls.MasaGadget.mixin.tweakeroo.feature.inventoryPreviewSupportPlayer;

import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    static private Entity traceEntity = null;

    @Inject(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;",
                    ordinal = 0, remap = true), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void getTraceEntity(MinecraftClient mc, MatrixStack matrixStack, CallbackInfo ci,
                                       World world, PlayerEntity player, HitResult trace, Inventory inv, ShulkerBoxBlock block,
                                       LivingEntity entityLivingBase, Entity entity) {
        traceEntity = entity;
    }

    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    ordinal = 0, remap = false), ordinal = 0)
    private static Inventory modifyInv(Inventory inv) {
        Inventory ret = inv;
        if (ret == null && traceEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) traceEntity;
            ret = playerEntity.inventory;

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
        traceEntity = null;
        return ret;
    }
}
