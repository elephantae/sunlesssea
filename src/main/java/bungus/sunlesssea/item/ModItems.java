package bungus.sunlesssea.item;

import bungus.sunlesssea.Sunlesssea;
import bungus.sunlesssea.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS=
            DeferredRegister.create(ForgeRegistries.ITEMS, Sunlesssea.MOD_ID);
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
    
    public static final RegistryObject<Item> SILT = ITEMS.register("silt",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STITCHER_EEL_SPAWN_EGG = ITEMS.register("stitchereel_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.STITCHER_EEL, 0x60606B, 0x283F4F, new Item.Properties()));
    
}
