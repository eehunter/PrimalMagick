package com.verdantartifice.primalmagick.common.network.packets.recipe_book;

import com.verdantartifice.primalmagick.common.menus.base.IArcaneRecipeBookMenu;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToServer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Packet which informs a server to place a recipe in an arcane recipe book menu.
 * 
 * @author Daedalus4096
 */
public class PlaceArcaneRecipePacket implements IMessageToServer {
    public static final StreamCodec<RegistryFriendlyByteBuf, PlaceArcaneRecipePacket> STREAM_CODEC = StreamCodec.ofMember(PlaceArcaneRecipePacket::encode, PlaceArcaneRecipePacket::decode);

    protected final int containerId;
    protected final ResourceLocation recipeId;
    protected final boolean shiftDown;

    public PlaceArcaneRecipePacket(int containerId, RecipeHolder<?> recipe, boolean shiftDown) {
        this(containerId, recipe.id(), shiftDown);
    }
    
    protected PlaceArcaneRecipePacket(int containerId, ResourceLocation recipeId, boolean shiftDown) {
        this.containerId = containerId;
        this.recipeId = recipeId;
        this.shiftDown = shiftDown;
    }
    
    public static void encode(PlaceArcaneRecipePacket message, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(message.containerId);
        buf.writeResourceLocation(message.recipeId);
        buf.writeBoolean(message.shiftDown);
    }
    
    public static PlaceArcaneRecipePacket decode(RegistryFriendlyByteBuf buf) {
        return new PlaceArcaneRecipePacket(buf.readVarInt(), buf.readResourceLocation(), buf.readBoolean());
    }
    
    public static void onMessage(PlaceArcaneRecipePacket message, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        player.resetLastActionTime();
        if (!player.isSpectator() && player.containerMenu.containerId == message.containerId && player.containerMenu instanceof IArcaneRecipeBookMenu<?, ?> bookMenu) {
            player.getServer().getRecipeManager().byKey(message.recipeId).ifPresent(recipe -> {
                bookMenu.handlePlacement(message.shiftDown, recipe, player);
            });
        }
    }
}
