package com.plusls.MasaGadget.mixin.generic.renderZombieVillagerConvertTime;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.accessor.AccessorZombieVillager;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.ZombieVillagerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(ZombieVillagerRenderer.class)
public abstract class MixinZombieVillagerRenderer extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerModel<ZombieVillager>> {


    public MixinZombieVillagerRenderer(EntityRendererProvider.Context context, ZombieVillagerModel<ZombieVillager> humanoidModel, float f) {
        super(context, humanoidModel, f);
    }

    @Override
    public void render(@NotNull ZombieVillager zombieVillager, float f, float g, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i) {
        super.render(zombieVillager, f, g, poseStack, multiBufferSource, i);
        if (!Configs.renderZombieVillagerConvertTime) {
            return;
        }
        zombieVillager = MiscUtil.getBestEntity(zombieVillager);

        Component text;
        int villagerConversionTime = ((AccessorZombieVillager) zombieVillager).getVillagerConversionTime();

        if (villagerConversionTime <= 0) {
            return;
        } else {
            text = new TextComponent(String.format("%d", villagerConversionTime));
        }

        RenderUtil.renderTextOnEntity(poseStack, zombieVillager, this.entityRenderDispatcher, multiBufferSource, text,
                zombieVillager.getBbHeight() / 16 * 15);
    }


}
