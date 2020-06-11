package com.verdantartifice.primalmagic.common.containers;

import javax.annotation.Nonnull;

import com.verdantartifice.primalmagic.common.containers.slots.RuneSlot;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

/**
 * Server data container for the enchanted runescribing altar GUI.
 * 
 * @author Daedalus4096
 */
public class RunescribingAltarEnchantedContainer extends AbstractRunescribingAltarContainer {
    protected static final int RUNE_CAPACITY = 5;
    
    public RunescribingAltarEnchantedContainer(int id, @Nonnull PlayerInventory playerInv) {
        this(id, playerInv, new Inventory(RUNE_CAPACITY + 2));
    }
    
    public RunescribingAltarEnchantedContainer(int id, @Nonnull PlayerInventory playerInv, @Nonnull IInventory altarInv) {
        super(ContainersPM.RUNESCRIBING_ALTAR_ENCHANTED.get(), id, playerInv, altarInv);
    }
    
    @Override
    protected int getRuneCapacity() {
        return RUNE_CAPACITY;
    }
    
    @Override
    protected void addRuneSlots() {
        this.addSlot(new RuneSlot(this.altarInv, 2, 62, 17));
        this.addSlot(new RuneSlot(this.altarInv, 3, 44, 35));
        this.addSlot(new RuneSlot(this.altarInv, 4, 62, 35));
        this.addSlot(new RuneSlot(this.altarInv, 5, 80, 35));
        this.addSlot(new RuneSlot(this.altarInv, 6, 62, 53));
    }
}
