package com.verdantartifice.primalmagic.common.spells.payloads;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagic.common.sources.SourceList;
import com.verdantartifice.primalmagic.common.spells.SpellProperty;
import com.verdantartifice.primalmagic.common.spells.packages.ISpellPackage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class AbstractDamageSpellPayload extends AbstractSpellPayload {
    public AbstractDamageSpellPayload() {
        super();
    }
    
    public AbstractDamageSpellPayload(int power) {
        super();
        this.getProperty("power").setValue(power);
    }
    
    @Override
    protected Map<String, SpellProperty> initProperties() {
        Map<String, SpellProperty> propMap = super.initProperties();
        propMap.put("power", new SpellProperty("power", "primalmagic.spell.property.power", 1, 5));
        return propMap;
    }
    
    protected abstract float getTotalDamage();

    @Override
    public void execute(RayTraceResult target, ISpellPackage spell, World world, PlayerEntity caster) {
        if (target != null && target.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult entityTarget = (EntityRayTraceResult)target;
            if (entityTarget.getEntity() != null) {
                entityTarget.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(entityTarget.getEntity(), caster), this.getTotalDamage());
            }
        }
        this.applySecondaryEffects(target, spell, world, caster);
    }
    
    protected void applySecondaryEffects(@Nullable RayTraceResult target, @Nonnull ISpellPackage spell, @Nonnull World world, @Nonnull PlayerEntity caster) {
        // Do nothing by default
    }

    @Override
    public SourceList getManaCost() {
        return new SourceList().add(this.getSource(), this.getPropertyValue("power"));
    }
}
