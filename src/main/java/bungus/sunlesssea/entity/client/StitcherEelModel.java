package bungus.sunlesssea.entity.client;

import bungus.sunlesssea.Sunlesssea;
import bungus.sunlesssea.entity.custom.StitcherEelEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class StitcherEelModel extends GeoModel<StitcherEelEntity> {
    @Override
    public ResourceLocation getModelResource(StitcherEelEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID, "geo/stitchereel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StitcherEelEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID, "textures/entity/stitchereel.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StitcherEelEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID, "animations/stitchereel.animation.json");
    }

    @Override
    public void setCustomAnimations(StitcherEelEntity animatable, long instanceId, AnimationState<StitcherEelEntity> animationState) {
        animatable.setRotationAnimation(this);
    }
}
