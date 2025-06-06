package com.verdantartifice.primalmagick.common.menus;

import com.verdantartifice.primalmagick.common.capabilities.IItemHandlerPM;
import com.verdantartifice.primalmagick.common.items.IItemHandlerChangeListener;
import com.verdantartifice.primalmagick.common.menus.base.AbstractTileSidedInventoryMenu;
import com.verdantartifice.primalmagick.common.menus.slots.FilteredSlotProperties;
import com.verdantartifice.primalmagick.common.theorycrafting.IWritingImplement;
import com.verdantartifice.primalmagick.common.theorycrafting.TheorycraftManager;
import com.verdantartifice.primalmagick.common.tiles.devices.ResearchTableTileEntity;
import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Server data container for the research table GUI.
 * 
 * @author Daedalus4096
 */
public class ResearchTableMenu extends AbstractTileSidedInventoryMenu<ResearchTableTileEntity> implements IItemHandlerChangeListener {
    public static final ResourceLocation PAPER_SLOT_TEXTURE = ResourceUtils.loc("item/empty_paper_slot");
    public static final ResourceLocation PENCIL_SLOT_TEXTURE = ResourceUtils.loc("item/empty_pencil_slot");
    protected static final Component PAPER_SLOT_TOOLTIP = Component.translatable("tooltip.primalmagick.research_table.slot.paper");
    protected static final Component PENCIL_SLOT_TOOLTIP = Component.translatable("tooltip.primalmagick.research_table.slot.pencil");
    
    protected final Player player;
    protected final Slot paperSlot;
    protected final Slot pencilSlot;
    protected final DataSlot writingReady = DataSlot.standalone();

    public ResearchTableMenu(int windowId, Inventory inv, BlockPos pos) {
        this(windowId, inv, pos, null);
    }

    public ResearchTableMenu(int windowId, Inventory inv, BlockPos pos, ResearchTableTileEntity table) {
        super(MenuTypesPM.RESEARCH_TABLE.get(), windowId, ResearchTableTileEntity.class, inv.player.level(), pos, table);
        this.player = inv.player;
        
        // Slot 0: Pencil
        this.pencilSlot = this.addSlot(Services.MENU.makeFilteredSlot(this.getTileInventory(Direction.UP), 0, 8, 8,
                new FilteredSlotProperties().background(PENCIL_SLOT_TEXTURE).tooltip(PENCIL_SLOT_TOOLTIP).typeOf(IWritingImplement.class)));
        
        // Slot 1: Paper
        this.paperSlot = this.addSlot(Services.MENU.makeFilteredSlot(this.getTileInventory(Direction.UP), 1, 206, 8,
                new FilteredSlotProperties().background(PAPER_SLOT_TEXTURE).tooltip(PAPER_SLOT_TOOLTIP).item(Items.PAPER)));
        
        // Slots 2-28: Player backpack
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + (i * 9) + 9, 35 + (j * 18), 140 + (i * 18)));
            }
        }
        
        // Slots 29-37: Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 35 + (i * 18), 198));
        }
        
        this.addDataSlot(this.writingReady).set(0);
        this.checkWritingImplements();
    }

    protected void checkWritingImplements() {
        // Set whether the container has writing tools ready; 1 for yes, 0 for no
        boolean ready = (!this.getWritingImplementStack().isEmpty() && !this.getPaperStack().isEmpty());
        this.writingReady.set(ready ? 1 : 0);
    }

    @Override
    public void itemsChanged(int itemHandlerIndex, IItemHandlerPM invBasic) {
        this.checkWritingImplements();
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if (index >= 2 && index < 29) {
                // If transferring from the backpack, move paper and writing implements to the appropriate slots, and everything else to the hotbar
                if (this.pencilSlot.mayPlace(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.paperSlot.mayPlace(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(slotStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index >= 29 && index < 38) {
                // If transferring from the hotbar, move paper and writing implements to the appropriate slots, and everything else to the backpack
                if (this.pencilSlot.mayPlace(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.paperSlot.mayPlace(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(slotStack, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(slotStack, 2, 38, false)) {
                // Move all other transfers to the backpack or hotbar
                return ItemStack.EMPTY;
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(playerIn, slotStack);
        }
        return stack;
    }
    
    @Nonnull
    protected ItemStack getWritingImplementStack() {
        return this.tile.getItem(0, 0);
    }
    
    @Nonnull
    protected ItemStack getPaperStack() {
        return this.tile.getItem(0, 1);
    }
    
    public boolean isWritingReady() {
        return this.writingReady.get() != 0;
    }
    
    public void consumeWritingImplements() {
        // Don't consume if in creative mode
        if (!this.player.hasInfiniteMaterials()) {
            // Consume ink, if applicable
            ItemStack inkStack = this.getWritingImplementStack();
            if (!inkStack.isEmpty() && inkStack.getItem() instanceof IWritingImplement inkItem && inkItem.isDamagedOnUse() && this.player.level() instanceof ServerLevel serverLevel) {
                inkStack.hurtAndBreak(1, serverLevel, this.player instanceof ServerPlayer serverPlayer ? serverPlayer : null, item -> {});
            }

            // Consume paper
            Services.CAPABILITIES.itemHandler(this.tile, Direction.UP).ifPresent(inv -> inv.extractItem(1, 1, false));
        }
    }
    
    @Nonnull
    public List<Component> getNearbyAidBlockNames() {
        Set<Block> nearby = this.containerLevelAccess.evaluate((level, pos) -> {
            return TheorycraftManager.getNearbyAidBlocks(this.player.level(), pos);
        }, Collections.emptySet());
        return nearby.stream().map(b -> b.getName()).distinct().sorted(Comparator.comparing(c -> c.getString())).collect(Collectors.toList());
    }
}
