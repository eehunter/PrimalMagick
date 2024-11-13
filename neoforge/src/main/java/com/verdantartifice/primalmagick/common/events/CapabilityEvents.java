package com.verdantartifice.primalmagick.common.events;

import com.verdantartifice.primalmagick.Constants;
import com.verdantartifice.primalmagick.common.capabilities.IItemHandlerPM;
import com.verdantartifice.primalmagick.common.tiles.BlockEntityTypesPM;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class CapabilityEvents {
    @SubscribeEvent
    public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityTypesPM.CALCINATOR.get(), (be, face) -> cast(be.getRawItemHandler(face)));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityTypesPM.CARVED_BOOKSHELF.get(), (be, face) -> cast(be.getRawItemHandler(face)));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityTypesPM.CONCOCTER.get(), (be, face) -> cast(be.getRawItemHandler(face)));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityTypesPM.DISSOLUTION_CHAMBER.get(), (be, face) -> cast(be.getRawItemHandler(face)));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityTypesPM.ESSENCE_CASK.get(), (be, face) -> cast(be.getRawItemHandler(face)));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityTypesPM.ESSENCE_FURNACE.get(), (be, face) -> cast(be.getRawItemHandler(face)));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityTypesPM.ESSENCE_TRANSMUTER.get(), (be, face) -> cast(be.getRawItemHandler(face)));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityTypesPM.HONEY_EXTRACTOR.get(), (be, face) -> cast(be.getRawItemHandler(face)));
    }

    private static IItemHandler cast(final IItemHandlerPM handler) {
        if (handler instanceof IItemHandler nfHandler) {
            return nfHandler;
        } else {
            return null;
        }
    }
}
