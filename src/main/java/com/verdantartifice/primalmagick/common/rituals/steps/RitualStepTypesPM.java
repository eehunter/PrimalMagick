package com.verdantartifice.primalmagick.common.rituals.steps;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.tiles.rituals.RitualAltarTileEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class RitualStepTypesPM {
    private static final DeferredRegister<RitualStepType<?>> DEFERRED_TYPES = DeferredRegister.create(RegistryKeysPM.RITUAL_STEP_TYPES, PrimalMagick.MODID);
    public static final Supplier<IForgeRegistry<RitualStepType<?>>> TYPES = DEFERRED_TYPES.makeRegistry(RegistryBuilder::new);
    
    public static void init() {
        DEFERRED_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    // TODO Add actions
    public static final RegistryObject<RitualStepType<OfferingRitualStep>> OFFERING = register("offering", OfferingRitualStep.CODEC, OfferingRitualStep.STREAM_CODEC, null);
    public static final RegistryObject<RitualStepType<PropRitualStep>> PROP = register("prop", PropRitualStep.CODEC, PropRitualStep.STREAM_CODEC, null);
    public static final RegistryObject<RitualStepType<UniversalRitualStep>> UNIVERSAL = register("universal", UniversalRitualStep.CODEC, UniversalRitualStep.STREAM_CODEC, null);
    
    protected static <T extends AbstractRitualStep<T>> RegistryObject<RitualStepType<T>> register(String id, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
            BiFunction<RitualAltarTileEntity, T, Boolean> action) {
        return DEFERRED_TYPES.register(id, () -> new RitualStepType<T>(PrimalMagick.resource(id), codec, streamCodec, action));
    }
}
