package io.github.plusls.MasaGadget.mixin.server.block;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.network.ServerNetworkHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// 由于陷阱箱继承自箱子，因此不用 mixin 陷阱箱
// implements ChestAnimationProgress 会出错 不知道为啥
@Mixin(ChestBlockEntity.class)
public abstract class MixinChestBlockEntity extends LootableContainerBlockEntity implements Tickable {
    @Shadow
    public abstract CompoundTag toTag(CompoundTag tag);

    public MixinChestBlockEntity() {
        super(null);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        // 在生成世界时可能会产生空指针
        if (this.world == null) {
            return;
        }
        BlockState blockState = this.world.getBlockState(this.pos);
        if (ServerNetworkHandler.lastBlockPosMap.containsValue(this.pos)) {
            ((ServerWorld) this.world).getChunkManager().markForUpdate(this.getPos());
            MasaGadgetMod.LOGGER.debug("update ChestBlockEntity: {}", this.pos);
        } else if (blockState.getBlock() == Blocks.CHEST && blockState.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
            // 如果是一个大箱子需要特殊处理
            BlockPos posAdj = pos.offset(ChestBlock.getFacing(blockState));
            if (ServerNetworkHandler.lastBlockPosMap.containsValue(posAdj)) {
                ((ServerWorld) this.world).getChunkManager().markForUpdate(this.getPos());
                MasaGadgetMod.LOGGER.debug("update ChestBlockEntity: {}", this.pos);
            }
        }
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 0, this.toTag(new CompoundTag()));
    }
}
