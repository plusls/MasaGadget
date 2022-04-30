package com.plusls.MasaGadget.generic.renderZombieVillagerConvertTime;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.RenderEvent;
import com.plusls.MasaGadget.mixin.accessor.AccessorZombieVillager;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.ComponentCompatApi;

public class ZombieVillagerConvertTimeRenderer {

    public static void init() {
        RenderEvent.register(ZombieVillagerConvertTimeRenderer::postRenderEntity);
    }

    private static void postRenderEntity(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta,
                                         PoseStack matrixStack, int light) {
        if (!(entity instanceof ZombieVillager) || !Configs.renderZombieVillagerConvertTime) {
            return;
        }
        ZombieVillager zombieVillager = MiscUtil.getBestEntity((ZombieVillager) entity);

        Component text;
        int villagerConversionTime = ((AccessorZombieVillager) zombieVillager).getVillagerConversionTime();

        if (villagerConversionTime <= 0) {
            return;
        } else {
            text = ComponentCompatApi.literal(String.format("%d", villagerConversionTime));
        }

        RenderUtil.renderTextOnEntity(matrixStack, zombieVillager, dispatcher, text,
                zombieVillager.getBbHeight() / 16 * 15, false);
    }
}
