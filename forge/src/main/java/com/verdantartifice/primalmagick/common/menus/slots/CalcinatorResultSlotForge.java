package com.verdantartifice.primalmagick.common.menus.slots;

import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Custom GUI slot for calcinator outputs.
 * 
 * @author Daedalus4096
 */
public class CalcinatorResultSlotForge extends GenericResultSlotForge {
    public CalcinatorResultSlotForge(Player player, IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
        super(player, inventoryIn, index, xPosition, yPosition);
    }
    
    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        super.checkTakeAchievements(stack);
        Services.EVENTS.firePlayerSmeltedEvent(this.player, stack);
    }
}
