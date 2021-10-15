package com.plusls.MasaGadget.mixin.litematica.fixCarpetAccurateProtocol;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.mixin.NeedObfuscate;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.util.BlockUtils;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@NeedObfuscate(packageName = "com.plusls.MasaGadget.mixin")
@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.LITEMATICA_MOD_ID, version = "*"))
@Mixin(value = WorldUtils.class, priority = 900, remap = false)
public class MixinWorldUtils {

    private static final ThreadLocal<Direction> easyPlaceActionNewSide = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Float> easyPlaceActionOldYaw = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Integer> interactBlockCount = ThreadLocal.withInitial(() -> null);


    @Inject(method = "applyCarpetProtocolHitVec", at = @At(value = "HEAD"), cancellable = true)
    private static void preApplyCarpetProtocolHitVec(BlockPos pos, BlockState state, Vec3d hitVecIn, CallbackInfoReturnable<Vec3d> cir) {
        if (!Configs.Litematica.FIX_ACCURATE_PROTOCOL.getBooleanValue()) {
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
            x = pos.getX() + relX + 2 + (facing.getId() * 2);
        }

        if (block instanceof RepeaterBlock) {
            // 这里也实现错了，不应该是 DELAY - 1
            x += ((state.get(RepeaterBlock.DELAY))) * propertyIncrement;
        } else if (block instanceof TrapdoorBlock && state.get(TrapdoorBlock.HALF) == BlockHalf.TOP) {
            x += propertyIncrement;
        } else if (block instanceof ComparatorBlock && state.get(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT) {
            x += propertyIncrement;
        } else if (block instanceof StairsBlock && state.get(StairsBlock.HALF) == BlockHalf.TOP) {
            x += propertyIncrement;
        } else if (block instanceof SlabBlock && state.get(SlabBlock.TYPE) != SlabType.DOUBLE) {
            //x += 10; // Doesn't actually exist (yet?)

            // Do it via vanilla
            if (state.get(SlabBlock.TYPE) == SlabType.TOP) {
                y = pos.getY() + 0.9;
            } else {
                y = pos.getY();
            }
        }
        ModInfo.LOGGER.debug("applyCarpetProtocolHitVec: {} -> {}", hitVecIn, new Vec3d(x, y, z).toString());
        cir.setReturnValue(new Vec3d(x, y, z));
    }

    // 修复 漏斗，原木放置问题
    // 核心思路是修改玩家看的位置以及 side
    @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/util/WorldUtils;cacheEasyPlacePosition(Lnet/minecraft/util/math/BlockPos;)V", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void fixDoEasyPlaceAction0(MinecraftClient mc, CallbackInfoReturnable<ActionResult> cir, RayTraceUtils.RayTraceWrapper traceWrapper) {
        if (!Configs.Litematica.FIX_ACCURATE_PROTOCOL.getBooleanValue()) {
            return;
        }
        BlockHitResult trace = Objects.requireNonNull(traceWrapper).getBlockHitResult();
        BlockPos pos = Objects.requireNonNull(trace).getBlockPos();
        World world = Objects.requireNonNull(SchematicWorldHandler.getSchematicWorld());
        BlockState stateSchematic = world.getBlockState(pos);
        ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(stateSchematic);
        Hand hand = EntityUtils.getUsedHandForItem(Objects.requireNonNull(mc.player), stack);
        Vec3d hitPos = trace.getPos();


        Direction newSide = BlockUtils.getFirstPropertyFacingValue(stateSchematic);
        easyPlaceActionNewSide.set(newSide);
        easyPlaceActionOldYaw.set(mc.player.getYaw());
        if (newSide == null && stateSchematic.contains(Properties.AXIS)) {
            // 原木之类的
            newSide = Direction.from(stateSchematic.get(Properties.AXIS), Direction.AxisDirection.POSITIVE);
            easyPlaceActionNewSide.set(newSide);
        }
        if (newSide != null && !(stateSchematic.getBlock() instanceof SlabBlock)) {
            // fuck mojang
            // 有时候放的东西是反向的,需要特判
            mc.player.setYaw(newSide.asRotation());
            ItemStack itemStack = new ItemStack(stateSchematic.getBlock().asItem());
            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(mc.player, hand, itemStack, new BlockHitResult(hitPos, newSide, pos, false));
            BlockState testState = stateSchematic.getBlock().getPlacementState(itemPlacementContext);
            if (testState != null) {
                Direction testDirection = BlockUtils.getFirstPropertyFacingValue(testState);
                if (testDirection != null && testDirection != newSide) {
                    newSide = newSide.getOpposite();
                    easyPlaceActionNewSide.set(newSide);
                    mc.player.setYaw(newSide.asRotation());
                }
            }
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
        }

        if (stateSchematic.getBlock() instanceof FenceGateBlock && stateSchematic.get(Properties.OPEN)) {
            interactBlockCount.set(1);
        }
    }

    @ModifyArg(method = "doEasyPlaceAction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/hit/BlockHitResult;<init>(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;Z)V", ordinal = 0),
            index = 1)
    private static Direction modifySide(Direction side) {
        if (Configs.Litematica.FIX_ACCURATE_PROTOCOL.getBooleanValue() && easyPlaceActionNewSide.get() != null) {
            side = easyPlaceActionNewSide.get();
        }
        return side;
    }

    @Redirect(method = "doEasyPlaceAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", ordinal = 0))
    private static ActionResult myInteractBlock(ClientPlayerInteractionManager clientPlayerInteractionManager, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult) {
        ActionResult ret = clientPlayerInteractionManager.interactBlock(player, world, hand, hitResult);
        if (!Configs.Litematica.FIX_ACCURATE_PROTOCOL.getBooleanValue() || interactBlockCount.get() == null) {
            return ret;
        }
        for (int i = 0; i < interactBlockCount.get(); ++i) {
            clientPlayerInteractionManager.interactBlock(player, world, hand, hitResult);
        }
        interactBlockCount.set(null);
        return ret;
    }

    @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", shift = At.Shift.AFTER, ordinal = 0))
    private static void fixDoEasyPlaceAction1(MinecraftClient mc, CallbackInfoReturnable<ActionResult> cir) {
        if (!Configs.Litematica.FIX_ACCURATE_PROTOCOL.getBooleanValue()) {
            return;
        }

        // 让玩家看回原来的位置
        if (easyPlaceActionOldYaw.get() != null) {
            Objects.requireNonNull(mc.player).setYaw(easyPlaceActionOldYaw.get());
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
        }
    }
}
