package bungus.sunlesssea.entity.custom.common;

import net.minecraft.world.phys.Vec3;

public class SlitheringWormBehaviour extends WormBehaviour{
    public SlitheringWormBehaviour() {
        super();
    }

    public SlitheringWormBehaviour(int priority) {
        super(priority);
    }

    @Override
    public Vec3 getSegmentOffset(WormEntity worm, int seg){
        double amplitude = 4*worm.getDeltaMovement().length();
        double wavelength = 8.0;
        double speed = 0.3;

        double phase = (seg / wavelength) * Math.PI * 2.0
                - worm.tickCount * speed;

        Vec3 forward = worm.getFacingDir();

// Horizontal perpendicular
        Vec3 lateral = new Vec3(
                -forward.z,
                0,
                forward.x
        ).normalize();

        Vec3 offset = lateral.scale(
                amplitude * Math.sin(phase)
        );
        return offset;
    }
}
