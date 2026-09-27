package bungus.sunlesssea.datagen.loot;

import bungus.sunlesssea.block.ModBlocks;
import bungus.sunlesssea.item.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.SILT_BLOCK.get());
        
        this.add(ModBlocks.SILT_BLOCK.get(),
                block -> silkOrTable(ModBlocks.SILT_BLOCK.get(), ModItems.SILT.get(),1,4));
        
    }
    
    @Override
    protected Iterable<Block> getKnownBlocks(){
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
    
    private LootTable.Builder silkOrFortune(Block pBlock, Item item, float low, float high){
        return createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(
                pBlock, 
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(
                                UniformGenerator.between(low, high)))
                        .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    private LootTable.Builder silkOrTable(Block pBlock, Item item, float low, float high){
        return createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(
                pBlock,
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(
                                UniformGenerator.between(low, high)))));
    }
}
