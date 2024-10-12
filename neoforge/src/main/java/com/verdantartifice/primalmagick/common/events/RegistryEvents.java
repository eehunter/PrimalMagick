package com.verdantartifice.primalmagick.common.events;

import com.verdantartifice.primalmagick.Constants;
import com.verdantartifice.primalmagick.common.books.grids.rewards.GridRewardTypeRegistration;
import com.verdantartifice.primalmagick.common.research.keys.ResearchKeyTypeRegistration;
import com.verdantartifice.primalmagick.common.research.requirements.RequirementTypeRegistration;
import com.verdantartifice.primalmagick.common.spells.SpellPropertyRegistration;
import com.verdantartifice.primalmagick.common.spells.mods.SpellModTypeRegistration;
import com.verdantartifice.primalmagick.common.spells.payloads.SpellPayloadTypeRegistration;
import com.verdantartifice.primalmagick.common.spells.vehicles.SpellVehicleTypeRegistration;
import com.verdantartifice.primalmagick.common.theorycrafting.materials.ProjectMaterialTypeRegistration;
import com.verdantartifice.primalmagick.common.theorycrafting.rewards.RewardTypeRegistration;
import com.verdantartifice.primalmagick.common.theorycrafting.weights.WeightFunctionTypeRegistration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

/**
 * Handlers for mod registry-related events.
 *
 * @author Daedalus4096
 */
@EventBusSubscriber(modid = Constants.MOD_ID)
public class RegistryEvents {
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(ResearchKeyTypeRegistration.TYPES);
        event.register(RequirementTypeRegistration.TYPES);
        event.register(ProjectMaterialTypeRegistration.TYPES);
        event.register(RewardTypeRegistration.TYPES);
        event.register(WeightFunctionTypeRegistration.TYPES);
        event.register(SpellPropertyRegistration.PROPERTIES);
        event.register(SpellModTypeRegistration.TYPES);
        event.register(SpellVehicleTypeRegistration.TYPES);
        event.register(SpellPayloadTypeRegistration.TYPES);
        event.register(GridRewardTypeRegistration.TYPES);
    }
}
