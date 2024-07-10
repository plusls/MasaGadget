package com.plusls.MasaGadget.mixin.feature.cacheContainerMenu;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.feature.cacheContainerMenu.CacheContainerMenuHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
    @Inject(
            method = "handleOpenScreen",
            at = @At(
                    value = "RETURN"
            )
    )
    private void postHandleOpenScreen(ClientboundOpenScreenPacket clientboundOpenScreenPacket, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!Configs.cacheContainerMenu.getBooleanValue() || minecraft.hasSingleplayerServer()) {
            return;
        }

        LocalPlayer localPlayer = Objects.requireNonNull(minecraft.player);

        if (localPlayer.containerMenu != localPlayer.inventoryMenu) {
            CacheContainerMenuHandler.getInstance().checkLastClickBlockPos();
        }
    }

    @Inject(
            method = "handleContainerSetSlot",
            at = @At(
                    value = "RETURN"
            )
    )
    private void postHandleContainerSetSlot(ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket,
                                            CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!Configs.cacheContainerMenu.getBooleanValue() || minecraft.hasSingleplayerServer()) {
            return;
        }

        LocalPlayer localPlayer = Objects.requireNonNull(minecraft.player);
        int containerId = clientboundContainerSetSlotPacket.getContainerId();

        if (containerId != 0 && containerId != -1 && containerId != -2 &&
                (clientboundContainerSetSlotPacket.getContainerId() == localPlayer.containerMenu.containerId ||
                        !(minecraft.screen instanceof CreativeModeInventoryScreen))) {
            if (CacheContainerMenuHandler.getInstance().isAvailableMenu()) {
                Container container = CacheContainerMenuHandler.getInstance().getLastClickContainer();

                if (container != null) {
                    int slotId = clientboundContainerSetSlotPacket.getSlot();

                    if (slotId < container.getContainerSize()) {
                        container.setItem(slotId, clientboundContainerSetSlotPacket.getItem());
                    }
                }
            } else {
                CacheContainerMenuHandler.getInstance().clearLastClickData();
            }
        }
    }

    @Inject(method = "handleContainerContent", at = @At(value = "RETURN"))
    private void postHandleContainerContent(ClientboundContainerSetContentPacket clientboundContainerSetContentPacket, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!Configs.cacheContainerMenu.getBooleanValue() || minecraft.hasSingleplayerServer()) {
            return;
        }

        LocalPlayer localPlayer = Objects.requireNonNull(minecraft.player);
        int containerId = clientboundContainerSetContentPacket.getContainerId();

        if (containerId != 0 && containerId == localPlayer.containerMenu.containerId) {
            if (CacheContainerMenuHandler.getInstance().isAvailableMenu()) {
                Container container = CacheContainerMenuHandler.getInstance().getLastClickContainer();
                List<ItemStack> items = clientboundContainerSetContentPacket.getItems();

                if (container != null) {
                    for (int i = 0; i < container.getContainerSize() && i < items.size(); ++i) {
                        container.setItem(i, items.get(i));
                    }
                }
            } else {
                CacheContainerMenuHandler.getInstance().clearLastClickData();
            }
        }
    }
}
