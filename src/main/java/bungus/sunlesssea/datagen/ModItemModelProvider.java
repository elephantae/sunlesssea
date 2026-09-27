package bungus.sunlesssea.datagen;

import bungus.sunlesssea.Sunlesssea;
import bungus.sunlesssea.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Sunlesssea.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.SILT);
        withExistingParent(ModItems.STITCHER_EEL_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }
    
    private ItemModelBuilder simpleItem(RegistryObject<Item> item){
        assert item.getId() != null;
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID, "item/" + item.getId().getPath()));
    }
}
