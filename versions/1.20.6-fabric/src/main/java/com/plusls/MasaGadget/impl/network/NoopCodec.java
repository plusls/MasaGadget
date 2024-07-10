package com.plusls.MasaGadget.impl.network;

import com.plusls.MasaGadget.api.network.packet.PCAPacket;
import lombok.AllArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@AllArgsConstructor(staticName = "create")
public class NoopCodec<B extends FriendlyByteBuf, P extends PCAPacket> implements StreamCodec<B, P> {
    private final Function<B, P> transformer;

    @Override
    public @NotNull P decode(B byteBuf) {
        return this.transformer.apply(byteBuf);
    }

    @Override
    public void encode(B byteBuf, P packet) {
        byteBuf.writeBytes(packet.getByteBuf().readByteArray());
    }
}
