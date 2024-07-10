package com.plusls.MasaGadget.util;

import com.mojang.blaze3d.vertex.*;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

//#if MC < 11700
import org.lwjgl.opengl.GL11;
//#endif

//#if MC > 11404
import com.mojang.blaze3d.systems.RenderSystem;
//#endif

public class RenderUtil {
    public static void drawConnectLine(Vec3 pos1, Vec3 pos2, double boxLength, Color4f pos1Color, Color4f pos2Color, @NotNull Color4f lineColor) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        pos1 = pos1.subtract(camPos);
        pos2 = pos2.subtract(camPos);
        Tesselator tesselator = Tesselator.getInstance();
        //#if MC > 12006
        //$$ BufferBuilder builder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#else
        BufferBuilder builder = tesselator.getBuilder();
        // Box1
        RenderUtil.beginLines(builder);
        //#endif
        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos1.x() - boxLength),
                (float) (pos1.y() - boxLength),
                (float) (pos1.z() - boxLength),
                (float) (pos1.x() + boxLength),
                (float) (pos1.y() + boxLength),
                (float) (pos1.z() + boxLength),
                pos1Color,
                builder
        );
        //#if MC > 12006
        //$$ RenderUtil.end(builder);
        //$$ builder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#else
        tesselator.end();
        // Box2
        RenderUtil.beginLines(builder);
        //#endif
        RenderUtils.drawBoxAllEdgesBatchedLines(
                (float) (pos2.x() - boxLength),
                (float) (pos2.y() - boxLength),
                (float) (pos2.z() - boxLength),
                (float) (pos2.x() + boxLength),
                (float) (pos2.y() + boxLength),
                (float) (pos2.z() + boxLength),
                pos2Color,
                builder
        );
        //#if MC > 12006
        //$$ RenderUtil.end(builder);
        //$$ builder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#else
        tesselator.end();
        // Line
        RenderUtil.beginLines(builder);
        //#endif
        //#if MC > 12006
        //$$ builder.addVertex((float) pos1.x(), (float) pos1.y(), (float) pos1.z()).setColor(lineColor.r, lineColor.g, lineColor.b, lineColor.a);
        //$$ builder.addVertex((float) pos2.x(), (float) pos2.y(), (float) pos2.z()).setColor(lineColor.r, lineColor.g, lineColor.b, lineColor.a);
        //$$ RenderUtil.end(builder);
        //$$ builder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        //#else
        builder.vertex(pos1.x(), pos1.y(), pos1.z()).color(lineColor.r, lineColor.g, lineColor.b, lineColor.a).endVertex();
        builder.vertex(pos2.x(), pos2.y(), pos2.z()).color(lineColor.r, lineColor.g, lineColor.b, lineColor.a).endVertex();
        tesselator.end();
        //#endif
    }

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
}
