package com.verdantartifice.primalmagick.common.worldgen.structures;

import java.util.Map;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.tags.BiomeTagsPM;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

/**
 * Registry of mod structures, backed by datapack JSON.
 * 
 * @author Daedalus4096
 */
public class StructuresPM {
    public static final ResourceKey<Structure> EARTH_SHRINE = registryKey("earth_shrine");
    
    private static ResourceKey<Structure> registryKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE, PrimalMagick.resource(name));
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> pBiomes, Map<MobCategory, StructureSpawnOverride> pSpawnOverrides, GenerationStep.Decoration pStep, TerrainAdjustment pTerrainAdaptation) {
        return new Structure.StructureSettings(pBiomes, pSpawnOverrides, pStep, pTerrainAdaptation);
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> pBiomes) {
        return structure(pBiomes, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN);
    }
    
    private static void register(BootstapContext<Structure> context, ResourceKey<Structure> structureKey, HolderSet<Biome> biomes, ShrineStructure.Type shrineType) {
        context.register(structureKey, new ShrineStructure(structure(biomes), shrineType));
    }
    
    public static void bootstrap(BootstapContext<Structure> context) {
        HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
        register(context, EARTH_SHRINE, biomeGetter.getOrThrow(BiomeTagsPM.HAS_EARTH_SHRINE), ShrineStructure.Type.EARTH);
    }
}
