package com.verdantartifice.primalmagick.common.theorycrafting.materials;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class ProjectMaterialTypesPM {
    private static final DeferredRegister<ProjectMaterialType<?>> DEFERRED_TYPES = DeferredRegister.create(RegistryKeysPM.PROJECT_MATERIAL_TYPES, PrimalMagick.MODID);
    public static final Supplier<IForgeRegistry<ProjectMaterialType<?>>> TYPES = DEFERRED_TYPES.makeRegistry(RegistryBuilder::new);
    
    public static void init() {
        DEFERRED_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    public static final RegistryObject<ProjectMaterialType<ExperienceProjectMaterial>> EXPERIENCE = register("experience", ExperienceProjectMaterial.CODEC, ExperienceProjectMaterial::fromNetwork);
    
    protected static <T extends AbstractProjectMaterial<T>> RegistryObject<ProjectMaterialType<T>> register(String id, Codec<T> codec, FriendlyByteBuf.Reader<T> networkReader) {
        return DEFERRED_TYPES.register(id, () -> new ProjectMaterialType<T>(PrimalMagick.resource(id), codec, networkReader));
    }
}
