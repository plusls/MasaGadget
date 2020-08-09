package io.github.plusls.MasaGadget.mixin.client.tweakeroo;

import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.network.DataAccessor;
import io.github.plusls.MasaGadget.network.ServerNetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = RenderHandler.class, remap = false)
public abstract class MixinRenderHandler implements IRenderer {
    // 未按下按键时若是 lastBlockPos 不为空， 则告诉服务端不需要更新 block entity
    @Redirect(method = "onRenderGameOverlayPost",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/hotkeys/IKeybind;isKeybindHeld()Z",
                    ordinal = 2))
    private boolean redirectIsKeyBindHeld(IKeybind iKeybind) {
        boolean ret = iKeybind.isKeybindHeld();

        if (!ret && DataAccessor.lastBlockPos != null) {
            DataAccessor.lastBlockPos = null;
            MasaGadgetMod.LOGGER.debug("cancel requestBlockEntity");
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(false);
            MinecraftClient.getInstance().getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(ServerNetworkHandler.REQUEST_BLOCK_ENTITY, buf));
        }
        return ret;
    }
}
