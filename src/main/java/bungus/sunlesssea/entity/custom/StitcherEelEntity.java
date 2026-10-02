package bungus.sunlesssea.entity.custom;

import bungus.sunlesssea.entity.custom.common.SlitheringWormBehaviour;
import bungus.sunlesssea.entity.custom.common.SwimmingWormBehaviour;
import bungus.sunlesssea.entity.custom.common.WormBehaviour;
import bungus.sunlesssea.entity.custom.common.WormEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class StitcherEelEntity extends WormEntity{
    private static final int SEGMENT_COUNT = 16;
    private static final float SEGMENT_LENGTH = 1f;
    private static final float SEGMENT_ANGLE = (float) Math.toRadians(45);
    private static final Vec3 BASE_OFFSET = new Vec3(0,0,-0.5);
    private static final Vec3 BASE_PIVOT = new Vec3(0,0.5,0);
    private static final Vec3 BASE_FORWARD = new Vec3(0,0,-1);
    private static final float SEGMENT_HITBOX_WIDTH = 1;
    private static final float SEGMENT_HITBOX_HEIGHT = 1;
    public static final ArrayList<WormBehaviour> behaviours = new ArrayList<>(List.of(
            new SwimmingWormBehaviour(0.3f),
            new SlitheringWormBehaviour()
    ));

    public StitcherEelEntity(EntityType<? extends WormEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, SEGMENT_COUNT, SEGMENT_LENGTH, SEGMENT_ANGLE, BASE_OFFSET, BASE_PIVOT, BASE_FORWARD,SEGMENT_HITBOX_WIDTH,SEGMENT_HITBOX_HEIGHT);
    }
    
    public static AttributeSupplier setAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.ATTACK_SPEED,1)
                .add(Attributes.MOVEMENT_SPEED, 0.8).build();
    }
    
    @Override
    protected void registerGoals(){
        super.registerGoals();
        goalSelector.addGoal(5,new RandomSwimmingGoal(this, 1, 5));
    }

    @Override
    public ArrayList<WormBehaviour> getBehaviours() {
        return behaviours;
    }
}
