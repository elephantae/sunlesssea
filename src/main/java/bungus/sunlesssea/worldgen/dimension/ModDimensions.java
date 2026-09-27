package bungus.sunlesssea.worldgen.dimension;

import bungus.sunlesssea.Sunlesssea;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.OptionalLong;

public class ModDimensions {
    public static final ResourceKey<LevelStem> SUNLESSDIM_KEY= ResourceKey.create(Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID,"sunlessdim"));
    public static final ResourceKey<Level> SUNLESSDIM_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID,"sunlessdim"));
    public static final ResourceKey<DimensionType> SUNLESSDIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(Sunlesssea.MOD_ID,"sunlessdim_type"));
    public static void bootstrapType(BootstapContext<DimensionType> context){
        context.register(SUNLESSDIM_TYPE,new DimensionType(
                OptionalLong.of(12000),
                false,
                true,
                false,
                false,
                1.0,
                true,
                false,
                -384,
                448,
                448,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                0,
                new DimensionType.MonsterSettings(false,false, ConstantInt.of(0),0)
                
        ));
    }
    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        NoiseBasedChunkGenerator chunkGen = new NoiseBasedChunkGenerator(
                new FixedBiomeSource(biomeRegistry.getOrThrow(Biomes.OCEAN)),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED)
        );
        
        LevelStem stem = new LevelStem(dimTypes.getOrThrow(ModDimensions.SUNLESSDIM_TYPE), chunkGen);
        context.register(SUNLESSDIM_KEY, stem);
    }
    
}
