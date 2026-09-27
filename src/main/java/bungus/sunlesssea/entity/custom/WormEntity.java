package bungus.sunlesssea.entity.custom;

import bungus.sunlesssea.entity.ModDataSerializers;
import dev.customhitboxlib.api.MultipartHelper;
import dev.customhitboxlib.api.PartPositioner;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;
import java.util.List;

public class WormEntity extends PathfinderMob {
    private final int segmentCount;
    private final float segmentLength;
    private final float segmentAngle;
    private final Vec3 baseOffset;
    private final Vec3 baseForward;
    private final float segmentHitboxWidth;
    private final float segmentHitboxHeight;
    private List<Vec3> jointPos = new ArrayList<>();
    private List<Vec3> oldJointPos = new ArrayList<>();
    private AABB cullingBox;
    private static final EntityDataAccessor<List<Vec3>> JOINTS = SynchedEntityData.defineId(WormEntity.class, ModDataSerializers.VEC3_LIST);

    public WormEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel, int segmentCount, float segmentLength, float segmentAngle,
                      Vec3 baseOffset, Vec3 baseForward, float segmentHitboxWidth, float segmentHitboxHeight) {
        super(pEntityType, pLevel);
        this.segmentCount = segmentCount;
        this.segmentLength = segmentLength;
        this.segmentAngle = segmentAngle;
        this.baseOffset = baseOffset;
        this.baseForward = baseForward;
        this.segmentHitboxWidth = segmentHitboxWidth;
        this.segmentHitboxHeight = segmentHitboxHeight;
        initSegments();
        entityData.set(JOINTS, new ArrayList<>(jointPos));
        cullingBox = getBoundingBox();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(JOINTS, new ArrayList<>(segmentCount));
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
            }
        }
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return cullingBox;
    }

    @Override
    public void tick() {
        oldJointPos = new ArrayList<>(jointPos);
        if (!level().isClientSide()) {
            jointPos.set(0, position().add(baseOffset.yRot(-(float) Math.toRadians(getYRot()))));
            for (int i = 1; i <= segmentCount; i++) {
                Vec3 fromDir;
                if (i == 1) {
                    fromDir = getLookAngle();
                } else {
                    fromDir = jointPos.get(i - 2).subtract(jointPos.get(i - 1)).normalize();
                }

                Vec3 offset = jointPos.get(i - 1).subtract(jointPos.get(i));
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
        } else {
            jointPos = entityData.get(JOINTS);
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
        Quaterniond cumRot = new Quaterniond().rotationTo(vector3d(baseForward), vector3d(getLookAngle()));
        for (int i = 1; i <= segmentCount; i++) {
            if (jointPos.size() > i && oldJointPos.size() > i) {
                CoreGeoBone bone = processor.getBone("segment" + i);
                float partialTick = Minecraft.getInstance().getPartialTick();
                Vec3 toDir = (oldJointPos.get(i - 1).lerp(jointPos.get(i - 1), partialTick))
                        .subtract(oldJointPos.get(i).lerp(jointPos.get(i), partialTick)).normalize();
                Vector3d eul = new Quaterniond().rotationTo(vector3d(baseForward), cumRot.transformInverse(vector3d(toDir))).getEulerAnglesZYX(new Vector3d());
                storeEuler(eul, bone);
                Quaterniond localRot = new Quaterniond().rotationZYX(eul.z, eul.y, eul.x);
                cumRot = cumRot.mul(localRot);
            }
        }
    }

    private static Vector3d vector3d(Vec3 v) {
        return new Vector3d(v.x, v.y, v.z);
    }

    private static Vec3 vec3(Vector3d v) {
        return new Vec3(v.x, v.y, v.z);
    }

    private static void storeEuler(Vector3d v, CoreGeoBone bone) {
        bone.setRotX((float) v.x);
        bone.setRotY((float) v.y);
        bone.setRotZ((float) v.z);
    }
}
