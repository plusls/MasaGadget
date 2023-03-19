package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportComparator;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.HitResultUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "render(FJ)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"))
    private void postRender(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Level world = WorldUtils.getBestWorld(mc);
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (world == null) {
            return;
        }

        if (!FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() || !Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld() ||
                !Configs.inventoryPreviewSupportComparator) {
            return;
        }

        // 开始渲染
        BlockPos pos = HitResultUtil.getHitBlockPos();
        Object blockEntity = HitResultUtil.getLastHitBlockEntity();

        if (pos != null && blockEntity instanceof ComparatorBlockEntity) {
            TextComponent literalText = new TextComponent(((ComparatorBlockEntity) blockEntity).getOutputSignal() + "");
            literalText.withStyle(ChatFormatting.GREEN);
            GlStateManager.disableDepthTest();
            PoseStack poseStackCompat = new PoseStack();
            RenderUtil.renderTextOnWorld(poseStackCompat, camera, pos, literalText, true);
            GlStateManager.enableDepthTest();
        }
    }
}
