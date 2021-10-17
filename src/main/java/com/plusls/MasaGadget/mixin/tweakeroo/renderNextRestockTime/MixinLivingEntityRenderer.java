package com.plusls.MasaGadget.mixin.tweakeroo.renderNextRestockTime;

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
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.village.TradeOffer;
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
        if (!(livingEntity instanceof VillagerEntity) || !Configs.Tweakeroo.RENDER_NEXT_RESTOCK_TIME.getBooleanValue()) {
            return;
        }

        VillagerEntity villagerEntity = (VillagerEntity) livingEntity;
        MinecraftClient client = MinecraftClient.getInstance();
        World world = livingEntity.getEntityWorld();

        // Only try to fetch the corresponding server world if the entity is in the actual client world.
        // Otherwise the entity may be for example in Litematica's schematic world.
        if (world == client.world) {
            world = WorldUtils.getBestWorld(client);

            if (world != null && world != client.world) {
                Entity entity = world.getEntityById(livingEntity.getEntityId());
                if (entity instanceof VillagerEntity) {
                    villagerEntity = (VillagerEntity) entity;
                }
            }
        }

        if (world == null) {
            return;
        }

        Text text;

        long nextRestockTime;
        long nextWorkTime;
        long timeOfDay = world.getTimeOfDay() % 24000;
        if (timeOfDay >= 2000 && timeOfDay <= 9000) {
            nextWorkTime = 0;
        } else {
            nextWorkTime = timeOfDay < 2000 ? 2000 - timeOfDay : 24000 - timeOfDay + 2000;
        }
        if (villagerEntity.restocksToday == 0) {
            nextRestockTime = 0;
        } else if (villagerEntity.restocksToday < 2) {
            nextRestockTime = Math.max(villagerEntity.lastRestockTime + 2400 - world.getTime(), 0);
        } else {
            nextRestockTime = 0x7fffffffffffffffL;
        }

        nextRestockTime = Math.min(nextRestockTime, Math.max(villagerEntity.lastRestockTime + 12000L - world.getTime(), 0));


        if (needsRestock(villagerEntity)) {
            if (timeOfDay + nextRestockTime > 8000) {
                // cd 好的时候村民已经不再工作了
                nextRestockTime = 24000 - timeOfDay + 2000;
            } else {
                nextRestockTime = Math.max(nextRestockTime, nextWorkTime);
            }
        } else {
            nextRestockTime = 0;
        }

        if (nextRestockTime == 0) {
            text = new LiteralText("OK").formatted(Formatting.GREEN);
        } else {
            text = new LiteralText(String.format("%d", nextRestockTime));
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

    // 因为刁民的需要补货的函数，会检查当前货物是否被消耗，从使用的角度只需要关心当前货物是否用完
    private static boolean needsRestock(VillagerEntity villagerEntity) {

        for (TradeOffer offer : villagerEntity.getOffers()) {
            if (offer.isDisabled()) {
                return true;
            }
        }
        return false;
    }

}
