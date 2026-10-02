package bungus.sunlesssea.event;

import bungus.sunlesssea.Sunlesssea;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Sunlesssea.MOD_ID, value = Dist.CLIENT)
public class DebugRenderHandler {

    // Initialize with an empty unmodifiable list to prevent null pointer exceptions
    private static volatile List<Tuple<Vec3,Vec3>> activePoints = Collections.emptyList();

    /**
     * Call this from your tick method to supply a fresh list of positions.
     * Pass an empty list or Collections.emptyList() to stop rendering everything.
     */
    public static void updateDebugPoints(List<Vec3> newPoints, Vec3 color) {
        // Direct assignment is safe and thread-safe thanks to volatile pointer swapping
        List<Tuple<Vec3,Vec3>> temp = new ArrayList<>();
        for(Vec3 v:newPoints){
            temp.add(new Tuple<>(v,color));
        }
        activePoints = temp;
    }
    
    public static void addDebugPoints(List<Vec3> newPoints, Vec3 color){
        List<Tuple<Vec3,Vec3>> temp = new ArrayList<>(activePoints);
        for(Vec3 v:newPoints){
            temp.add(new Tuple<>(v,color));
        }
        activePoints = temp;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // Capture a local reference snapshot of the list for this frame
        List<Tuple<Vec3, Vec3>> pointsToRender = activePoints;

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

        float size = 0.125f; // Crosshair size

        for (Tuple<Vec3,Vec3> t: pointsToRender) {
            // Guard against null entries inside the list
            if (t == null) continue;
            Vec3 point = t.getA();
            Vec3 col = t.getB();
            int r = (int)col.x;
            int g = (int)col.y;
            int b = (int)col.z;

            float renderX = (float) (point.x - cameraPos.x());
            float renderY = (float) (point.y - cameraPos.y());
            float renderZ = (float) (point.z - cameraPos.z());

            // X-axis line (Red)
            bufferBuilder.vertex(poseStack.last().pose(), renderX - size, renderY, renderZ).color(r, g, b, 255).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), renderX + size, renderY, renderZ).color(r, g, b, 255).endVertex();
            // Y-axis line (Green)
            bufferBuilder.vertex(poseStack.last().pose(), renderX, renderY - size, renderZ).color(r, g, b, 255).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), renderX, renderY + size, renderZ).color(r, g, b, 255).endVertex();
            // Z-axis line (Blue)
            bufferBuilder.vertex(poseStack.last().pose(), renderX, renderY, renderZ - size).color(r, g, b, 255).endVertex();
            bufferBuilder.vertex(poseStack.last().pose(), renderX, renderY, renderZ + size).color(r, g, b, 255).endVertex();
        }

        tesselator.end();
        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }
}