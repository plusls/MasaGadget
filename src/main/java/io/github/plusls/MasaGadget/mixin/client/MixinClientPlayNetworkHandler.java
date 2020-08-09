package io.github.plusls.MasaGadget.mixin.client;

import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements ClientPlayPacketListener {

    @Shadow
    private MinecraftClient client;

    @Inject(method = "onBlockEntityUpdate(Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;)V",
            at = @At(value = "RETURN"))
    private void postOnBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo info) {
        int blockEntityType = packet.getBlockEntityType();
        BlockEntity blockEntity = this.client.world.getBlockEntity(packet.getPos());
        if (blockEntityType == 0 && (
                blockEntity instanceof ShulkerBoxBlockEntity ||
                        blockEntity instanceof HopperBlockEntity ||
                        blockEntity instanceof AbstractFurnaceBlockEntity ||
                        blockEntity instanceof DispenserBlockEntity // 包括了投掷器
        )) {
            blockEntity.fromTag(this.client.world.getBlockState(blockEntity.getPos()), packet.getCompoundTag());
        }
    }
}
