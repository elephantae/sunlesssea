package bungus.sunlesssea.entity.custom.common;

import bungus.sunlesssea.entity.ModDataSerializers;
import bungus.sunlesssea.event.DebugRenderHandler;
import dev.customhitboxlib.api.MultipartHelper;
import dev.customhitboxlib.api.PartPositioner;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.GeoModel;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class WormEntity extends PathfinderMob implements GeoEntity, Behaviourable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private Vec3 facingDir;
    private Vec3 oldFacingDir;
    private final int segmentCount;
    private final float segmentLength;
    private final float segmentAngle;
    private final Vec3 baseOffset;
    private final Vec3 basePivot;
    private final Vec3 segmentOffset;
    private final Vec3 baseForward;
    private final float segmentHitboxWidth;
    private final float segmentHitboxHeight;
    private List<Vec3> jointPos = new ArrayList<>();
    private List<Vec3> oldJointPos = new ArrayList<>();
    private AABB cullingBox;
    private static final EntityDataAccessor<List<Vec3>> JOINTS = SynchedEntityData.defineId(WormEntity.class, ModDataSerializers.VEC3_LIST);
    private static final EntityDataAccessor<Vector3f> FACING = SynchedEntityData.defineId(WormEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<List<Vec3>> OLD_JOINTS = SynchedEntityData.defineId(WormEntity.class, ModDataSerializers.VEC3_LIST);

    public WormEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel, int segmentCount, float segmentLength, float segmentAngle,
                      Vec3 baseOffset, Vec3 basePivot, Vec3 baseForward, float segmentHitboxWidth, float segmentHitboxHeight) {
        super(pEntityType, pLevel);
        this.segmentCount = segmentCount;
        this.segmentLength = segmentLength;
        this.segmentAngle = segmentAngle;
        this.baseOffset = baseOffset;
        this.basePivot = basePivot;
        this.baseForward = baseForward;
        this.segmentHitboxWidth = segmentHitboxWidth;
        this.segmentHitboxHeight = segmentHitboxHeight;
        segmentOffset = new Vec3(0, 0, segmentLength);
        initSegments();
        entityData.set(JOINTS, new ArrayList<>(jointPos));
        facingDir = new Vec3(0,0,1);
        oldFacingDir = new Vec3(0,0,1);
        cullingBox = getBoundingBox();
        setYRot(0);
        setXRot(0);
        
        for(WormBehaviour b:getBehaviours()){
            b.constructorBehaviour(this);
        }
        Optional<WormBehaviour> behaviour = getBehaviours().stream()
                .filter(b -> b.getMoveControl(this) != null)
                .max(Comparator.comparingInt(WormBehaviour::getPriority));
        behaviour.ifPresent(b -> moveControl = b.getMoveControl(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(JOINTS, new ArrayList<>(segmentCount));
        entityData.define(FACING, new Vector3f(0,0,1));
        entityData.define(OLD_JOINTS, new ArrayList<>(segmentCount));
    }
    
    private void initSegments() {
        jointPos.clear();
        for (int i = 0; i <= segmentCount; i++) {
            jointPos.add(getPosition(0).add(new Vec3(0, 0, -i * segmentLength)));
        }
        for (int i = 0; i < segmentCount; i++) {
            MultipartHelper.addPart(this, "segment" + i, segmentHitboxWidth, segmentHitboxHeight, getPositioner(i));
        }
    }

    private PartPositioner getPositioner(int segment) {
        return (entity, partialTick) -> {
            WormEntity wormEntity = (WormEntity) entity;
            if (jointPos.size() > segment && oldJointPos.size() > segment) {
                return wormEntity.oldJointPos.get(segment).lerp(wormEntity.jointPos.get(segment), partialTick)
                        .lerp(wormEntity.oldJointPos.get(segment + 1).lerp(wormEntity.jointPos.get(segment + 1), partialTick), 0.5)
                        .subtract(new Vec3(0, segmentHitboxHeight / 2, 0));
            } else {
                return wormEntity.position();
            }
        };
    }
    
    public void setFacingDir(Vec3 facingDir){
        this.facingDir = facingDir;
    }
    
    public Vec3 getFacingDir(){return facingDir;}

    public boolean isNoGravity(){
        return super.isNoGravity()||isUnderWater();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag listTag = new ListTag();
        for (Vec3 v : jointPos) {
            listTag.add(DoubleTag.valueOf(v.x));
            listTag.add(DoubleTag.valueOf(v.y));
            listTag.add(DoubleTag.valueOf(v.z));
        }
        tag.put("jointPositions", listTag);
        ListTag listTag2 = new ListTag();
        listTag2.add(DoubleTag.valueOf(facingDir.x));
        listTag2.add(DoubleTag.valueOf(facingDir.y));
        listTag2.add(DoubleTag.valueOf(facingDir.z));
        tag.put("wormFacing", listTag2);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("jointPositions", 9)) {
            ListTag listTag = tag.getList("jointPositions", 6);
            if (!listTag.isEmpty()) {
                ArrayList<Vec3> out = new ArrayList<>();
                for (int i = 0; i < listTag.size() / 3; i++) {
                    out.add(new Vec3(listTag.getDouble(3 * i), listTag.getDouble(3 * i + 1), listTag.getDouble(3 * i + 2)));
                }
                jointPos = out;
                oldJointPos = out;
            }
        }
        if (tag.contains("wormFacing", 9)) {
            ListTag listTag = tag.getList("wormFacing", 6);
            if (!listTag.isEmpty()) {
                facingDir = new Vec3(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
                oldFacingDir = facingDir;
            }
        }
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return cullingBox;
    }
    
    protected Vec3 getSegmentDirections(int seg){
        Vec3 out = jointPos.get(seg - 1).subtract(jointPos.get(seg));
        for(WormBehaviour b:getBehaviours()){
            Vec3 offset = b.getSegmentOffset(this, seg);
            if(offset != null){
                out = out.add(offset);
            }
        }
        return out;
    }
    @Override
    public void tick() {
        if (!level().isClientSide()) {
            ArrayList<Vec3> tempOldPos = new ArrayList<>(jointPos);
            jointPos.set(0, position().add(basePivot).subtract(facingDir.scale(segmentLength/2)));
            for (int i = 1; i <= segmentCount; i++) {
                Vec3 fromDir;
                if (i == 1) {
                    fromDir = facingDir;
                } else {
                    fromDir = jointPos.get(i - 2).subtract(jointPos.get(i - 1)).normalize();
                }
                Vec3 offset = getSegmentDirections(i);
                Vec3 newPos = jointPos.get(i - 1).subtract(offset.normalize().scale(segmentLength));
                Vec3 testPos = offset.normalize();
                double angle = vector3d(fromDir).angle(vector3d(testPos));
                if (angle > segmentAngle) {
                    Quaterniond q = new Quaterniond().rotationTo(vector3d(fromDir), vector3d(testPos));
                    Quaterniond interp = new Quaterniond().slerp(q, segmentAngle / angle);
                    testPos = vec3(interp.transform(vector3d(fromDir)));
                    newPos = jointPos.get(i - 1).subtract(testPos.scale(segmentLength));
                }

                jointPos.set(i, newPos);
            }
            entityData.set(JOINTS, new ArrayList<>(jointPos));
            entityData.set(FACING, new Vector3f((float) facingDir.x, (float) facingDir.y, (float) facingDir.z));
            oldJointPos = new ArrayList<>(tempOldPos);
            entityData.set(OLD_JOINTS, new ArrayList<>(oldJointPos));
            
        } else {
            jointPos = entityData.get(JOINTS);
            oldJointPos = entityData.get(OLD_JOINTS);
            Vector3f temp = entityData.get(FACING);
            oldFacingDir = facingDir;
            facingDir = new Vec3(temp.x, temp.y, temp.z);
            
            
            
        }
        if (jointPos.isEmpty()) {
            cullingBox = getBoundingBox();
        } else {
            double minx = Integer.MAX_VALUE;
            double miny = Integer.MAX_VALUE;
            double minz = Integer.MAX_VALUE;
            double maxx = Integer.MIN_VALUE;
            double maxy = Integer.MIN_VALUE;
            double maxz = Integer.MIN_VALUE;
            for (Vec3 joint : jointPos) {
                minx = Math.min(minx, joint.x);
                miny = Math.min(miny, joint.y);
                minz = Math.min(minz, joint.z);
                maxx = Math.max(maxx, joint.x);
                maxy = Math.max(maxy, joint.y);
                maxz = Math.max(maxz, joint.z);
            }
            cullingBox = new AABB(minx, miny, minz, maxx, maxy, maxz).inflate(Math.max(segmentHitboxHeight, segmentHitboxWidth));
        }
        super.tick();
    }
    
    public <T extends WormEntity & GeoAnimatable> void setRotationAnimation(GeoModel<T> model) {
        AnimationProcessor<T> processor = model.getAnimationProcessor();
        GeoBone base = (GeoBone) processor.getBone("base");
        CoreGeoBone head = processor.getBone("head");

        float partialTick = Minecraft.getInstance().getPartialTick();
        Vec3 lerpZero;
        if(!oldJointPos.isEmpty()) {
            lerpZero = oldJointPos.get(0).lerp(jointPos.get(0), partialTick);
        }else{
            lerpZero = jointPos.get(0);
        }
        Vec3 lerpFacing = vec3(new Quaterniond().slerp(new Quaterniond().rotationTo(vector3d(oldFacingDir),vector3d(facingDir)), partialTick).transform(vector3d(oldFacingDir)));
        
        float yHeadAngle = (float)Math.atan2(lerpFacing.x, lerpFacing.z);
        float xHeadAngle = (float)Math.atan2(lerpFacing.y, Math.sqrt(lerpFacing.x* lerpFacing.x+ lerpFacing.z* lerpFacing.z));
        
        head.updateRotation(xHeadAngle,yHeadAngle,0);
        
        for (int i = 1; i <= segmentCount; i++) {
            if (jointPos.size() > i && oldJointPos.size() > i) {
                Vec3 lerpCurrent = oldJointPos.get(i).lerp(jointPos.get(i),partialTick);
                Vec3 lerpPrevious = oldJointPos.get(i-1).lerp(jointPos.get(i-1),partialTick);
                
                CoreGeoBone bone = processor.getBone("segment" + i);
                Vec3 toDir = lerpCurrent.subtract(lerpPrevious).normalize();
                float yAngle = (float)Math.atan2(-toDir.x,-toDir.z);
                float xAngle = (float)Math.atan2(-toDir.y, Math.sqrt(toDir.x*toDir.x+toDir.z*toDir.z));
                bone.updateRotation(xAngle,yAngle,0);
                Vec3 posOffset = lerpPrevious.add(new Vec3(0,0,segmentLength*(i))).subtract(vec3(base.getWorldPosition())).subtract(lerpZero.subtract(vec3(base.getWorldPosition()))).subtract(lerpFacing.scale(segmentLength/2));
                bone.updatePosition((float)posOffset.x*16f, (float)posOffset.y*16f, -(float)posOffset.z*16f+8f);
            }
        }
    }

    //move these to helper lib
    public static Vector3d vector3d(Vec3 v) {
        return new Vector3d(v.x, v.y, v.z);
    }
    public static Vec3 vec3(Vector3d v) {
        return new Vec3(v.x, v.y, v.z);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        return PlayState.CONTINUE;
    }

    public void setYRot(float yRot){
        yBodyRot=0;
        yHeadRot=0;
    }
    public void setXRot(float xRot){}
    
    //behaviours
    
    @Override
    public boolean canBreatheUnderwater(){
        return getBehaviours().stream().anyMatch(WormBehaviour::canBreatheUnderwater);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level pLevel) {
        Optional<WormBehaviour> behaviour = getBehaviours().stream()
                .filter(b -> b.getNavigation(this, pLevel) != null)
                .max(Comparator.comparingInt(WormBehaviour::getPriority));
        if(behaviour.isPresent()){
            return behaviour.get().getNavigation(this, pLevel);
        }
        return super.createNavigation(pLevel);
    }
}
