package com.verdantartifice.primalmagick.client.renderers.itemstack;

import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom item stack renderer for a primalite shield.
 * 
 * @author Daedalus4096
 */
public class PrimaliteShieldISTER extends AbstractTieredShieldISTER {
    public static final ResourceLocation TEXTURE_SHIELD_BASE = ResourceUtils.loc("entity/shield/primalite_shield_base");
    public static final ResourceLocation TEXTURE_SHIELD_NO_PATTERN = ResourceUtils.loc("entity/shield/primalite_shield_base_nopattern");
    @SuppressWarnings("deprecation")
    protected static final Material LOCATION_SHIELD_BASE = new Material(TextureAtlas.LOCATION_BLOCKS, TEXTURE_SHIELD_BASE);
    @SuppressWarnings("deprecation")
    protected static final Material LOCATION_SHIELD_NO_PATTERN = new Material(TextureAtlas.LOCATION_BLOCKS, TEXTURE_SHIELD_NO_PATTERN);

    @Override
    protected Material getRenderMaterial(boolean hasPattern) {
        return hasPattern ? LOCATION_SHIELD_BASE : LOCATION_SHIELD_NO_PATTERN;
    }
}
