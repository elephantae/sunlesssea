package bungus.sunlesssea.entity;

import bungus.sunlesssea.Sunlesssea;
import bungus.sunlesssea.entity.custom.StitcherEelEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Sunlesssea.MOD_ID);
    
    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
    
    public static final RegistryObject<EntityType<StitcherEelEntity>> STITCHER_EEL =
            ENTITY_TYPES.register("stitchereel",
                    () -> EntityType.Builder.of(StitcherEelEntity::new, MobCategory.WATER_CREATURE)
                            .sized(0.8f,0.8f)
                            .build(ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID,"stitchereel").toString()));
}
