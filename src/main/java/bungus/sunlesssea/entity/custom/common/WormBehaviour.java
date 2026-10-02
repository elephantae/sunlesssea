package bungus.sunlesssea.entity.custom.common;

import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WormBehaviour {
    private final int priority;
    
    public WormBehaviour(){
        priority = 0;
    }
    public WormBehaviour(int priority){
        this.priority = priority;
    }
    
    public int getPriority(){
        return priority;
    }
    
    public boolean canBreatheUnderwater(){
        return false;
    }

    public PathNavigation getNavigation(WormEntity worm, Level pLevel){
        return null;
    }

    public MoveControl getMoveControl(WormEntity worm){
        return null;
    }

    public void constructorBehaviour(WormEntity worm){
    }

    public Vec3 getSegmentOffset(WormEntity worm, int seg){
        return null;
    }
}
