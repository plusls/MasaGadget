package com.plusls.MasaGadget.impl.feature.entityInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.SyncUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.event.minecraft.render.RenderEntityListener;
import top.hendrixshen.magiclib.api.event.minecraft.render.RenderLevelListener;
import top.hendrixshen.magiclib.api.render.context.RenderContext;
import top.hendrixshen.magiclib.impl.render.TextRenderer;

import java.util.Queue;

public class EntityInfoRenderer implements RenderEntityListener, RenderLevelListener {
    @Getter
    private static final EntityInfoRenderer instance = new EntityInfoRenderer();
    private final Queue<Entity> queue = Queues.newConcurrentLinkedQueue();

    @ApiStatus.Internal
    public void init() {
        MagicLib.getInstance().getEventManager().register(RenderEntityListener.class, this);
        MagicLib.getInstance().getEventManager().register(RenderLevelListener.class, this);
    }

    private static TextRenderer rotationAround(@NotNull TextRenderer renderer, @NotNull Position centerPos, double range) {
        Position camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        float xAngle = (float) Mth.atan2(camPos.z() - centerPos.z(), camPos.x() - centerPos.x());
        float zAngle = (float) Mth.atan2(camPos.x() - centerPos.x(), camPos.z() - centerPos.z());
        return renderer.at(range * Mth.cos(xAngle) + centerPos.x(), centerPos.y(), range * Mth.cos(zAngle) + centerPos.z());
    }

    @Override
    public void preRenderEntity(Entity entity, RenderContext renderContext, float partialTicks) {
        // NO-OP
    }

    @Override
    public void postRenderEntity(Entity entity, RenderContext renderContext, float partialTicks) {
        if ((entity instanceof Villager &&
                (Configs.renderNextRestockTime.getBooleanValue() || Configs.renderTradeEnchantedBook.getBooleanValue())) ||
                (entity instanceof ZombieVillager && (Configs.renderZombieVillagerConvertTime.getBooleanValue()))) {
            this.queue.add(entity);
        }
    }

    @Override
    public void preRenderLevel(Level level, RenderContext renderContext, float partialTicks) {
        // NO-OP
    }

    @Override
    public void postRenderLevel(Level level, RenderContext renderContext, float partialTicks) {
        for (Entity entity : this.queue) {
            if (entity instanceof Villager) {
                Villager villager = MiscUtil.cast(SyncUtil.syncEntityDataFromIntegratedServer(entity));
                TextRenderer renderer = TextRenderer.create();

                if (Configs.renderNextRestockTime.getBooleanValue()) {
                    renderer.addLine(VillagerNextRestockTimeInfo.getInfo(villager));
                }

                if (Configs.renderTradeEnchantedBook.getBooleanValue()) {
                    VillageTradeEnchantedBookInfo.getInfo(villager).forEach(renderer::addLine);
                }

                if (villager.isSleeping()) {
                    Position position = entity.getEyePosition(partialTicks);
                    renderer.at(position.x(), position.y() + 0.4F, position.z());
                } else {
                    EntityInfoRenderer.rotationAround(renderer, entity.getEyePosition(partialTicks), 0.6);
                }

                renderer.bgColor((int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24)
                        .fontScale(0.015F)
                        .seeThrough()
                        .render();
            } else if (entity instanceof ZombieVillager) {
                ZombieVillager zombieVillager = MiscUtil.cast(SyncUtil.syncEntityDataFromIntegratedServer(entity));
                EntityInfoRenderer.rotationAround(TextRenderer.create(), entity.getEyePosition(partialTicks), 0.6)
                        .text(ZombieVillagerConvertTimeInfo.getInfo(zombieVillager))
                        .bgColor((int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24)
                        .fontScale(0.015F)
                        .seeThrough()
                        .render();
            }
        }

        this.queue.clear();
    }
}
