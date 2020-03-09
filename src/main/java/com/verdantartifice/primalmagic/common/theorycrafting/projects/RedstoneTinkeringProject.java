package com.verdantartifice.primalmagic.common.theorycrafting.projects;

import com.verdantartifice.primalmagic.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagic.common.theorycrafting.AbstractProject;
import com.verdantartifice.primalmagic.common.theorycrafting.AbstractProjectMaterial;
import com.verdantartifice.primalmagic.common.theorycrafting.ItemProjectMaterial;
import com.verdantartifice.primalmagic.common.theorycrafting.ObservationProjectMaterial;
import com.verdantartifice.primalmagic.common.util.WeightedRandomBag;

import net.minecraft.item.Items;

/**
 * Definition of a research project option.
 * 
 * @author Daedalus4096
 */
public class RedstoneTinkeringProject extends AbstractProject {
    public static final String TYPE = "redstone_tinkering";
    
    protected static final WeightedRandomBag<AbstractProjectMaterial> OPTIONS = new WeightedRandomBag<>();
    protected static final SimpleResearchKey RESEARCH = SimpleResearchKey.parse("BASIC_MAGITECH");
    
    static {
        OPTIONS.add(new ItemProjectMaterial(Items.ACTIVATOR_RAIL, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.DISPENSER, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.DROPPER, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.DAYLIGHT_DETECTOR, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.PISTON, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.HOPPER, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.TNT, true), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.STICKY_PISTON, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.COMPARATOR, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.CLOCK, false), 1);
        OPTIONS.add(new ObservationProjectMaterial(), 5);
    }
    
    @Override
    protected String getProjectType() {
        return TYPE;
    }

    @Override
    protected WeightedRandomBag<AbstractProjectMaterial> getMaterialOptions() {
        return OPTIONS;
    }
    
    @Override
    public SimpleResearchKey getRequiredResearch() {
        return RESEARCH;
    }
}
