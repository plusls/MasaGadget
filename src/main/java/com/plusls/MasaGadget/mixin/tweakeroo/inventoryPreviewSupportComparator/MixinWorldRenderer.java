package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportComparator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.tweakeroo.TraceUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = {@Dependency(ModInfo.TWEAKEROO_MOD_ID), @Dependency(value = "minecraft", versionPredicate = ">=1.15.2")})
@Mixin(LevelRenderer.class)
public class MixinWorldRenderer {
    @Inject(method = "renderLevel", at = @At(value = "RETURN"))
    private void postRender(PoseStack matrices, float tickDelta, long limitTime,
                            boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                            LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Level world = WorldUtils.getBestWorld(mc);
        if (world == null) {
            return;
        }

        if (!FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() || !Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld() ||
                !Configs.inventoryPreviewSupportComparator) {
            return;
        }


        // 开始渲染
        BlockPos pos = TraceUtil.getTraceBlockPos();
        if (pos != null) {
            // 绕过线程检查
            BlockEntity blockEntity = world.getChunkAt(pos).getBlockEntity(pos);
            if (blockEntity instanceof ComparatorBlockEntity) {
                TextComponent literalText = new TextComponent(((ComparatorBlockEntity) blockEntity).getOutputSignal() + "");
                literalText.withStyle(ChatFormatting.GREEN);
                // 不加 1.17 渲染会有问题
                RenderSystem.disableDepthTest();
                MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                RenderUtil.renderTextOnWorld(matrices, camera, immediate, pos, literalText, true);
                immediate.endBatch();
                RenderSystem.enableDepthTest();
            }
        }
    }
}
