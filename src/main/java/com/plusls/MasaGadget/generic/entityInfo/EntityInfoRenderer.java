package com.plusls.MasaGadget.generic.entityInfo;

import com.google.common.collect.Lists;
import com.plusls.MasaGadget.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.event.render.impl.RenderContext;
import top.hendrixshen.magiclib.event.render.impl.RenderEventHandler;
import top.hendrixshen.magiclib.render.impl.TextRenderer;

import java.util.List;

public class EntityInfoRenderer {
    private static final List<Entity> list = Lists.newArrayList();

    public static void init() {
        // TODO: Temp impl
        RenderEventHandler.registerPostRenderEntityEvent(EntityInfoRenderer::collect);
        RenderEventHandler.registerPostRenderLevelEvent(EntityInfoRenderer::render);
    }

    private static void collect(Entity entity, RenderContext context, float tickDelta) {
        if ((entity instanceof Villager && (Configs.renderNextRestockTime || Configs.renderTradeEnchantedBook)) ||
                (entity instanceof ZombieVillager && (Configs.renderZombieVillagerConvertTime))) {
            EntityInfoRenderer.list.add(entity);
        }
    }

    public static void render(Level level, RenderContext context, float tickDelta) {
        for (Entity entity : EntityInfoRenderer.list) {
            if (entity instanceof Villager) {
                Villager villager = (Villager) EntityInfoRenderer.getEntityDataFromIntegratedServer(entity);
                TextRenderer renderer = TextRenderer.create();

                if (Configs.renderNextRestockTime) {
                    renderer.addLine(VillagerNextRestockTimeInfo.getInfo(villager));
                }

                if (Configs.renderTradeEnchantedBook) {
                    VillageTradeEnchantedBookInfo.getInfo(villager).forEach(renderer::addLine);
                }

                EntityInfoRenderer.rotationAround(renderer, villager.getEyePosition(tickDelta), 0.7)
                        .bgColor((int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24)
                        .fontScale(0.015F)
                        .render(context);
            } else if (entity instanceof ZombieVillager) {
                ZombieVillager zombieVillager = (ZombieVillager) EntityInfoRenderer.getEntityDataFromIntegratedServer(entity);
                EntityInfoRenderer.rotationAround(TextRenderer.create(), zombieVillager.getEyePosition(tickDelta), 0.6)
                        .text(ZombieVillagerConvertTimeInfo.getInfo(zombieVillager))
                        .bgColor((int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24)
                        .fontScale(0.015F)
                        .render(context);
            }
        }

        EntityInfoRenderer.list.clear();
    }

    private static TextRenderer rotationAround(@NotNull TextRenderer renderer, @NotNull Position centerPos, double range) {
        Position camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        float xAngle = (float) Mth.atan2(camPos.z() - centerPos.z(), camPos.x() - centerPos.x());
        float yAngle = (float) Mth.atan2(camPos.x() - centerPos.x(), camPos.z() - centerPos.z());
        return renderer.pos(range * Mth.cos(xAngle) + centerPos.x(), centerPos.y(), range * Mth.cos(yAngle) + centerPos.z());
    }

    private static Entity getEntityDataFromIntegratedServer(Entity entity) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();

        if (server == null) {
            return entity;
        }

        //#if MC > 11502
        ServerLevel level = server.getLevel(entity.getLevelCompat().dimension());
        //#else
        //$$ ServerLevel level = server.getLevel(entity.dimension);
        //#endif
        return level == null ? entity : level.getEntity(entity.getId());
    }
}
