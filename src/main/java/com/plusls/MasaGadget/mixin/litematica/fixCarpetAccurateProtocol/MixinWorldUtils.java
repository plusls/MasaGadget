package com.plusls.MasaGadget.mixin.litematica.fixCarpetAccurateProtocol;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

import java.util.Objects;

//#if MC <= 11502
//$$ import net.minecraft.world.item.UseOnContext;
//#endif


@SuppressWarnings("DefaultAnnotationParam")
@Dependencies(and = @Dependency(ModInfo.LITEMATICA_MOD_ID))
@Mixin(value = WorldUtils.class, priority = 900, remap = false)
public class MixinWorldUtils {

    private static final ThreadLocal<Direction> easyPlaceActionNewSide = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Float> easyPlaceActionOldYaw = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Integer> interactBlockCount = ThreadLocal.withInitial(() -> null);


    @Inject(method = "applyCarpetProtocolHitVec", at = @At(value = "HEAD"), cancellable = true)
    private static void preApplyCarpetProtocolHitVec(BlockPos pos, BlockState state, Vec3 hitVecIn, CallbackInfoReturnable<Vec3> cir) {
        if (!Configs.fixAccurateProtocol) {
            return;
        }
        double x = hitVecIn.x;
        double y = hitVecIn.y;
        double z = hitVecIn.z;
        Block block = state.getBlock();
        Direction facing = fi.dy.masa.malilib.util.BlockUtils.getFirstPropertyFacingValue(state);
        // 应该是 32 而不是 16
        final int propertyIncrement = 32;
        double relX = hitVecIn.x - pos.getX();

        if (facing != null) {
            x = pos.getX() + relX + 2 + (facing.get3DDataValue() * 2);
        }

        if (block instanceof RepeaterBlock) {
            // 这里也实现错了，不应该是 DELAY - 1
            x += ((state.getValue(RepeaterBlock.DELAY))) * propertyIncrement;
        } else if (block instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
            x += propertyIncrement;
        } else if (block instanceof ComparatorBlock && state.getValue(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT) {
            x += propertyIncrement;
        } else if (block instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.TOP) {
            x += propertyIncrement;
        } else if (block instanceof SlabBlock && state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE) {
            //x += 10; // Doesn't actually exist (yet?)

            // Do it via vanilla
            if (state.getValue(SlabBlock.TYPE) == SlabType.TOP) {
                y = pos.getY() + 0.9;
            } else {
                y = pos.getY();
            }
        }
        ModInfo.LOGGER.debug("applyCarpetProtocolHitVec: {} -> {}", hitVecIn, new Vec3(x, y, z).toString());
        cir.setReturnValue(new Vec3(x, y, z));
    }

    // 修复 漏斗，原木放置问题
    // 核心思路是修改玩家看的位置以及 side
    @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/litematica/util/WorldUtils;cacheEasyPlacePosition(Lnet/minecraft/core/BlockPos;)V", ordinal = 0, remap = true),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void fixDoEasyPlaceAction0(Minecraft mc, CallbackInfoReturnable<InteractionResult> cir, RayTraceUtils.RayTraceWrapper traceWrapper) {
        if (!Configs.fixAccurateProtocol) {
            return;
        }
        BlockHitResult trace = Objects.requireNonNull(traceWrapper).getBlockHitResult();
        BlockPos pos = Objects.requireNonNull(trace).getBlockPos();
        Level world = Objects.requireNonNull(SchematicWorldHandler.getSchematicWorld());
        BlockState stateSchematic = world.getBlockState(pos);
        //#if MC > 11404
        ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(stateSchematic);
        //#else
        //$$ ItemStack stack = MaterialCache.getInstance().getItemForState(stateSchematic);
        //#endif
        InteractionHand hand = EntityUtils.getUsedHandForItem(Objects.requireNonNull(mc.player), stack);
        Vec3 hitPos = trace.getLocation();


        Direction newSide = BlockUtils.getFirstPropertyFacingValue(stateSchematic);
        easyPlaceActionNewSide.set(newSide);
        easyPlaceActionOldYaw.set(mc.player.getYRot());
        if (newSide == null && stateSchematic.hasProperty(BlockStateProperties.AXIS)) {
            // 原木之类的
            newSide = Direction.fromAxisAndDirection(stateSchematic.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
            easyPlaceActionNewSide.set(newSide);
        }
        if (newSide != null && !(stateSchematic.getBlock() instanceof SlabBlock)) {
            // fuck mojang
            // 有时候放的东西是反向的,需要特判
            mc.player.setYRot(newSide.toYRot());
            ItemStack itemStack = new ItemStack(stateSchematic.getBlock().asItem());
            //#if MC > 11502
            BlockPlaceContext itemPlacementContext = new BlockPlaceContext(mc.player, hand, itemStack, new BlockHitResult(hitPos, newSide, pos, false));
            //#else
            //$$ BlockPlaceContext itemPlacementContext = new BlockPlaceContext(new UseOnContext(mc.player, hand, new BlockHitResult(hitPos, newSide, pos, false)));
            //#endif
            BlockState testState = stateSchematic.getBlock().getStateForPlacement(itemPlacementContext);
            if (testState != null) {
                Direction testDirection = BlockUtils.getFirstPropertyFacingValue(testState);
                if (testDirection != null && testDirection != newSide) {
                    newSide = newSide.getOpposite();
                    easyPlaceActionNewSide.set(newSide);
                    mc.player.setYRot(newSide.toYRot());
                }
            }
            mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(mc.player.getYRot(), mc.player.getXRot(), mc.player.isOnGround()));
        }

        if (stateSchematic.getBlock() instanceof FenceGateBlock && stateSchematic.getValue(BlockStateProperties.OPEN)) {
            interactBlockCount.set(1);
        }
    }

    @ModifyArg(method = "doEasyPlaceAction",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/BlockHitResult;<init>(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Z)V",
                    ordinal = 0, remap = true),
            index = 1)
    private static Direction modifySide(Direction side) {
        if (Configs.fixAccurateProtocol && easyPlaceActionNewSide.get() != null) {
            side = easyPlaceActionNewSide.get();
        }
        return side;
    }

    //#if MC <= 11802
    @Redirect(method = "doEasyPlaceAction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            ordinal = 0, remap = true))
    private static InteractionResult myInteractBlock(MultiPlayerGameMode clientPlayerInteractionManager, LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hitResult) {
        InteractionResult ret = clientPlayerInteractionManager.useItemOn(player, world, hand, hitResult);
        if (!Configs.fixAccurateProtocol || interactBlockCount.get() == null) {
            return ret;
        }
        for (int i = 0; i < interactBlockCount.get(); ++i) {
            clientPlayerInteractionManager.useItemOn(player, world, hand, hitResult);
        }
        interactBlockCount.set(null);
        return ret;
    }

    @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            shift = At.Shift.AFTER, ordinal = 0, remap = true))
    private static void fixDoEasyPlaceAction1(Minecraft mc, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Configs.fixAccurateProtocol) {
            return;
        }

        // 让玩家看回原来的位置
        if (easyPlaceActionOldYaw.get() != null) {
            Objects.requireNonNull(mc.player).setYRot(easyPlaceActionOldYaw.get());
            mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(mc.player.getYRot(), mc.player.getXRot(), mc.player.isOnGround()));
        }
    }
    //#endif
}
