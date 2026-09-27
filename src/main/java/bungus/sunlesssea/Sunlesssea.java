package bungus.sunlesssea;

import bungus.sunlesssea.block.ModBlocks;
import bungus.sunlesssea.entity.ModDataSerializers;
import bungus.sunlesssea.entity.ModEntities;
import bungus.sunlesssea.entity.client.StitcherEelRenderer;
import bungus.sunlesssea.item.ModCreativeModeTabs;
import bungus.sunlesssea.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Sunlesssea.MOD_ID)
public class Sunlesssea {
    public static final String MOD_ID = "sunlesssea";
    private static final Logger LOGGER = LogUtils.getLogger();
    public Sunlesssea(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        
        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModDataSerializers.register(modEventBus);
        
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
    
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }
    
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.STITCHER_EEL.get(), StitcherEelRenderer::new);
        }
    }
}
