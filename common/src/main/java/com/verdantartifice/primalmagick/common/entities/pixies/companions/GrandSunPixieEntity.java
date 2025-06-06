package com.verdantartifice.primalmagick.common.entities.pixies.companions;

import com.verdantartifice.primalmagick.common.entities.pixies.IGrandPixie;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

/**
 * Definition of a grand sun pixie.  Middle of the sun pixies.
 * 
 * @author Daedalus4096
 */
public class GrandSunPixieEntity extends AbstractSunPixieEntity implements IGrandPixie {
    public GrandSunPixieEntity(EntityType<? extends AbstractPixieEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected SpawnEggItem getSpawnItem() {
        return ItemsPM.GRAND_SUN_PIXIE.get();
    }
}
