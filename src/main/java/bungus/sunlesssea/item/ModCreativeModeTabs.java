package bungus.sunlesssea.item;

import bungus.sunlesssea.Sunlesssea;
import bungus.sunlesssea.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Sunlesssea.MOD_ID);
    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
    
    public static final RegistryObject<CreativeModeTab> SUNLESS_SEA_TAB = CREATIVE_MODE_TABS.register("sunlesssea_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SILT.get()))
                    .title(Component.translatable("creativetab.sunlesssea_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SILT.get());
                        pOutput.accept(ModBlocks.SILT_BLOCK.get());
                        pOutput.accept(ModItems.STITCHER_EEL_SPAWN_EGG.get());
                    })
                    .build());
}
