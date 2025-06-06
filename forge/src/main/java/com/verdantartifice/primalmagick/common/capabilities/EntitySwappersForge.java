package com.verdantartifice.primalmagick.common.capabilities;

import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class EntitySwappersForge extends EntitySwappers implements IEntitySwappersForge {
    /**
     * Capability provider for the entity swapper capability.  Used to attach capability data to the owner.
     *
     * @author Daedalus4096
     * @see com.verdantartifice.primalmagick.common.events.CapabilityEvents
     */
    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        public static final ResourceLocation NAME = ResourceUtils.loc("capability_entity_swappers");

        private final IEntitySwappers instance = new EntitySwappers();
        private final LazyOptional<IEntitySwappers> holder = LazyOptional.of(() -> instance);  // Cache a lazy optional of the capability instance

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == CapabilitiesForge.ENTITY_SWAPPERS) {
                return holder.cast();
            } else {
                return LazyOptional.empty();
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registries) {
            return instance.serializeNBT(registries);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
            instance.deserializeNBT(registries, nbt);
        }
    }
}
