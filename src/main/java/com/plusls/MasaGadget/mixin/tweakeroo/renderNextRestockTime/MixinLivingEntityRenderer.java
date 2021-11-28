package com.plusls.MasaGadget.mixin.tweakeroo.renderNextRestockTime;

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
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
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

    // 因为刁民的需要补货的函数，会检查当前货物是否被消耗，从使用的角度只需要关心当前货物是否用完
    private static boolean needsRestock(VillagerEntity villagerEntity) {

        for (TradeOffer offer : villagerEntity.getOffers()) {
            if (offer.isDisabled()) {
                return true;
            }
        }
        return false;
    }

    // from entityRenderer
    @Inject(method = "render*", at = @At(value = "RETURN"))
    private void postRenderEntity(T livingEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (!(livingEntity instanceof VillagerEntity) || !Configs.Tweakeroo.RENDER_NEXT_RESTOCK_TIME.getBooleanValue()) {
            return;
        }

        VillagerEntity villagerEntity = MiscUtil.getBestEntity((VillagerEntity) livingEntity);

        Text text;

        long nextRestockTime;
        long nextWorkTime;
        long timeOfDay = villagerEntity.world.getTimeOfDay() % 24000;
        if (timeOfDay >= 2000 && timeOfDay <= 9000) {
            nextWorkTime = 0;
        } else {
            nextWorkTime = timeOfDay < 2000 ? 2000 - timeOfDay : 24000 - timeOfDay + 2000;
        }
        if (villagerEntity.restocksToday == 0) {
            nextRestockTime = 0;
        } else if (villagerEntity.restocksToday < 2) {
            nextRestockTime = Math.max(villagerEntity.lastRestockTime + 2400 - villagerEntity.world.getTime(), 0);
        } else {
            nextRestockTime = 0x7fffffffffffffffL;
        }

        nextRestockTime = Math.min(nextRestockTime, Math.max(villagerEntity.lastRestockTime + 12000L - villagerEntity.world.getTime(), 0));


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
        RenderUtil.renderTextOnEntity(matrixStack, villagerEntity, this.renderManager, vertexConsumerProvider, text,
                livingEntity.getHeight() / 32 * 31);
    }

}
