package com.plusls.MasaGadget.mixin.malilib;

import com.plusls.MasaGadget.network.BborProtocol;
import fi.dy.masa.minihud.util.DataStorage;
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
            if (!BborProtocol.enable) {
                return;
            }
            if (BborProtocol.seedCache != null)
                DataStorage.getInstance().setWorldSeed(BborProtocol.seedCache);
            if (BborProtocol.spawnPos != null)
                DataStorage.getInstance().setWorldSpawn(BborProtocol.spawnPos);
            if (BborProtocol.structuresCache != null)
                DataStorage.getInstance().addOrUpdateStructuresFromServer(BborProtocol.structuresCache, 0x7fffffff - 0x1000, false);
        }
    }
}