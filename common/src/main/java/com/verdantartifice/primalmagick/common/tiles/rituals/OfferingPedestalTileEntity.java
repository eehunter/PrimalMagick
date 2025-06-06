package com.verdantartifice.primalmagick.common.tiles.rituals;

import com.google.common.collect.ImmutableSet;
import com.verdantartifice.primalmagick.common.capabilities.IItemHandlerPM;
import com.verdantartifice.primalmagick.common.tiles.BlockEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.base.AbstractTileSidedInventoryPM;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

/**
 * Definition of an offering pedestal tile entity.  Holds the pedestal's inventory.
 * 
 * @author Daedalus4096
 * @see com.verdantartifice.primalmagick.common.blocks.rituals.OfferingPedestalBlock
 */
public abstract class OfferingPedestalTileEntity extends AbstractTileSidedInventoryPM {
    public static final int INPUT_INV_INDEX = 0;
    
    protected BlockPos altarPos = null;

    public OfferingPedestalTileEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypesPM.OFFERING_PEDESTAL.get(), pos, state);
    }
    
    @Override
    protected Set<Integer> getSyncedSlotIndices(int inventoryIndex) {
        // Sync the pedestal's item stack for client rendering use
        return inventoryIndex == INPUT_INV_INDEX ? ImmutableSet.of(Integer.valueOf(0)) : ImmutableSet.of();
    }
    
    @Nullable
    public BlockPos getAltarPos() {
        return this.altarPos;
    }
    
    public void setAltarPos(@Nullable BlockPos pos) {
        this.altarPos = pos;
        this.setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        this.altarPos = compound.contains("AltarPos", Tag.TAG_LONG) ? BlockPos.of(compound.getLong("AltarPos")) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        if (this.altarPos != null) {
            compound.putLong("AltarPos", this.altarPos.asLong());
        }
    }

    public ItemStack getItem() {
        return this.getItem(INPUT_INV_INDEX, 0);
    }
    
    public ItemStack getSyncedStack() {
        return this.syncedInventories.get(INPUT_INV_INDEX).get(0);
    }
    
    public void setItem(ItemStack stack) {
        this.setItem(INPUT_INV_INDEX, 0, stack);
    }
    
    public ItemStack removeItem(int count) {
        return this.itemHandlers.get(INPUT_INV_INDEX).extractItem(0, count, false);
    }

    @Override
    protected int getInventoryCount() {
        return 1;
    }

    @Override
    protected int getInventorySize(int inventoryIndex) {
        return inventoryIndex == INPUT_INV_INDEX ? 1 : 0;
    }

    @Override
    public Optional<Integer> getInventoryIndexForFace(@NotNull Direction face) {
        return Optional.of(INPUT_INV_INDEX);
    }

    @Override
    protected NonNullList<IItemHandlerPM> createItemHandlers() {
        NonNullList<IItemHandlerPM> retVal = NonNullList.withSize(this.getInventoryCount(), Services.ITEM_HANDLERS.create(this));
        
        // Create input handler
        retVal.set(INPUT_INV_INDEX, Services.ITEM_HANDLERS.builder(this.inventories.get(INPUT_INV_INDEX), this)
                .slotLimitFunction(slot -> 1)
                .build());

        return retVal;
    }

    @Override
    public Optional<IItemHandlerPM> getTargetRandomizedInventory() {
        return Optional.ofNullable(this.itemHandlers.get(INPUT_INV_INDEX));
    }
}
