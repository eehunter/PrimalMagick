package com.verdantartifice.primalmagick.common.tiles.mana;

import com.verdantartifice.primalmagick.common.blocks.mana.ManaBatteryBlock;
import com.verdantartifice.primalmagick.common.capabilities.IManaStorage;
import com.verdantartifice.primalmagick.common.capabilities.ManaStorage;
import com.verdantartifice.primalmagick.common.capabilities.PrimalMagickCapabilities;
import com.verdantartifice.primalmagick.common.items.essence.EssenceItem;
import com.verdantartifice.primalmagick.common.menus.ManaBatteryMenu;
import com.verdantartifice.primalmagick.common.sources.IManaContainer;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.base.TileInventoryPM;
import com.verdantartifice.primalmagick.common.wands.IWand;
import com.verdantartifice.primalmagick.common.wands.WandCap;
import com.verdantartifice.primalmagick.common.wands.WandGem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class ManaBatteryTileEntity extends TileInventoryPM implements MenuProvider, IManaContainer {
    protected static final int INPUT_SLOT_INDEX = 0;
    protected static final int CHARGE_SLOT_INDEX = 1;
    protected static final int[] SLOTS_FOR_UP = new int[] { INPUT_SLOT_INDEX };
    protected static final int[] SLOTS_FOR_DOWN = new int[0];
    protected static final int[] SLOTS_FOR_SIDES = new int[] { CHARGE_SLOT_INDEX };
    
    protected int chargeTime;
    protected int chargeTimeTotal;
    protected ManaStorage manaStorage;
    protected LazyOptional<IManaStorage> manaStorageOpt = LazyOptional.of(() -> this.manaStorage);

    // Define a container-trackable representation of this tile's relevant data
    protected final ContainerData chargerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> ManaBatteryTileEntity.this.chargeTime;
                case 1 -> ManaBatteryTileEntity.this.chargeTimeTotal;
                case 2 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.EARTH);
                case 3 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.EARTH);
                case 4 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.SEA);
                case 5 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.SEA);
                case 6 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.SKY);
                case 7 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.SKY);
                case 8 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.SUN);
                case 9 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.SUN);
                case 10 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.MOON);
                case 11 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.MOON);
                case 12 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.BLOOD);
                case 13 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.BLOOD);
                case 14 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.INFERNAL);
                case 15 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.INFERNAL);
                case 16 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.VOID);
                case 17 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.VOID);
                case 18 -> ManaBatteryTileEntity.this.manaStorage.getManaStored(Source.HALLOWED);
                case 19 -> ManaBatteryTileEntity.this.manaStorage.getMaxManaStored(Source.HALLOWED);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Don't set mana storage values
            switch (index) {
            case 0:
                ManaBatteryTileEntity.this.chargeTime = value;
                break;
            case 1:
                ManaBatteryTileEntity.this.chargeTimeTotal = value;
                break;
            }
        }

        @Override
        public int getCount() {
            return 20;
        }
    };
    
    public ManaBatteryTileEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypesPM.MANA_BATTERY.get(), pos, state, 2);
        this.manaStorage = new ManaStorage(this.getBatteryCapacity(), this.getBatteryTransferCap(), Source.SORTED_SOURCES.toArray(new Source[0]));
    }
    
    protected int getBatteryCapacity() {
        // Return the capacity of the battery in centimana
        if (this.getBlockState().getBlock() instanceof ManaBatteryBlock batteryBlock) {
            return switch (batteryBlock.getDeviceTier()) {
                case FORBIDDEN -> 400 * WandGem.WIZARD.getCapacity();
                case HEAVENLY -> 400 * WandGem.ARCHMAGE.getCapacity();
                default -> 0;
            };
        } else {
            return 0;
        }
    }
    
    protected int getBatteryTransferCap() {
        // Return the max amount of centimana that can be transfered by the battery per tick
        if (this.getBlockState().getBlock() instanceof ManaBatteryBlock batteryBlock) {
            return switch (batteryBlock.getDeviceTier()) {
                case FORBIDDEN -> 100 * WandCap.HEXIUM.getSiphonAmount();
                case HEAVENLY -> 100 * WandCap.HALLOWSTEEL.getSiphonAmount();
                default -> 0;
            };
        } else {
            return 0;
        }
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, ManaBatteryTileEntity entity) {
        boolean shouldMarkDirty = false;
        
        if (!level.isClientSide) {
            ItemStack inputStack = entity.items.get(INPUT_SLOT_INDEX);
            ItemStack chargeStack = entity.items.get(CHARGE_SLOT_INDEX);
            
            // TODO Siphon from nearby fonts
            
            if (!inputStack.isEmpty()) {
                // Break down input if it's essence
                if (entity.canBreakDownEssence(inputStack)) {
                    entity.chargeTime++;
                    if (entity.chargeTime >= entity.chargeTimeTotal) {
                        entity.chargeTime = 0;
                        entity.chargeTimeTotal = entity.getChargeTimeTotal();
                        entity.breakDownEssence(inputStack);
                        shouldMarkDirty = true;
                    }
                } else {
                    entity.chargeTime = 0;
                }
                
                // TODO Siphon from input if it's a wand
            }
            
            if (!chargeStack.isEmpty()) {
                // TODO Output mana to wand
            }
        }
        
        if (shouldMarkDirty) {
            entity.setChanged();
            entity.syncTile(true);
        }
    }
    
    protected int getChargeTimeTotal() {
        return 100;
    }
    
    protected boolean canBreakDownEssence(ItemStack inputStack) {
        if (!inputStack.isEmpty() && inputStack.getItem() instanceof EssenceItem essenceItem) {
            return this.manaStorage.getManaStored(essenceItem.getSource()) < this.manaStorage.getMaxManaStored(essenceItem.getSource());
        } else {
            return false;
        }
    }
    
    protected void breakDownEssence(ItemStack inputStack) {
        if (this.canBreakDownEssence(inputStack) && inputStack.getItem() instanceof EssenceItem essenceItem) {
            this.manaStorage.setMana(essenceItem.getSource(), this.manaStorage.getManaStored(essenceItem.getSource()) + essenceItem.getEssenceType().getManaEquivalent() * 100);
            inputStack.shrink(1);
        }
    }
    
    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.chargeTime = compound.getInt("ChargeTime");
        this.chargeTimeTotal = compound.getInt("ChargeTimeTotal");
        this.manaStorage.deserializeNBT(compound.getCompound("ManaStorage"));
    }
    
    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("ChargeTime", this.chargeTime);
        compound.putInt("ChargeTimeTotal", this.chargeTimeTotal);
        compound.put("ManaStorage", this.manaStorage.serializeNBT());
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ManaBatteryMenu(pContainerId, pPlayerInventory, this, this.chargerData);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == PrimalMagickCapabilities.MANA_STORAGE) {
            return this.manaStorageOpt.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.manaStorageOpt.invalidate();
    }

    @Override
    public int getMana(Source source) {
        return this.manaStorage.getManaStored(source);
    }

    @Override
    public SourceList getAllMana() {
        SourceList mana = new SourceList();
        for (Source source : Source.SORTED_SOURCES) {
            int amount = this.manaStorage.getManaStored(source);
            if (amount > 0) {
                mana.add(source, amount);
            }
        }
        return mana;
    }

    @Override
    public int getMaxMana() {
        // TODO Fix up
        return this.manaStorage.getMaxManaStored(Source.EARTH);
    }

    @Override
    public void setMana(Source source, int amount) {
        this.manaStorage.setMana(source, amount);
        this.setChanged();
        this.syncTile(true);
    }

    @Override
    public void setMana(SourceList mana) {
        this.manaStorage.setMana(mana);
        this.setChanged();
        this.syncTile(true);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack slotStack = this.items.get(index);
        super.setItem(index, stack);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, slotStack);
        if (index == 0 && !flag) {
            this.chargeTimeTotal = this.getChargeTimeTotal();
            this.chargeTime = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int slotIndex, ItemStack stack) {
        if (slotIndex == INPUT_SLOT_INDEX) {
            return stack.getItem() instanceof EssenceItem || stack.getItem() instanceof IWand;
        } else if (slotIndex == CHARGE_SLOT_INDEX) {
            return stack.getItem() instanceof IWand;
        } else {
            return false;
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return SLOTS_FOR_UP;
        } else if (side == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return SLOTS_FOR_SIDES;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
        return this.canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }
}
