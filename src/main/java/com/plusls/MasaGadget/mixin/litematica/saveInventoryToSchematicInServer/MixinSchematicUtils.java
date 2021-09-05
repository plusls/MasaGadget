package com.plusls.MasaGadget.mixin.litematica.saveInventoryToSchematicInServer;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.litematica.saveInventoryToSchematicInServer.PcaSyncUtil;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.pcaSyncProtocol.PcaSyncProtocol;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.selection.SelectionManager;
import fi.dy.masa.litematica.util.SchematicUtils;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SchematicUtils.class, remap = false)
@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.LITEMATICA_MOD_ID, version = "*"))
public class MixinSchematicUtils {

    @Inject(method = "saveSchematic", at = @At(value = "HEAD"))
    private static void syncInventory(boolean inMemoryOnly, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.Litematica.SAVE_INVENTORY_TO_SCHEMATIC_IN_SERVER.getBooleanValue() || MinecraftClient.getInstance().isIntegratedServerRunning() || !PcaSyncProtocol.enable) {
            return;
        }
        SelectionManager sm = DataManager.getSelectionManager();
        AreaSelection area = sm.getCurrentSelection();
        if (area == null) {
            return;
        }
        PcaSyncUtil.sync(area.getAllSubRegionBoxes());
    }

    @Inject(method = "saveSchematic", at = @At(value = "RETURN"))
    private static void postSaveSchematic(boolean inMemoryOnly, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.Litematica.SAVE_INVENTORY_TO_SCHEMATIC_IN_SERVER.getBooleanValue() && PcaSyncUtil.lastUpdatePos == null) {
            InfoUtils.showGuiOrInGameMessage(Message.MessageType.SUCCESS, "masa_gadget_mod.message.loadInventoryToLocalSuccess");
        }
    }
}
