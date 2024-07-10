package com.plusls.MasaGadget.mixin.feature.cacheContainerMenu;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.feature.cacheContainerMenu.CacheContainerMenuHandler;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC < 11900
import net.minecraft.client.multiplayer.ClientLevel;
//#endif

@Mixin(MultiPlayerGameMode.class)
public class MixinMultiPlayerGameMode {
    @Inject(
            method = "useItemOn",
            at = @At(
                    value = "HEAD"
            )
    )
    private void prevUseItemOn(LocalPlayer localPlayer,
                               //#if MC < 11900
                               ClientLevel clientLevel,
                               //#endif
                               InteractionHand interactionHand,
                               BlockHitResult blockHitResult,
                               CallbackInfoReturnable<InteractionResult> cir) {
        if (Configs.cacheContainerMenu.getBooleanValue()) {
            CacheContainerMenuHandler.getInstance().setLastClickBlockPos(blockHitResult.getBlockPos());
        }
    }
}
