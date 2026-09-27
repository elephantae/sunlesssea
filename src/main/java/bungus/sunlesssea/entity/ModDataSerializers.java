package bungus.sunlesssea.entity;

import bungus.sunlesssea.Sunlesssea;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ModDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = 
            DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Sunlesssea.MOD_ID);
    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
        SERIALIZERS.register("vec3_list", () -> ModDataSerializers.VEC3_LIST);
    }
    
    public static final EntityDataSerializer<List<Vec3>> VEC3_LIST = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf pBuffer, List<Vec3> pValue) {
            pBuffer.writeVarInt(pValue.size());
            for(Vec3 v : pValue){
                pBuffer.writeDouble(v.x);
                pBuffer.writeDouble(v.y);
                pBuffer.writeDouble(v.z);
            }
        }

        @Override
        public List<Vec3> read(FriendlyByteBuf pBuffer) {
            int size = pBuffer.readVarInt();
            List<Vec3> list = new ArrayList<>(size);
            for(int i = 0; i < size; i++){
                list.add(new Vec3(pBuffer.readDouble(),pBuffer.readDouble(),pBuffer.readDouble()));
            }
            return list;
        }

        @Override
        public List<Vec3> copy(List<Vec3> pValue) {
            return new ArrayList<>(pValue);
        }
    };
}
