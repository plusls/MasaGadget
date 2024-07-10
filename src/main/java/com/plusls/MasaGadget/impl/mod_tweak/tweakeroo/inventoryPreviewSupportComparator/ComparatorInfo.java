package com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportComparator;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.feature.entityInfo.EntityInfoRenderer;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import org.jetbrains.annotations.ApiStatus;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.event.minecraft.render.RenderLevelListener;
import top.hendrixshen.magiclib.api.render.context.RenderContext;
import top.hendrixshen.magiclib.impl.render.TextRenderer;
import top.hendrixshen.magiclib.util.collect.ValueContainer;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;

public class ComparatorInfo implements RenderLevelListener {
    @Getter
    private static final EntityInfoRenderer instance = new EntityInfoRenderer();

    @ApiStatus.Internal
    public void init() {
        MagicLib.getInstance().getEventManager().register(RenderLevelListener.class, this);
    }

    @Override
    public void preRenderLevel(Level entity, RenderContext renderContext, float partialTicks) {
        // NO-OP
    }

    @Override
    public void postRenderLevel(Level entity, RenderContext renderContext, float partialTicks) {
        if (
                !MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.tweakeroo) ||
                !FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() ||
                !Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld() ||
                !Configs.inventoryPreviewSupportComparator.getBooleanValue()) {
            return;
        }

        ValueContainer<BlockPos> pos = HitResultHandler.getInstance().getHitBlockPos();
        Object blockEntity = HitResultHandler.getInstance().getLastHitBlockEntity().orElse(null);

        if (pos.isPresent() && blockEntity instanceof ComparatorBlockEntity) {
            TextRenderer.create()
                    .text(ComponentUtil.simple(((ComparatorBlockEntity) blockEntity).getOutputSignal())
                            .withStyle(ChatFormatting.GREEN).get())
                    .atCenter(pos.get())
                    .seeThrough();
        }
    }
}
