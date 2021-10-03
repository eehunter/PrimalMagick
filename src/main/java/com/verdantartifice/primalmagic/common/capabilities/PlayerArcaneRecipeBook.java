package com.verdantartifice.primalmagic.common.capabilities;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.crafting.recipebook.ArcaneRecipeBook;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Default implementation for the player arcane recipe book capability.
 * 
 * @author Daedalus4096
 */
public class PlayerArcaneRecipeBook implements IPlayerArcaneRecipeBook {
    private final ArcaneRecipeBook book = new ArcaneRecipeBook();

    @Override
    public ArcaneRecipeBook get() {
        return this.book;
    }

    @Override
    public void sync(ServerPlayer player) {
        // TODO Auto-generated method stub

    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag retVal = new CompoundTag();
        retVal.put("Book", this.book.toNbt());
        return retVal;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, RecipeManager recipeManager) {
        this.book.fromNbt(nbt.getCompound("Book"), recipeManager);
    }

    
    /**
     * Capability provider for the player arcane recipe book capability.  Used to attach capability data to the owner.
     * 
     * @author Daedalus4096
     * @see {@link com.verdantartifice.primalmagic.common.events.CapabilityEvents}
     */
    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        public static final ResourceLocation NAME = new ResourceLocation(PrimalMagic.MODID, "capability_arcane_recipe_book");
        
        private final IPlayerArcaneRecipeBook instance = new PlayerArcaneRecipeBook();
        private final LazyOptional<IPlayerArcaneRecipeBook> holder = LazyOptional.of(() -> instance);   // Cache a lazy optional of the capability instance
        private final RecipeManager recipeManager;
        
        public Provider(RecipeManager recipeManager) {
            this.recipeManager = recipeManager;
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == PrimalMagicCapabilities.ARCANE_RECIPE_BOOK) {
                return holder.cast();
            } else {
                return LazyOptional.empty();
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.instance.deserializeNBT(nbt, this.recipeManager);
        }
    }
}
