package com.plusls.MasaGadget.mixin.minihud.compactBborProtocol;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.minihud.compactBborProtocol.BborProtocol;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.minihud.util.DataStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DataStorage.class, remap = false)
@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.MINIHUD_MOD_ID, version = "*"))
public abstract class MixinDataStorage {

    // reset 会发生在进入游戏以后, 所以需要在 reset 后重新加载种子和结构
    @Inject(method = "reset", at = @At(value = "RETURN"))
    private void postReset(boolean isLogout, CallbackInfo ci) {
        if (!Configs.Minihud.COMPACT_BBOR_PROTOCOL.getBooleanValue()) {
            return;
        }
        if (!isLogout) {
            if (!BborProtocol.enable) {
                return;
            }
            World world = MinecraftClient.getInstance().world;
            if (world != null) {
                BborProtocol.bborRefreshData(world.getRegistryKey().getValue());
            }
        }
    }
}