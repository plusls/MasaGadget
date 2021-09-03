package com.plusls.MasaGadget.mixin.litematica.fixCarpetAccurateProtocol;

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@NeedObfuscate(packageName = "com.plusls.MasaGadget.mixin")
@Dependencies(dependencyList = @Dependency(modId = "litematica", version = "*"))
@Mixin(value = WorldUtils.class, priority = 900, remap = false)
public class MixinWorldUtils {

    private static final ThreadLocal<Direction> easyPlaceActionNewSide = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Float> easyPlaceActionOldYaw = ThreadLocal.withInitial(() -> null);


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
        BlockState debugState = debug(block, pos, new Vec3d(x, y, z));
        if (debugState != null)
            ModInfo.LOGGER.debug("BlockState: {}", debugState.toString());

        cir.setReturnValue(new Vec3d(x, y, z));
    }

    private static BlockState debug(Block block, BlockPos pos, Vec3d hitVecIn) {
        //actual alternative block placement code
        //ItemPlacementContext context = new ItemPlacementContext(new ItemUsageContext(MinecraftClient.getInstance().player, Hand.MAIN_HAND, blockHitResult));

        Direction facing;
        Vec3d vec3d = hitVecIn;
        float hitX = (float) vec3d.x - pos.getX();
        if (hitX < 2) // vanilla
            return null;
        int code = (int) (hitX - 2) / 2;
        //
        // now it would be great if hitX was adjusted in context to original range from 0.0 to 1.0
        // since its actually using it. Its private - maybe with Reflections?
        //
        PlayerEntity placer = MinecraftClient.getInstance().player;
        World world = MinecraftClient.getInstance().world;

        if (block instanceof GlazedTerracottaBlock) {
            facing = Direction.byId(code);
            if (facing == Direction.UP || facing == Direction.DOWN) {
                facing = placer.getHorizontalFacing().getOpposite();
            }
            return block.getDefaultState().with(HorizontalFacingBlock.FACING, facing);
        } else if (block instanceof ObserverBlock) {
            return block.getDefaultState()
                    .with(FacingBlock.FACING, Direction.byId(code))
                    .with(ObserverBlock.POWERED, true);
        } else if (block instanceof RepeaterBlock) {
            facing = Direction.byId(code % 16);
            if (facing == Direction.UP || facing == Direction.DOWN) {
                facing = placer.getHorizontalFacing().getOpposite();
            }
            ModInfo.LOGGER.debug("RepeaterBlock facing: {} {} delay: {}", Direction.byId(code % 16), facing.toString(), MathHelper.clamp(code / 16, 1, 4));
            return block.getDefaultState()
                    .with(HorizontalFacingBlock.FACING, facing)
                    .with(RepeaterBlock.DELAY, MathHelper.clamp(code / 16, 1, 4))
                    .with(RepeaterBlock.LOCKED, Boolean.FALSE);
        } else if (block instanceof TrapdoorBlock) {
            facing = Direction.byId(code % 16);
            if (facing == Direction.UP || facing == Direction.DOWN) {
                facing = placer.getHorizontalFacing().getOpposite();
            }
            return block.getDefaultState()
                    .with(TrapdoorBlock.FACING, facing)
                    .with(TrapdoorBlock.OPEN, Boolean.FALSE)
                    .with(TrapdoorBlock.HALF, (code >= 16) ? BlockHalf.TOP : BlockHalf.BOTTOM)
                    .with(TrapdoorBlock.OPEN, world.isReceivingRedstonePower(pos));
        } else if (block instanceof ComparatorBlock) {
            facing = Direction.byId(code % 16);
            if ((facing == Direction.UP) || (facing == Direction.DOWN)) {
                facing = placer.getHorizontalFacing().getOpposite();
            }
            ComparatorMode m = (hitX >= 16) ? ComparatorMode.SUBTRACT : ComparatorMode.COMPARE;
            return block.getDefaultState()
                    .with(HorizontalFacingBlock.FACING, facing)
                    .with(ComparatorBlock.POWERED, Boolean.FALSE)
                    .with(ComparatorBlock.MODE, m);
        } else if (block instanceof DispenserBlock) {
            return block.getDefaultState()
                    .with(DispenserBlock.FACING, Direction.byId(code))
                    .with(DispenserBlock.TRIGGERED, Boolean.FALSE);
        } else if (block instanceof PistonBlock) {
            return block.getDefaultState()
                    .with(FacingBlock.FACING, Direction.byId(code))
                    .with(PistonBlock.EXTENDED, Boolean.FALSE);
        } else if (block instanceof StairsBlock) {
            return block.getDefaultState()//worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
                    .with(StairsBlock.FACING, Direction.byId(code % 16))
                    .with(StairsBlock.HALF, (hitX >= 16) ? BlockHalf.TOP : BlockHalf.BOTTOM);
        }
        return null;
    }

    @Shadow(remap = false)
    private static boolean easyPlaceBlockChecksCancel(BlockState stateSchematic, BlockState stateClient, PlayerEntity player, HitResult trace, ItemStack stack) {
        return false;
    }

    @Shadow(remap = false)
    private static Direction applyPlacementFacing(BlockState stateSchematic, Direction side, BlockState stateClient) {
        return null;
    }

    @Shadow(remap = false)
    private static void cacheEasyPlacePosition(BlockPos pos) {
    }

    @Shadow(remap = false)
    private static boolean placementRestrictionInEffect(MinecraftClient mc) {
        return false;
    }


    /**
     * @author plusls
     * @reason fix carpet accurate protocol
     * it works
     */
//    @Overwrite(remap = false)
//    private static ActionResult doEasyPlaceAction(MinecraftClient mc) {
//        RayTraceUtils.RayTraceWrapper traceWrapper = RayTraceUtils.getGenericTrace(mc.world, mc.player, 6.0D, true);
//        if (traceWrapper == null) {
//            return ActionResult.PASS;
//        } else if (traceWrapper.getHitType() == RayTraceUtils.RayTraceWrapper.HitType.SCHEMATIC_BLOCK) {
//            BlockHitResult trace = traceWrapper.getBlockHitResult();
//            HitResult traceVanilla = RayTraceUtils.getRayTraceFromEntity(mc.world, mc.player, false, 6.0D);
//            BlockPos pos = trace.getBlockPos();
//            World world = SchematicWorldHandler.getSchematicWorld();
//            BlockState stateSchematic = world.getBlockState(pos);
//            ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(stateSchematic);
//            if (WorldUtils.easyPlaceIsPositionCached(pos)) {
//                return ActionResult.FAIL;
//            } else {
//                if (!stack.isEmpty()) {
//                    BlockState stateClient = mc.world.getBlockState(pos);
//                    if (stateSchematic == stateClient) {
//                        return ActionResult.FAIL;
//                    }
//
//                    if (easyPlaceBlockChecksCancel(stateSchematic, stateClient, mc.player, traceVanilla, stack)) {
//                        return ActionResult.FAIL;
//                    }
//
//                    if (!WorldUtils.doSchematicWorldPickBlock(true, mc)) {
//                        return ActionResult.FAIL;
//                    }
//
//                    Hand hand = EntityUtils.getUsedHandForItem(mc.player, stack);
//                    if (hand == null) {
//                        return ActionResult.FAIL;
//                    }
//
//                    Vec3d hitPos = trace.getPos();
//                    Direction sideOrig = trace.getSide();
//                    if (traceVanilla != null && traceVanilla.getType() == HitResult.Type.BLOCK) {
//                        BlockHitResult hitResult = (BlockHitResult) traceVanilla;
//                        BlockPos posVanilla = hitResult.getBlockPos();
//                        Direction sideVanilla = hitResult.getSide();
//                        BlockState stateVanilla = mc.world.getBlockState(posVanilla);
//                        Vec3d hit = traceVanilla.getPos();
//                        ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(mc.player, hand, hitResult));
//                        if (!stateVanilla.canReplace(ctx)) {
//                            posVanilla = posVanilla.offset(sideVanilla);
//                            if (pos.equals(posVanilla)) {
//                                hitPos = hit;
//                                sideOrig = sideVanilla;
//                            }
//                        }
//                    }
//
//                    Direction side = applyPlacementFacing(stateSchematic, sideOrig, stateClient);
//                    hitPos = WorldUtils.applyCarpetProtocolHitVec(pos, stateSchematic, hitPos);
//
//                    // 更改从这开始
//                    // 由于人比较懒，直接 overwrite 了
//                    // 出问题了再改吧
//                    // 核心思路是修改玩家看的位置以及 side
//                    // TODO
//                    Direction newSide = BlockUtils.getFirstPropertyFacingValue(stateSchematic);
//                    float oldYaw = mc.player.getYaw();
//                    if (newSide == null && stateSchematic.contains(Properties.AXIS)) {
//                        // 原木之类的
//                        newSide = Direction.from(stateSchematic.get(Properties.AXIS), Direction.AxisDirection.POSITIVE);
//
//                    }
//                    if (newSide != null && !(stateSchematic.getBlock() instanceof SlabBlock)) {
//                        // fuck mojang
//                        // 有时候放的东西是反向的,需要特判
//                        side = newSide;
//                        mc.player.setYaw(side.asRotation());
//                        ItemStack itemStack = new ItemStack(stateSchematic.getBlock().asItem());
//                        ItemPlacementContext itemPlacementContext = new ItemPlacementContext(mc.player, hand, itemStack, new BlockHitResult(hitPos, side, pos, false));
//                        BlockState testState = stateSchematic.getBlock().getPlacementState(itemPlacementContext);
//                        if (testState != null) {
//                            Direction testDirection = BlockUtils.getFirstPropertyFacingValue(testState);
//                            if (testDirection != null && testDirection != side) {
//                                side = side.getOpposite();
//                                mc.player.setYaw(side.asRotation());
//                            }
//                        }
//                        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
//                    }
//                    // 结束
//                    cacheEasyPlacePosition(pos);
//                    BlockHitResult hitResult = new BlockHitResult(hitPos, side, pos, false);
//
//                    assert mc.interactionManager != null;
//                    mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
//
//                    // 让玩家看回原来的位置
//                    if (newSide != null) {
//                        mc.player.setYaw(oldYaw);
//                        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
//                    }
//                    // 结束
//                    if (stateSchematic.getBlock() instanceof SlabBlock && stateSchematic.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
//                        stateClient = mc.world.getBlockState(pos);
//                        if (stateClient.getBlock() instanceof SlabBlock && stateClient.get(SlabBlock.TYPE) != SlabType.DOUBLE) {
//                            side = applyPlacementFacing(stateSchematic, sideOrig, stateClient);
//                            hitResult = new BlockHitResult(hitPos, side, pos, false);
//                            mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
//                        }
//                    }
//                }
//
//                return ActionResult.SUCCESS;
//            }
//        } else if (traceWrapper.getHitType() == RayTraceUtils.RayTraceWrapper.HitType.VANILLA_BLOCK) {
//            return placementRestrictionInEffect(mc) ? ActionResult.FAIL : ActionResult.PASS;
//        } else {
//            return ActionResult.PASS;
//        }
//    }

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

    @Inject(method = "doEasyPlaceAction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", ordinal = 0))
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
