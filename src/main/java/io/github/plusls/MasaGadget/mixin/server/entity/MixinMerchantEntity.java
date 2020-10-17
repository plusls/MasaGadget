package io.github.plusls.MasaGadget.mixin.server.entity;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.network.ClientNetworkHandler;
import io.github.plusls.MasaGadget.network.ServerNetworkHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.Merchant;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(MerchantEntity.class)
public abstract class MixinMerchantEntity extends PassiveEntity implements Npc, Merchant, InventoryChangedListener {
    @Final
    @Dynamic
    @Shadow
    private SimpleInventory inventory;

    public MixinMerchantEntity(World world) {
        super(null, null);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At(value = "RETURN"))
    private void postInit(EntityType<? extends MerchantEntity> entityType, World world, CallbackInfo info) {
        if (this.world.isClient()) {
            return;
        }
        this.inventory.addListener(this);
    }

    @Override
    public void onInventoryChanged(Inventory inventory) {
        int entityId = this.getEntityId();
        for (Map.Entry<UUID, Integer> entry : ServerNetworkHandler.lastEntityUuidMap.entrySet()) {
            if (entry.getValue() == entityId) {
                PlayerEntity player = (PlayerEntity) ((ServerWorld) this.world).getEntity(entry.getKey());
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeInt(entityId);
                buf.writeCompoundTag(this.toTag(new CompoundTag()));
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ClientNetworkHandler.RESPONSE_ENTITY, buf);
                MasaGadgetMod.LOGGER.debug("update villager inventory: onInventoryChanged.");
            }
        }
    }
}
