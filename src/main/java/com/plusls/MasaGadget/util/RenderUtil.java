package com.plusls.MasaGadget.util;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.compat.minecraft.client.MinecraftCompat;

// CHECKSTYLE.OFF: ImportOrder
//#if MC > 12104
//$$ import fi.dy.masa.malilib.render.MaLiLibPipelines;
//$$ import fi.dy.masa.malilib.render.RenderContext;
//#else
import top.hendrixshen.magiclib.api.compat.mojang.blaze3d.vertex.VertexFormatCompat;
//#endif
// CHECKSTYLE.ON: ImportOrder

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.phys.Vec3;

// CHECKSTYLE.OFF: ImportOrder
//#if 12106 > MC && MC > 12104
//$$ import com.mojang.blaze3d.buffers.BufferUsage;
//#endif

//#if MC > 12006
//$$ import com.mojang.blaze3d.vertex.MeshData;
//#endif

//#if MC < 12105
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
//#endif

//#if 12105 > MC && MC > 12006
//$$ import com.mojang.blaze3d.vertex.BufferUploader;
//#endif

//#if 12100 > MC && MC > 11605
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import net.minecraft.client.renderer.GameRenderer;
//#endif
// CHECKSTYLE.ON: ImportOrder

public class RenderUtil {
    public static void drawConnectLine(Vec3 pos1, Vec3 pos2, double expend, Color4f pos1Color, Color4f pos2Color, @NotNull Color4f lineColor) {
        RenderUtil.drawOutlineBox(pos1, expend, pos1Color);
        RenderUtil.drawLine(pos1, pos2, lineColor);
        RenderUtil.drawOutlineBox(pos2, expend, pos2Color);
    }

    public static void drawLine(Vec3 pos1, Vec3 pos2, Color4f color) {
        Vec3 camPos = MinecraftCompat.getInstance().getMainCameraCompat().getPosition();
        pos1 = pos1.subtract(camPos);
        pos2 = pos2.subtract(camPos);
        //#if MC > 12104
        //$$ RenderContext ctx = new RenderContext(
        //$$         // CHECKSTYLE.OFF: NoWhitespaceBefore
        //$$         // CHECKSTYLE.OFF: SeparatorWrap
        //$$         //#if MC >= 12107
        //$$         //$$ () -> "masa_gadget:line",
        //$$         //#endif
        //$$         MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL
        //$$         //#if MC >= 26.2
        //$$         //$$ , 0
        //$$         //#endif
        //$$         //#if MC < 12106
        //$$         , BufferUsage.STATIC_WRITE
        //$$         //#endif
        //$$         // CHECKSTYLE.ON: SeparatorWrap
        //$$         // CHECKSTYLE.ON: NoWhitespaceBefore
        //$$ );
        //$$ BufferBuilder builder = ctx.getBuilder();
        //#else
        Tesselator tesselator = Tesselator.getInstance();
        //#if MC > 12006
        //$$ BufferBuilder builder = tesselator.begin(VertexFormatCompat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
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
        //$$     // ignore
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
        Vec3 camPos = MinecraftCompat.getInstance().getMainCameraCompat().getPosition();
        pos = pos.subtract(camPos);

        //#if MC > 12104
        //$$ RenderContext ctx = new RenderContext(
        //$$         // CHECKSTYLE.OFF: NoWhitespaceBefore
        //$$         // CHECKSTYLE.OFF: SeparatorWrap
        //$$         //#if MC >= 12107
        //$$         //$$ () -> "masa_gadget:outline_box",
        //$$         //#endif
        //$$         MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL
        //$$         //#if MC >= 26.2
        //$$         //$$ , 0
        //$$         //#endif
        //$$         //#if MC < 12106
        //$$         , BufferUsage.STATIC_WRITE
        //$$         //#endif
        //$$         // CHECKSTYLE.ON: SeparatorWrap
        //$$         // CHECKSTYLE.ON: NoWhitespaceBefore
        //$$ );
        //$$ BufferBuilder builder = ctx.getBuilder();
        //#else
        Tesselator tesselator = Tesselator.getInstance();
        //#if MC > 12006
        //$$ BufferBuilder builder = tesselator.begin(VertexFormatCompat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
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
                //#if MC >= 1.21.11
                //$$ 1.0F,
                //#endif
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
        //$$     // ignore
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
    //$$         // ignore
    //$$     }
    //$$ }
    //#else
    private static void beginLines(BufferBuilder builder) {
        //#if MC > 11700
        //$$ RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //#endif
        builder.begin(VertexFormatCompat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
    }
    //#endif
    //#endif
}
