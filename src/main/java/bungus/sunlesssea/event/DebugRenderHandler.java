package bungus.sunlesssea.event;

import bungus.sunlesssea.Sunlesssea;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Sunlesssea.MOD_ID, value = Dist.CLIENT)
public class DebugRenderHandler {

    // Initialize with an empty unmodifiable list to prevent null pointer exceptions
    private static volatile List<Vec3> activePoints = Collections.emptyList();

    /**
     * Call this from your tick method to supply a fresh list of positions.
     * Pass an empty list or Collections.emptyList() to stop rendering everything.
     */
    public static void updateDebugPoints(List<Vec3> newPoints) {
        // Direct assignment is safe and thread-safe thanks to volatile pointer swapping
        activePoints = newPoints;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // Capture a local reference snapshot of the list for this frame
        List<Vec3> pointsToRender = activePoints;

        if (pointsToRender.isEmpty()) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.gameRenderer.getMainCamera() == null) return;

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        float size = 0.25f; // Crosshair size

        for (Vec3 point : pointsToRender) {
            // Guard against null entries inside the list
            if (point == null) continue;

            float renderX = (float) (point.x - cameraPos.x());
            float renderY = (float) (point.y - cameraPos.y());
            float renderZ = (float) (point.z - cameraPos.z());

            // X-axis line (Red)
            bufferBuilder.vertex(poseStack.last().pose(), renderX - size, renderY, renderZ).color(255, 0, 0, 255).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), renderX + size, renderY, renderZ).color(255, 0, 0, 255).endVertex();
            // Y-axis line (Green)
            bufferBuilder.vertex(poseStack.last().pose(), renderX, renderY - size, renderZ).color(0, 255, 0, 255).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), renderX, renderY + size, renderZ).color(0, 255, 0, 255).endVertex();
            // Z-axis line (Blue)
            bufferBuilder.vertex(poseStack.last().pose(), renderX, renderY, renderZ - size).color(0, 0, 255, 255).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), renderX, renderY, renderZ + size).color(0, 0, 255, 255).endVertex();
        }

        tesselator.end();
        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }
}