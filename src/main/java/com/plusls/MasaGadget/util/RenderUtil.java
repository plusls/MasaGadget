package com.plusls.MasaGadget.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

//#if MC < 11700
//$$ import org.lwjgl.opengl.GL11;
//#endif

//#if MC <= 11502
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif

public class RenderUtil {

    // 只能在 EntityRenderer.render 中调用
    public static void renderTextOnEntity(PoseStack matrixStack, Entity entity,
                                          EntityRenderDispatcher entityRenderDispatcher,
                                          Component text, float height, boolean seeThrough) {
        if (entityRenderDispatcher.distanceToSqr(entity) <= 4096.0D) {
            Position camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Position eysPos = entity.getEyePosition(0);
            float xAngle = (float) Mth.atan2(camPos.z() - eysPos.z(), camPos.x() - eysPos.x());
            float YAngle = (float) Mth.atan2(camPos.x() - eysPos.x(), camPos.z() - eysPos.z());
            matrixStack.pushPose();
            matrixStack.translate(0.6 * Mth.cos(xAngle), height, 0.6 * Mth.cos(YAngle));
            matrixStack.mulPose(entityRenderDispatcher.cameraOrientation());
            matrixStack.scale(-0.018F, -0.018F, -0.018F);
            renderText(matrixStack, text, seeThrough);
            matrixStack.popPose();
        }
    }


    // 在 LevelRenderer.renderLevel 中调用
    public static void renderTextOnWorld(PoseStack matrixStack, Camera camera, BlockPos pos,
                                         Component text, boolean seeThrough) {
        matrixStack.pushPose();
        matrixStack.translate(pos.getX() + 0.5 - camera.getPosition().x(), pos.getY() + 0.6 - camera.getPosition().y(), pos.getZ() + 0.5 - camera.getPosition().z());
        // 保证文字面向玩家
        matrixStack.mulPose(camera.rotation());
        matrixStack.scale(-0.04F, -0.04F, -0.04F);
        renderText(matrixStack, text, seeThrough);
        matrixStack.popPose();
    }

    public static void renderText(PoseStack matrixStack, Component text, boolean seeThrough) {

        //#if MC <= 11502
        //$$ // 不加的话 minihud 渲染球体时会导致 bug
        //$$ RenderSystem.disableLighting();
        //#endif
        matrixStack.pushPose();
        Minecraft client = Minecraft.getInstance();
        Matrix4f matrix4f = matrixStack.last().pose();
        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
        float xOffset = (float) (-client.font.width(text) / 2);
        // 		ARG 1 text
        //		ARG 2 x
        //		ARG 3 y
        //		ARG 4 color
        //			COMMENT the text color in the 0xAARRGGBB format
        //		ARG 5 shadow
        //		ARG 6 matrix
        //		ARG 7 vertexConsumers
        //		ARG 8 seeThrough
        //		ARG 9 backgroundColor
        //		ARG 10 light
        client.font.drawInBatch(text, xOffset, 0, 0x20ffffff, false, matrix4f, seeThrough, backgroundColor, 0xF000F0);
        matrixStack.translate(0, 0, 2);
        client.font.drawInBatch(text, xOffset, 0, 0xffffffff, false, matrix4f, seeThrough, 0, 0xF000F0);
        matrixStack.popPose();
        //#if MC <= 11502
        //$$ RenderSystem.enableLighting();
        //#endif
    }

    public static void drawConnectLine(Vec3 pos1, Vec3 pos2, double boxLength, Color4f pos1Color, Color4f pos2Color, Color4f lineColor) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        pos1 = pos1.subtract(camPos);
        pos2 = pos2.subtract(camPos);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        // Box1
        RenderUtil.beginLines(builder);
        RenderUtils.drawBoxAllEdgesBatchedLines(
                pos1.x() - boxLength, pos1.y() - boxLength, pos1.z() - boxLength,
                pos1.x() + boxLength, pos1.y() + boxLength, pos1.z() + boxLength, pos1Color, builder);
        tesselator.end();
        // Box2
        RenderUtil.beginLines(builder);
        RenderUtils.drawBoxAllEdgesBatchedLines(
                pos2.x() - boxLength, pos2.y() - boxLength, pos2.z() - boxLength,
                pos2.x() + boxLength, pos2.y() + boxLength, pos2.z() + boxLength, pos2Color, builder);
        tesselator.end();
        // Line
        RenderUtil.beginLines(builder);
        builder.vertex(pos1.x(), pos1.y(), pos1.z()).color(lineColor.r, lineColor.g, lineColor.b, lineColor.a).endVertex();
        builder.vertex(pos2.x(), pos2.y(), pos2.z()).color(lineColor.r, lineColor.g, lineColor.b, lineColor.a).endVertex();
        tesselator.end();
    }

    private static void beginLines(BufferBuilder builder) {
        //#if MC > 11700
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#else
        //$$ builder.begin(GL11.GL_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#endif
    }
}
