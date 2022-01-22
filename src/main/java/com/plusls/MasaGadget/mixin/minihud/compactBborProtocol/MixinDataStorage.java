package com.plusls.MasaGadget.mixin.minihud.compactBborProtocol;

import com.plusls.MasaGadget.ModInfo;
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
@Dependencies(dependencyList = @Dependency(modId = ModInfo.MINIHUD_MOD_ID, version = "*"))
public abstract class MixinDataStorage {

    // 每进入一个新维度就需要重新加载一次数据
    @Inject(method = "onWorldJoin", at = @At(value = "RETURN"))
    private void postOnWorldJoin(CallbackInfo ci) {
        if (!Configs.Minihud.COMPACT_BBOR_PROTOCOL.getBooleanValue() || !BborProtocol.enable) {
            return;
        }
        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            BborProtocol.initMetaData();
            BborProtocol.bborRefreshData(world.getRegistryKey().getValue());
        }
    }
}