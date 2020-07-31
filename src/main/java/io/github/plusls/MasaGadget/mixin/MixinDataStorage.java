package io.github.plusls.MasaGadget.mixin;

import fi.dy.masa.minihud.util.DataStorage;
import io.github.plusls.MasaGadget.util.ParseBborPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DataStorage.class, remap = false)
public abstract class MixinDataStorage {
    // reset 会发生在进入游戏以后, 所以需要在 reset 后重新加载种子和结构
    @Inject(method = "reset(Z)V", at = @At(value = "RETURN"))
    private void postReset(boolean isLogout, CallbackInfo ci) {
        if (!isLogout) {
            if (ParseBborPacket.seedCache != null)
                DataStorage.getInstance().setWorldSeed(ParseBborPacket.seedCache);
            if (ParseBborPacket.spawnPos != null)
                DataStorage.getInstance().setWorldSpawn(ParseBborPacket.spawnPos);
            if (ParseBborPacket.structuresCache != null)
                DataStorage.getInstance().addOrUpdateStructuresFromServer(ParseBborPacket.structuresCache, 0x7fffffff - 0x1000, false);
        } else {
            ParseBborPacket.seedCache = null;
            ParseBborPacket.spawnPos = null;
            ParseBborPacket.structuresCache = null;
        }
    }
}