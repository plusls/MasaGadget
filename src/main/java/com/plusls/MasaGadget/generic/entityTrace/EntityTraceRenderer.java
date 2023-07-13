package com.plusls.MasaGadget.generic.entityTrace;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import com.plusls.MasaGadget.util.SyncUtil;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.transformer.Config;
import top.hendrixshen.magiclib.event.render.impl.RenderContext;
import top.hendrixshen.magiclib.event.render.impl.RenderEventHandler;

import java.util.List;

public class EntityTraceRenderer {
    private static final List<Entity> list = Lists.newArrayList();

    public static void init() {
        RenderEventHandler.registerPostRenderEntityEvent(EntityTraceRenderer::collect);
        RenderEventHandler.registerPostRenderLevelEvent(EntityTraceRenderer::render);
    }

    private static void collect(Entity entity, RenderContext context, float tickDelta) {
        if (entity instanceof Villager && Configs.renderVillageHomeTracer || Configs.renderVillageJobSiteTracer) {
            EntityTraceRenderer.list.add(entity);
        }
    }

    private static void render(Level level, RenderContext context, float tickDelta) {
        for (Entity entity : EntityTraceRenderer.list) {
            if (entity instanceof Villager) {
                Villager villager = MiscUtil.cast(SyncUtil.syncEntityDataFromIntegratedServer(entity));

                if (Configs.renderVillageHomeTracer) {
                    villager.getBrain().getMemory(MemoryModuleType.HOME).ifPresent(globalPos -> {
                        Vec3 eyeVec3 = entity.getEyePosition(tickDelta);
                        Vec3 bedVec3 = new Vec3(globalPos.pos().getX() + 0.5, globalPos.pos().getY() + 0.5, globalPos.pos().getZ() + 0.5);
                        RenderSystem.disableDepthTest();
                        RenderUtil.drawConnectLine(eyeVec3, bedVec3, 0.05,
                                new Color4f(1, 1, 1),
                                Color4f.fromColor(Configs.renderVillageHomeTracerColor, 1.0F),
                                Configs.renderVillageHomeTracerColor);
                        RenderSystem.enableDepthTest();
                    });
                }

                if (Configs.renderVillageJobSiteTracer) {
                    villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).ifPresent(globalPos -> {
                        Vec3 eyeVec3 = entity.getEyePosition(tickDelta);
                        Vec3 jobVev3 = new Vec3(globalPos.pos().getX() + 0.5, globalPos.pos().getY() + 0.5, globalPos.pos().getZ() + 0.5);
                        RenderSystem.disableDepthTest();
                        RenderUtil.drawConnectLine(eyeVec3, jobVev3, 0.05,
                                new Color4f(1, 1, 1),
                                Color4f.fromColor(Configs.renderVillageJobSiteTracerColor, 1.0F),
                                Configs.renderVillageJobSiteTracerColor);
                        RenderSystem.enableDepthTest();
                    });
                }
            }
        }

        EntityTraceRenderer.list.clear();
    }
}
