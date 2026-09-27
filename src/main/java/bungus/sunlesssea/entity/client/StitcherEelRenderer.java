package bungus.sunlesssea.entity.client;

import bungus.sunlesssea.Sunlesssea;
import bungus.sunlesssea.entity.custom.StitcherEelEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StitcherEelRenderer extends GeoEntityRenderer<StitcherEelEntity> {
    public StitcherEelRenderer(EntityRendererProvider.Context context) {
        super(context, new StitcherEelModel());
    }

    @Override
    public ResourceLocation getTextureLocation(StitcherEelEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID, "textures/entity/stitchereel.png");
    }

    @Override
    public void render(StitcherEelEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()){
            poseStack.scale(0.4f,0.4f,0.4f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
