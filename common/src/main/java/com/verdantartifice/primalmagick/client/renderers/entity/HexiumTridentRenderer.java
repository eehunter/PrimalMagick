package com.verdantartifice.primalmagick.client.renderers.entity;

import com.verdantartifice.primalmagick.common.entities.projectiles.AbstractTridentEntity;
import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer definition for a thrown hexium trident.
 * 
 * @author Daedalus4096
 */
public class HexiumTridentRenderer extends AbstractTridentRenderer {
    public static final ResourceLocation TEXTURE = ResourceUtils.loc("textures/entity/trident/hexium_trident.png");

    public HexiumTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractTridentEntity entity) {
        return TEXTURE;
    }
}
