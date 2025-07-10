package com.plusls.MasaGadget.util;

import com.mojang.blaze3d.vertex.*;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

//#if 12106 > MC && MC > 12104
//$$ import com.mojang.blaze3d.buffers.BufferUsage;
//#endif

//#if MC > 12104
//$$ import fi.dy.masa.malilib.render.MaLiLibPipelines;
//$$ import fi.dy.masa.malilib.render.RenderContext;
//#elseif MC > 11605
//$$ import net.minecraft.client.renderer.GameRenderer;
//#endif

//#if MC > 11605
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#else
import org.lwjgl.opengl.GL11;
//#endif

public class RenderUtil {
    public static void drawConnectLine(Vec3 pos1, Vec3 pos2, double expend, Color4f pos1Color, Color4f pos2Color, @NotNull Color4f lineColor) {
        RenderUtil.drawOutlineBox(pos1, expend, pos1Color);
        RenderUtil.drawLine(pos1, pos2, lineColor);
        RenderUtil.drawOutlineBox(pos2, expend, pos2Color);
    }

    public static void drawLine(Vec3 pos1, Vec3 pos2, Color4f color) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        pos1 = pos1.subtract(camPos);
        pos2 = pos2.subtract(camPos);
        //#if MC > 12104
        //$$ RenderContext ctx = new RenderContext(
        //$$         //#if MC >= 12107
        //$$         //$$ () -> "masa_gadget:line",
        //$$         //#endif
        //$$         MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL
        //$$         //#if MC < 12106
        //$$         , BufferUsage.STATIC_WRITE
        //$$         //#endif
        //$$ );
        //$$ BufferBuilder builder = ctx.getBuilder();
        //#else
        Tesselator tesselator = Tesselator.getInstance();
        //#if MC > 12006
        //$$ BufferBuilder builder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#else
        BufferBuilder builder = tesselator.getBuilder();
        RenderUtil.beginLines(builder);
        //#endif
        //#endif
        //#if MC > 12006
        //$$ builder.addVertex((float) pos1.x(), (float) pos1.y(), (float) pos1.z()).setColor(color.r, color.g, color.b, color.a);
        //$$ builder.addVertex((float) pos2.x(), (float) pos2.y(), (float) pos2.z()).setColor(color.r, color.g, color.b, color.a);
        //#if MC > 12104
        //$$
        //$$ try {
        //$$     MeshData meshData = builder.build();
        //$$
        //$$     if (meshData != null) {
        //$$         ctx.draw(meshData, false, true);
        //$$         meshData.close();
        //$$     }
        //$$
        //$$     ctx.close();
        //$$ } catch (Exception ignored) {
        //$$ }
        //#else
        //$$ RenderUtil.end(builder);
        //#endif
        //#else
        builder.vertex(pos1.x(), pos1.y(), pos1.z()).color(color.r, color.g, color.b, color.a).endVertex();
        builder.vertex(pos2.x(), pos2.y(), pos2.z()).color(color.r, color.g, color.b, color.a).endVertex();
        tesselator.end();
        //#endif
    }

    public static void drawOutlineBox(Vec3 pos, double expend, Color4f color) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        pos = pos.subtract(camPos);

        //#if MC > 12104
        //$$ RenderContext ctx = new RenderContext(
        //$$         //#if MC >= 12107
        //$$         //$$ () -> "masa_gadget:outline_box",
        //$$         //#endif
        //$$         MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL
        //$$         //#if MC < 12106
        //$$         , BufferUsage.STATIC_WRITE
        //$$         //#endif
        //$$ );
        //$$ BufferBuilder builder = ctx.getBuilder();
        //#else
        Tesselator tesselator = Tesselator.getInstance();
        //#if MC > 12006
        //$$ BufferBuilder builder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#else
        BufferBuilder builder = tesselator.getBuilder();
        RenderUtil.beginLines(builder);
        //#endif
        //#endif
        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos.x() - expend),
                (float) (pos.y() - expend),
                (float) (pos.z() - expend),
                (float) (pos.x() + expend),
                (float) (pos.y() + expend),
                (float) (pos.z() + expend),
                color,
                builder
        );
        //#if MC > 12104
        //$$
        //$$ try {
        //$$     MeshData meshData = builder.build();
        //$$
        //$$     if (meshData != null) {
        //$$         ctx.draw(meshData, false, true);
        //$$         meshData.close();
        //$$     }
        //$$
        //$$     ctx.close();
        //$$ } catch (Exception ignored) {
        //$$ }
        //#elseif MC > 12006
        //$$ RenderUtil.end(builder);
        //#else
        tesselator.end();
        //#endif
    }

    //#if MC < 12105
    //#if MC > 12006
    //$$ private static void end(BufferBuilder builder) {
    //$$     try (MeshData meshData = builder.buildOrThrow()) {
    //$$         BufferUploader.drawWithShader(meshData);
    //$$     } catch (Exception ignore) {
    //$$     }
    //$$ }
    //#else
    private static void beginLines(BufferBuilder builder) {
        //#if MC > 11700
        //$$ RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //$$ builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#else
        builder.begin(GL11.GL_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#endif
    }
    //#endif
    //#endif
}
