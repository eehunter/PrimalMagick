package com.verdantartifice.primalmagick.common.entities.pixies.companions;

import com.verdantartifice.primalmagick.common.entities.pixies.IBasicPixie;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

/**
 * Definition of a basic sky pixie.  Weakest of the sky pixies.
 * 
 * @author Daedalus4096
 */
public class BasicSkyPixieEntity extends AbstractSkyPixieEntity implements IBasicPixie {
    public BasicSkyPixieEntity(EntityType<? extends AbstractPixieEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected SpawnEggItem getSpawnItem() {
        return ItemsPM.BASIC_SKY_PIXIE.get();
    }
}
