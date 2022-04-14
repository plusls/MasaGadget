package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportComparator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(LevelRenderer.class)
public class MixinWorldRenderer {
    @Inject(method = "renderLevel", at = @At(value = "RETURN"))
    private void postRender(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Level world = WorldUtils.getBestWorld(mc);
        if (world == null || mc.player == null) {
            return;
        }
        Entity cameraEntity = world.getPlayerByUUID(mc.player.getUUID());
        if (cameraEntity == null) {
            cameraEntity = mc.player;
        }

        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue()) {
            cameraEntity = mc.getCameraEntity();
        }

        if (!FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() || !Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld() ||
                !Configs.inventoryPreviewSupportComparator || cameraEntity == null) {
            return;
        }

        PoseStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushPose();
        matrixStack.mulPoseMatrix(matrices.last().pose());
        RenderSystem.applyModelViewMatrix();

        // 开始渲染

        HitResult trace = RayTraceUtils.getRayTraceFromEntity(world, cameraEntity, false);

        if (trace.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) trace).getBlockPos();

            // 绕过线程检查
            BlockEntity blockEntity = world.getChunkAt(pos).getBlockEntity(pos);
            if (blockEntity instanceof ComparatorBlockEntity) {
                TextComponent literalText = new TextComponent(((ComparatorBlockEntity) blockEntity).getOutputSignal() + "");
                literalText.withStyle(ChatFormatting.GREEN);
                //literalText.formatted(Formatting.);

                MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

                // 不加 1.17 渲染会有问题
                RenderSystem.disableDepthTest();

                matrixStack.pushPose();
                matrixStack.translate(pos.getX() + 0.5 - camera.getPosition().x(), pos.getY() + 0.6 - camera.getPosition().y(), pos.getZ() + 0.5 - camera.getPosition().z());
                matrixStack.mulPoseMatrix(new Matrix4f(camera.rotation()));
                matrixStack.scale(-0.04F, -0.04F, -0.04F);
                RenderSystem.applyModelViewMatrix();

                Matrix4f lv = Transformation.identity().getMatrix();

                float xOffset = (float) (-mc.font.width(literalText) / 2);
                float g = mc.options.getBackgroundOpacity(0.25F);
                int k = (int) (g * 255.0F) << 24;
                mc.font.drawInBatch(literalText, xOffset, 0, 553648127, false, lv, immediate, true, k, 0xf00000);
                immediate.endBatch();

                mc.font.drawInBatch(literalText, xOffset, 0, -1, false, lv, immediate, true, 0, 0xf00000);

                immediate.endBatch();
                matrixStack.popPose();
                RenderSystem.applyModelViewMatrix();
                RenderSystem.enableDepthTest();
            }
        }

        // 结束渲染
        matrixStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
