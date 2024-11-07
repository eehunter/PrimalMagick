package com.verdantartifice.primalmagick.common.items.tools;

import com.verdantartifice.primalmagick.client.renderers.itemstack.HexiumShieldISTER;
import com.verdantartifice.primalmagick.common.items.IHasCustomRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Definition of a shield item made of the magickal metal hexium.
 * 
 * @author Daedalus4096
 */
public class HexiumShieldItem extends AbstractTieredShieldItem implements IHasCustomRenderer {
    public HexiumShieldItem(Item.Properties properties) {
        super(ItemTierPM.HEXIUM, properties);
    }

    @Override
    public Supplier<BlockEntityWithoutLevelRenderer> getCustomRendererSupplier() {
        return HexiumShieldISTER::new;
    }
}
