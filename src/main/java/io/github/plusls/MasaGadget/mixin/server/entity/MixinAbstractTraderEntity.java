package io.github.plusls.MasaGadget.mixin.server.entity;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.network.ClientNetworkHandler;
import io.github.plusls.MasaGadget.network.ServerNetworkHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.Trader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(AbstractTraderEntity.class)
public abstract class MixinAbstractTraderEntity extends PassiveEntity implements Npc, Trader, InventoryListener {
    @Final
    @Shadow
    private BasicInventory inventory;

    public MixinAbstractTraderEntity(World world) {
        super(null, null);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At(value = "RETURN"))
    private void postInit(EntityType<? extends AbstractTraderEntity> entityType, World world, CallbackInfo info) {
        if (this.world.isClient()) {
            return;
        }
        this.inventory.addListener(this);
    }

    @Override
    public void onInvChange(Inventory inventory) {
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
