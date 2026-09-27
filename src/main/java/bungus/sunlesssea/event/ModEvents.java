package bungus.sunlesssea.event;

import bungus.sunlesssea.Sunlesssea;
import bungus.sunlesssea.entity.ModEntities;
import bungus.sunlesssea.entity.custom.StitcherEelEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Sunlesssea.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event){
        event.put(ModEntities.STITCHER_EEL.get(), StitcherEelEntity.setAttributes());
    }
}
