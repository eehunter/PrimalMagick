package com.verdantartifice.primalmagick.common.items.tools;

import com.verdantartifice.primalmagick.common.items.IHasCustomRendererForge;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ForbiddenTridentItemForge extends ForbiddenTridentItem implements IHasCustomRendererForge {
    private IClientItemExtensions renderProps;

    public ForbiddenTridentItemForge(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(this.getRenderProperties());
    }

    public IClientItemExtensions getRenderProperties() {
        if (this.renderProps == null) {
            this.renderProps = this.getRenderPropertiesUncached();
        }
        return this.renderProps;
    }
}
