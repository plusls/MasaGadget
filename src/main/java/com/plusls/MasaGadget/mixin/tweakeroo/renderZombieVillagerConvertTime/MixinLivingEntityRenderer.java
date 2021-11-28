package com.plusls.MasaGadget.mixin.tweakeroo.renderZombieVillagerConvertTime;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Dependencies(dependencyList = @Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity> extends EntityRenderer<T> {

    protected MixinLivingEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    // from entityRenderer
    @Inject(method = "render*", at = @At(value = "RETURN"))
    private void postRenderEntity(T livingEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (!(livingEntity instanceof ZombieVillagerEntity) || !Configs.Tweakeroo.RENDER_ZOMBIE_VILLAGER_CONVERT_TIME.getBooleanValue()) {
            return;
        }

        ZombieVillagerEntity zombieVillagerEntity = MiscUtil.getBestEntity((ZombieVillagerEntity) livingEntity);

        Text text;

        if (zombieVillagerEntity.conversionTimer <= 0) {
            return;
        } else {
            text = new LiteralText(String.format("%d", zombieVillagerEntity.conversionTimer));
        }

        RenderUtil.renderTextOnEntity(matrixStack, zombieVillagerEntity, this.renderManager, vertexConsumerProvider, text,
                zombieVillagerEntity.getHeight() / 16 * 15);
    }

}
