package com.verdantartifice.primalmagick.common.wands;

import com.mojang.serialization.Codec;
import com.verdantartifice.primalmagick.common.util.ResourceUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Definition of a wand gem data structure.  Wand gems determine the maximum amount of mana that can be
 * contained by a wand.  Also contains a static registry of all types of wand gems in the mod.
 * 
 * @author Daedalus4096
 */
@Immutable
public class WandGem implements IWandComponent {
    protected static final Map<String, WandGem> REGISTRY = new HashMap<>();
    
    public static final Codec<WandGem> CODEC = Codec.STRING.xmap(WandGem::getWandGem, WandGem::getTag);
    public static final StreamCodec<ByteBuf, WandGem> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(WandGem::getWandGem, WandGem::getTag);
    
    public static final WandGem APPRENTICE = new WandGem("apprentice", Rarity.COMMON, 7500);
    public static final WandGem ADEPT = new WandGem("adept", Rarity.UNCOMMON, 25000);
    public static final WandGem WIZARD = new WandGem("wizard", Rarity.RARE, 75000);
    public static final WandGem ARCHMAGE = new WandGem("archmage", Rarity.EPIC, 250000);
    public static final WandGem CREATIVE = new WandGem("creative", Rarity.EPIC, -1);    // Creative-only wand gem allowing infinite mana
    
    protected final String tag;                 // Unique identifier for the wand gem
    protected final int capacity;               // The amount of mana the wand can hold
    protected final Rarity rarity;              // The gem's rarity, used to color its name and determine completed wand rarity
    protected final ResourceLocation mrlNamespace;  // Resource location of the gem's model, stored in a blockstate file
    
    public WandGem(@Nonnull String tag, @Nonnull Rarity rarity, int capacity) {
        this(tag, rarity, capacity, ResourceUtils.loc(tag + "_wand_gem"));
    }
    
    public WandGem(@Nonnull String tag, @Nonnull Rarity rarity, int capacity, @Nonnull ResourceLocation mrln) {
        if (REGISTRY.containsKey(tag)) {
            // Don't allow a given gem to be registered more than once
            throw new IllegalArgumentException("Wand gem " + tag + " already registered!");
        }
        this.tag = tag;
        this.capacity = capacity;
        this.rarity = rarity;
        this.mrlNamespace = mrln;
        REGISTRY.put(tag, this);
    }
    
    @Override
    public String getTag() {
        return this.tag;
    }
    
    @Override
    public Type getComponentType() {
        return IWandComponent.Type.GEM;
    }

    /**
     * Return the capacity of this wand gem in centimana.
     *
     * @return the capacity of this wand gem
     */
    public int getCapacity() {
        return this.capacity;
    }
    
    @Override
    public Rarity getRarity() {
        return this.rarity;
    }
    
    @Nonnull
    public ResourceLocation getModelResourceLocationNamespace() {
        return this.mrlNamespace;
    }
    
    @Nonnull
    public static Collection<WandGem> getAllWandGems() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }
    
    @Nullable
    public static WandGem getWandGem(@Nullable String tag) {
        return REGISTRY.get(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WandGem wandGem)) return false;
        return capacity == wandGem.capacity && Objects.equals(tag, wandGem.tag) && rarity == wandGem.rarity && Objects.equals(mrlNamespace, wandGem.mrlNamespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, capacity, rarity, mrlNamespace);
    }
}
