package com.plusls.MasaGadget.generic.entityInfo;

import com.plusls.MasaGadget.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.render.impl.RenderContext;
import top.hendrixshen.magiclib.render.impl.RenderEventHandler;
import top.hendrixshen.magiclib.render.impl.TextRenderer;

public class EntityInfoRenderer {
    public static void init() {
        RenderEventHandler.registerPostRenderLevelRenderer(((level, context, tickDelta) ->
                ((ClientLevel) level).entitiesForRendering().forEach(entity ->
                        EntityInfoRenderer.render(entity, context, tickDelta))));
    }

    public static void render(Entity entity, RenderContext context, float tickDelta) {
        if (entity instanceof Villager && (Configs.renderNextRestockTime || Configs.renderTradeEnchantedBook)) {
            Villager villager = ((Villager) entity);
            TextRenderer renderer = new TextRenderer();

            if (Configs.renderNextRestockTime) {
                renderer.addLine(VillagerNextRestockTimeInfo.getInfo(villager));
            }

            if (Configs.renderTradeEnchantedBook) {
                VillageTradeEnchantedBookInfo.getInfo(villager).forEach(renderer::addLine);
            }

            EntityInfoRenderer.rotationAround(renderer, villager.getEyePosition(tickDelta), 0.7)
                    .fontSize(0.015)
                    .bgColor((int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24)
                    .render(context);
        } else if (entity instanceof ZombieVillager && (Configs.renderZombieVillagerConvertTime)) {
            ZombieVillager zombieVillager = (ZombieVillager) entity;
            EntityInfoRenderer.rotationAround(new TextRenderer(), zombieVillager.getEyePosition(tickDelta), 0.6)
                    .text(ZombieVillagerConvertTimeInfo.getInfo(zombieVillager))
                    .fontSize(0.015)
                    .bgColor((int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24)
                    .render(context);
        }
    }

    private static TextRenderer rotationAround(@NotNull TextRenderer renderer, @NotNull Position centerPos, double range) {
        Position camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        float xAngle = (float) Mth.atan2(camPos.z() - centerPos.z(), camPos.x() - centerPos.x());
        float yAngle = (float) Mth.atan2(camPos.x() - centerPos.x(), camPos.z() - centerPos.z());
        return renderer.pos(range * Mth.cos(xAngle) + centerPos.x(), centerPos.y(), range * Mth.cos(yAngle) + centerPos.z());
    }
}
