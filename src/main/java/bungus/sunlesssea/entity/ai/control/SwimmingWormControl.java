package bungus.sunlesssea.entity.ai.control;

import bungus.sunlesssea.entity.custom.common.WormEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

public class SwimmingWormControl extends MoveControl {
    private double turningSpeed;
    public SwimmingWormControl(Mob pMob, double turningSpeed) {
        super(pMob);
        this.turningSpeed = turningSpeed;
    }
    
    public void tick(){
        if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            WormEntity worm = (WormEntity) mob;
            double distX = this.wantedX - this.mob.getX();
            double distY = this.wantedY - this.mob.getY();
            double distZ = this.wantedZ - this.mob.getZ();
            Vec3 targetFacing = new Vec3(distX, distY, distZ).normalize();
            Quaterniond rot = new Quaterniond().rotationTo(WormEntity.vector3d(worm.getFacingDir()), WormEntity.vector3d(targetFacing));
            double angle = rot.angle();
            Quaterniond slerpRot = new Quaterniond().slerp(rot, Math.min(turningSpeed/angle, 1));
            Vec3 newFacing = WormEntity.vec3(slerpRot.transform(WormEntity.vector3d(worm.getFacingDir())));
            worm.setFacingDir(newFacing);
            if(angle<Math.toRadians(60)){
                worm.addDeltaMovement(worm.getFacingDir().scale(0.1));
            }
        }
    }
}
