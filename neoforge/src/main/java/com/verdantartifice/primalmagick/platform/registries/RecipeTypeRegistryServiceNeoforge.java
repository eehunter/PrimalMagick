package com.verdantartifice.primalmagick.platform.registries;

import com.verdantartifice.primalmagick.Constants;
import com.verdantartifice.primalmagick.platform.services.registries.IRecipeTypeRegistryService;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Neoforge implementation of the recipe type registry service.
 *
 * @author Daedalus4096
 */
public class RecipeTypeRegistryServiceNeoforge extends AbstractRegistryServiceNeoforge<RecipeType<?>> implements IRecipeTypeRegistryService {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Constants.MOD_ID);

    @Override
    protected Supplier<DeferredRegister<RecipeType<?>>> getDeferredRegisterSupplier() {
        return () -> TYPES;
    }

    @Override
    protected Registry<RecipeType<?>> getRegistry() {
        return BuiltInRegistries.RECIPE_TYPE;
    }
}
