package com.plusls.MasaGadget.util;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.compat.minecraft.client.MinecraftCompat;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.world.phys.Vec3;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc26.2: subproject 1.16.5 (main project)</li>
 * <li>mc26.3+        : subproject 26.3        &lt;--------</li>
 */
// CHECKSTYLE.ON: JavadocStyle
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

        try (RenderContext ctx = new RenderContext(() -> "masa_gadget:line",
                MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, 0)) {
            BufferBuilder builder = ctx.getBuilder();
            builder.addVertex((float) pos1.x(), (float) pos1.y(), (float) pos1.z()).setColor(color.r, color.g, color.b, color.a).setLineWidth(1.0F);
            builder.addVertex((float) pos2.x(), (float) pos2.y(), (float) pos2.z()).setColor(color.r, color.g, color.b, color.a).setLineWidth(1.0F);

            try (MeshData meshData = builder.build()) {
                if (meshData != null) {
                    ctx.draw(meshData, false, true);
                }
            }
        } catch (Exception ignored) {
            // Match the existing renderer's handling of unavailable render contexts.
        }
    }

    public static void drawOutlineBox(Vec3 pos, double expend, Color4f color) {
        Vec3 camPos = MinecraftCompat.getInstance().getMainCameraCompat().getPosition();
        pos = pos.subtract(camPos);

        try (RenderContext ctx = new RenderContext(() -> "masa_gadget:outline_box",
                MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, 0)) {
            BufferBuilder builder = ctx.getBuilder();
            RenderUtils.drawBoxAllEdgesBatchedLines(
                    (float) (pos.x() - expend), (float) (pos.y() - expend), (float) (pos.z() - expend),
                    (float) (pos.x() + expend), (float) (pos.y() + expend), (float) (pos.z() + expend),
                    color, 1.0F, builder);

            try (MeshData meshData = builder.build()) {
                if (meshData != null) {
                    ctx.draw(meshData, false, true);
                }
            }
        } catch (Exception ignored) {
            // Match the existing renderer's handling of unavailable render contexts.
        }
    }
}
