package com.verdantartifice.primalmagic.common.containers;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagic.common.research.ResearchDiscipline;
import com.verdantartifice.primalmagic.common.research.ResearchEntry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class GrimoireContainer extends Container {
    protected Object topic;
    
    public GrimoireContainer(int windowId) {
        super(ContainersPM.GRIMOIRE, windowId);
        this.topic = null;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
    
    public Object getTopic() {
        return this.topic;
    }
    
    /**
     * New topic can either be null (for the category index), a ResearchDiscipline (for a listing
     * of that discipline's entries), or a ResearchEntry (for details of that entry).
     * @param newTopic
     */
    @Nullable
    public void setTopic(Object newTopic) {
        if (newTopic == null || newTopic instanceof ResearchDiscipline || newTopic instanceof ResearchEntry) {
            this.topic = newTopic;
        } else {
            throw new IllegalArgumentException("Invalid grimoire topic");
        }
    }
}
