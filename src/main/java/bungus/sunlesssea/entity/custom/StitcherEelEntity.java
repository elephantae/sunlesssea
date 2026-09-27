package bungus.sunlesssea.entity.custom;

import bungus.sunlesssea.entity.ModEntities;
import dev.customhitboxlib.api.ICustomMultipart;
import dev.customhitboxlib.api.PartDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Map;

public class StitcherEelEntity extends WormEntity implements GeoEntity{
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int SEGMENT_COUNT = 16;
    private static final float SEGMENT_LENGTH = 1f;
    private static final float SEGMENT_ANGLE = (float)Math.toRadians(45);
    private static final Vec3 BASE_OFFSET = new Vec3(0,0.5,-0.5);
    private static final Vec3 BASE_FORWARD = new Vec3(0,0,-1);
    private static final float SEGMENT_HITBOX_WIDTH = 1;
    private static final float SEGMENT_HITBOX_HEIGHT = 1;

    public StitcherEelEntity(EntityType<? extends WormEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, SEGMENT_COUNT, SEGMENT_LENGTH, SEGMENT_ANGLE, BASE_OFFSET, BASE_FORWARD,SEGMENT_HITBOX_WIDTH,SEGMENT_HITBOX_HEIGHT);
    }
    
    public static AttributeSupplier setAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.ATTACK_SPEED,1)
                .add(Attributes.MOVEMENT_SPEED, 0.8).build();
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    protected void registerGoals(){
        super.registerGoals();
        goalSelector.addGoal(5,new RandomSwimmingGoal(this, 1, 40));
    }
}
