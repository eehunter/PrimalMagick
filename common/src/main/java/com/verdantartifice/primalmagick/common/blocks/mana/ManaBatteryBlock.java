package com.verdantartifice.primalmagick.common.blocks.mana;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verdantartifice.primalmagick.common.misc.DeviceTier;
import com.verdantartifice.primalmagick.common.misc.ITieredDevice;
import com.verdantartifice.primalmagick.common.tiles.BlockEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.base.IOwnedTileEntity;
import com.verdantartifice.primalmagick.common.tiles.mana.ManaBatteryTileEntity;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Block definition for a mana battery, such as the Mana Nexus.  A mana battery will automatically
 * siphon mana from nearby fonts and use that to charge its internal storage, which can then in turn
 * be used to charge wands or other devices.  Also accepts input mana from essence, breaking it down
 * like the Wand Charger does.
 * 
 * @author Daedalus4096
 */
public class ManaBatteryBlock extends BaseEntityBlock implements ITieredDevice {
    public static final MapCodec<ManaBatteryBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DeviceTier.CODEC.fieldOf("tier").forGetter(b -> b.tier),
            propertiesCodec()
    ).apply(instance, ManaBatteryBlock::new));
    
    protected final DeviceTier tier;
    
    public ManaBatteryBlock(DeviceTier tier, Block.Properties properties) {
        super(properties);
        this.tier = tier;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return Services.BLOCK_ENTITY_PROTOTYPES.manaBattery().create(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityTypesPM.MANA_BATTERY.get(), Services.BLOCK_ENTITY_TICKERS.manaBattery());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
            // Open the GUI for the battery
            BlockEntity tile = pLevel.getBlockEntity(pPos);
            if (tile instanceof ManaBatteryTileEntity batteryTile) {
                Services.PLAYER.openMenu(serverPlayer, batteryTile, tile.getBlockPos());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            if (pLevel.getBlockEntity(pPos) instanceof ManaBatteryTileEntity batteryTile) {
                // Before the block entity is removed, invalidate its route table
                batteryTile.getRouteTable().invalidate();

                // Drop the tile entity's inventory into the world when the block is replaced
                batteryTile.dropContents(pLevel, pPos);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public DeviceTier getDeviceTier() {
        return this.tier;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        // Set the block entity's owner when placed by a player
        if (!pLevel.isClientSide && pPlacer instanceof Player player && pLevel.getBlockEntity(pPos) instanceof IOwnedTileEntity ownedTile) {
            ownedTile.setTileOwner(player);
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
