package com.plusls.MasaGadget.mixin.mod_tweak.litematica.saveInventoryToSchematicInServer;

import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import com.plusls.MasaGadget.util.PcaSyncUtil;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.selection.SelectionManager;
import fi.dy.masa.litematica.util.SchematicUtils;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(ModId.litematica))
@Mixin(value = SchematicUtils.class, remap = false)
public class MixinSchematicUtils {
    @Inject(method = "saveSchematic", at = @At("HEAD"))
    private static void syncInventory(boolean inMemoryOnly, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.saveInventoryToSchematicInServer.getBooleanValue() ||
                Minecraft.getInstance().hasSingleplayerServer() || !PcaSyncProtocol.enable) {
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
        if (Configs.saveInventoryToSchematicInServer.getBooleanValue() && PcaSyncUtil.lastUpdatePos == null) {
            InfoUtils.showGuiOrInGameMessage(Message.MessageType.SUCCESS, SharedConstants.getModIdentifier() + ".message.loadInventoryToLocalSuccess");
        }
    }
}
