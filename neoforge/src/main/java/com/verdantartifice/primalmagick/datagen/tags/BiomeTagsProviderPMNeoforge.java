package com.verdantartifice.primalmagick.datagen.tags;

import com.verdantartifice.primalmagick.Constants;
import com.verdantartifice.primalmagick.common.tags.BiomeTagsPM;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Data provider for all of the mod's biome tags, both original tags and modifications to vanilla tags.
 * 
 * @author Daedalus4096
 */
public class BiomeTagsProviderPMNeoforge extends BiomeTagsProvider {
    public BiomeTagsProviderPMNeoforge(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Primal Magick Biome Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        // Create custom tags
        this.tag(BiomeTagsPM.HAS_EARTH_SHRINE).addTag(BiomeTags.IS_SAVANNA).add(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS);
        this.tag(BiomeTagsPM.HAS_SEA_SHRINE).addTag(BiomeTags.IS_RIVER).addTag(BiomeTags.IS_BEACH).add(Biomes.SWAMP, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES);
        this.tag(BiomeTagsPM.HAS_SKY_SHRINE).addTag(BiomeTags.IS_HILL).addTag(BiomeTags.IS_MOUNTAIN).add(Biomes.BAMBOO_JUNGLE);
        this.tag(BiomeTagsPM.HAS_SUN_SHRINE).addTag(BiomeTags.IS_BADLANDS).add(Biomes.DESERT);
        this.tag(BiomeTagsPM.HAS_MOON_SHRINE).addTag(BiomeTags.IS_FOREST);
        
        this.tag(BiomeTagsPM.HAS_EARTH_LIBRARY).addTag(BiomeTagsPM.HAS_EARTH_SHRINE);
        this.tag(BiomeTagsPM.HAS_SEA_LIBRARY).addTag(BiomeTagsPM.HAS_SEA_SHRINE);
        this.tag(BiomeTagsPM.HAS_SKY_LIBRARY).addTag(BiomeTagsPM.HAS_SKY_SHRINE);
        this.tag(BiomeTagsPM.HAS_SUN_LIBRARY).addTag(BiomeTagsPM.HAS_SUN_SHRINE);
        this.tag(BiomeTagsPM.HAS_MOON_LIBRARY).addTag(BiomeTagsPM.HAS_MOON_SHRINE);
        this.tag(BiomeTagsPM.HAS_FORBIDDEN_LIBRARY).addTag(BiomeTags.IS_NETHER);
        
        this.tag(BiomeTagsPM.HAS_MARBLE).addTag(BiomeTags.IS_OVERWORLD);
        this.tag(BiomeTagsPM.HAS_ROCK_SALT).addTag(BiomeTags.IS_OVERWORLD);
        this.tag(BiomeTagsPM.HAS_QUARTZ).addTag(BiomeTags.IS_OVERWORLD);
        this.tag(BiomeTagsPM.HAS_WILD_SUNWOOD).addTag(BiomeTags.IS_FOREST);
        this.tag(BiomeTagsPM.HAS_WILD_MOONWOOD).addTag(BiomeTags.IS_FOREST);
        
        this.tag(BiomeTagsPM.HAS_TREEFOLK).addTag(BiomeTags.IS_FOREST);
    }
}
