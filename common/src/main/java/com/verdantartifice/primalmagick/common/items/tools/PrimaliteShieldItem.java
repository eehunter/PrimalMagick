package com.verdantartifice.primalmagick.common.items.tools;

import com.verdantartifice.primalmagick.client.renderers.itemstack.PrimaliteShieldISTER;
import com.verdantartifice.primalmagick.common.items.IHasCustomRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Definition of a shield item made of the magickal metal primalite.
 * 
 * @author Daedalus4096
 */
public class PrimaliteShieldItem extends AbstractTieredShieldItem implements IHasCustomRenderer {
    private BlockEntityWithoutLevelRenderer customRenderer = null;

    public PrimaliteShieldItem(Item.Properties properties) {
        super(ItemTierPM.PRIMALITE, properties);
    }

    @Override
    public Supplier<BlockEntityWithoutLevelRenderer> getCustomRendererSupplier() {
        if (this.customRenderer == null) {
            this.customRenderer = this.getCustomRendererSupplierUncached().get();
        }
        return () -> this.customRenderer;
    }

    @Override
    public Supplier<BlockEntityWithoutLevelRenderer> getCustomRendererSupplierUncached() {
        return PrimaliteShieldISTER::new;
    }
}
