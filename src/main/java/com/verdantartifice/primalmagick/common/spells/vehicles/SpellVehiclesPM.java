package com.verdantartifice.primalmagick.common.spells.vehicles;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class SpellVehiclesPM {
    private static final DeferredRegister<SpellVehicleType<?>> DEFERRED_TYPES = DeferredRegister.create(RegistryKeysPM.SPELL_VEHICLE_TYPES, PrimalMagick.MODID);
    public static final Supplier<IForgeRegistry<SpellVehicleType<?>>> TYPES = DEFERRED_TYPES.makeRegistry(RegistryBuilder::new);
    
    public static void init() {
        DEFERRED_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    public static final RegistryObject<SpellVehicleType<EmptySpellVehicle>> EMPTY = register("empty", EmptySpellVehicle.CODEC, EmptySpellVehicle.STREAM_CODEC);
    public static final RegistryObject<SpellVehicleType<BoltSpellVehicle>> BOLT = register("bolt", BoltSpellVehicle.CODEC, BoltSpellVehicle.STREAM_CODEC);
    public static final RegistryObject<SpellVehicleType<ProjectileSpellVehicle>> PROJECTILE = register("projectile", ProjectileSpellVehicle.CODEC, ProjectileSpellVehicle.STREAM_CODEC);
    public static final RegistryObject<SpellVehicleType<SelfSpellVehicle>> SELF = register("self", SelfSpellVehicle.CODEC, SelfSpellVehicle.STREAM_CODEC);
    public static final RegistryObject<SpellVehicleType<TouchSpellVehicle>> TOUCH = register("touch", TouchSpellVehicle.CODEC, TouchSpellVehicle.STREAM_CODEC);
    
    protected static <T extends AbstractSpellVehicle<T>> RegistryObject<SpellVehicleType<T>> register(String id, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return DEFERRED_TYPES.register(id, () -> new SpellVehicleType<T>(PrimalMagick.resource(id), codec, streamCodec));
    }
}
