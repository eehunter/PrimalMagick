package com.verdantartifice.primalmagick.client.renderers.entity;

import com.verdantartifice.primalmagick.client.renderers.entity.layers.HallowsteelGolemCracksLayer;
import com.verdantartifice.primalmagick.common.entities.golems.HallowsteelGolemEntity;
import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Entity renderer for a hallowsteel golem.
 * 
 * @author Daedalus4096
 */
public class HallowsteelGolemRenderer extends AbstractEnchantedGolemRenderer<HallowsteelGolemEntity> {
    protected static final ResourceLocation TEXTURE = ResourceUtils.loc("textures/entity/hallowsteel_golem/hallowsteel_golem.png");
    
    public HallowsteelGolemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new HallowsteelGolemCracksLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(HallowsteelGolemEntity entity) {
        return TEXTURE;
    }
}
