package com.verdantartifice.primalmagick.common.tags;

import com.verdantartifice.primalmagick.PrimalMagick;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Collection of custom-defined enchantment tags for the mod.  Used to determine tag contents
 * and for data file generation.
 * 
 * @author Daedalus4096
 */
public class EnchantmentTagsPM {
    public static final TagKey<Enchantment> DIGGING_AREA_EXCLUSIVE = create("exclusive_set/digging_area");
    
    private static TagKey<Enchantment> create(String name) {
        return TagKey.create(Registries.ENCHANTMENT, PrimalMagick.resource(name));
    }
}
