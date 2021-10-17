package com.plusls.MasaGadget.mixin.tweakeroo.renderZombieVillagerConvertTime;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity> extends EntityRenderer<T> {

    protected MixinLivingEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    // from entityRenderer
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void postRenderEntity(T livingEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (!(livingEntity instanceof ZombieVillagerEntity) || !Configs.Tweakeroo.RENDER_ZOMBIE_VILLAGER_CONVERT_TIME.getBooleanValue()) {
            return;
        }

        ZombieVillagerEntity zombieVillagerEntity = (ZombieVillagerEntity) livingEntity;
        MinecraftClient client = MinecraftClient.getInstance();
        World world = livingEntity.getEntityWorld();

        // Only try to fetch the corresponding server world if the entity is in the actual client world.
        // Otherwise the entity may be for example in Litematica's schematic world.
        if (world == client.world) {
            world = WorldUtils.getBestWorld(client);

            if (world != null && world != client.world) {
                Entity entity = world.getEntityById(livingEntity.getEntityId());
                if (entity instanceof ZombieVillagerEntity) {
                    zombieVillagerEntity = (ZombieVillagerEntity) entity;
                }
            }
        }

        if (world == null) {
            return;
        }

        Text text;


        if (zombieVillagerEntity.conversionTimer <= 0) {
            return;
        } else {
            text = new LiteralText(String.format("%d", zombieVillagerEntity.conversionTimer));
        }

        double d = this.dispatcher.getSquaredDistanceToCamera(livingEntity);
        if (!(d > 4096.0D)) {
            float f = livingEntity.getHeight() / 16 * 15;
            matrixStack.push();
            matrixStack.translate(0, f, 0);
            matrixStack.multiply(this.dispatcher.getRotation());
            matrixStack.scale(-0.018F, -0.018F, 0.018F);
            matrixStack.translate(0, 0, -33);
            Matrix4f lv = matrixStack.peek().getModel();
            float g = client.options.getTextBackgroundOpacity(0.25F);
            int k = (int) (g * 255.0F) << 24;
            TextRenderer lv2 = this.getFontRenderer();
            float h = (float) (-lv2.getWidth(text) / 2);
            lv2.draw(text, h, 0, 553648127, false, lv, vertexConsumerProvider, false, k, light);
            lv2.draw(text, h, 0, -1, false, lv, vertexConsumerProvider, false, 0, light);
            matrixStack.pop();
        }
    }

}
