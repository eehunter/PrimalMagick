package com.verdantartifice.primalmagick.common.capabilities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Access point for all capabilities defined by the mod.  Capabilities are injected with the output 
 * of their registered factories post-registration.
 * 
 * @author Daedalus4096
 */
public class PrimalMagickCapabilities {
    public static final Capability<IPlayerArcaneRecipeBook> ARCANE_RECIPE_BOOK = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IWorldEntitySwappers> ENTITY_SWAPPERS = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IManaStorage<?>> MANA_STORAGE = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ITileResearchCache> RESEARCH_CACHE = CapabilityManager.get(new CapabilityToken<>(){});
    
    @Nonnull
    public static LazyOptional<IPlayerArcaneRecipeBook> getArcaneRecipeBook(@Nullable Player player) {
        return player == null ? LazyOptional.empty() : player.getCapability(ARCANE_RECIPE_BOOK);
    }
    
    @Nullable
    public static IWorldEntitySwappers getEntitySwappers(@Nonnull Level world) {
        return world.getCapability(ENTITY_SWAPPERS, null).orElse(null);
    }
    
    @Nullable
    public static IManaStorage<?> getManaStorage(@Nonnull BlockEntity tile) {
        return tile.getCapability(MANA_STORAGE, null).orElse(null);
    }
    
    @Nonnull
    public static LazyOptional<ITileResearchCache> getResearchCache(@Nonnull BlockEntity tile) {
        return tile.getCapability(RESEARCH_CACHE);
    }
}
