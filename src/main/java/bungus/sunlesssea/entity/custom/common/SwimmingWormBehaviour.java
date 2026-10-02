package bungus.sunlesssea.entity.custom.common;

import bungus.sunlesssea.entity.ai.control.SwimmingWormControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class SwimmingWormBehaviour extends WormBehaviour{
    private final float turningSpeed;

    public SwimmingWormBehaviour(float turningSpeed) {
        super();
        this.turningSpeed = turningSpeed;
    }
    public SwimmingWormBehaviour(int priority, float turningSpeed) {
        super(priority);
        this.turningSpeed = turningSpeed;
    }


    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public PathNavigation getNavigation(WormEntity worm, Level pLevel) {
        return new WaterBoundPathNavigation(worm, pLevel);
    }
    
    @Override
    public MoveControl getMoveControl(WormEntity worm){
        return new SwimmingWormControl(worm, turningSpeed);
    }
    
    @Override
    public void constructorBehaviour(WormEntity worm){
        worm.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }
}
