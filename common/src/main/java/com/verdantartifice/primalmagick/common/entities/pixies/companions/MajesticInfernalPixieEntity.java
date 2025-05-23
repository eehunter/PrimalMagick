package com.verdantartifice.primalmagick.common.entities.pixies.companions;

import com.verdantartifice.primalmagick.common.entities.pixies.IMajesticPixie;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

/**
 * Definition of a majestic infernal pixie.  Greatest of the infernal pixies.
 * 
 * @author Daedalus4096
 */
public class MajesticInfernalPixieEntity extends AbstractInfernalPixieEntity implements IMajesticPixie {
    public MajesticInfernalPixieEntity(EntityType<? extends AbstractPixieEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected SpawnEggItem getSpawnItem() {
        return ItemsPM.MAJESTIC_INFERNAL_PIXIE.get();
    }

    @Override
    public int getSpellPower() {
        return 4;
    }
}
