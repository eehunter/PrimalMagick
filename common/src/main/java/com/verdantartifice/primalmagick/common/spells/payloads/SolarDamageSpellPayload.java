package com.verdantartifice.primalmagick.common.spells.payloads;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.verdantartifice.primalmagick.common.research.ResearchEntries;
import com.verdantartifice.primalmagick.common.research.keys.ResearchEntryKey;
import com.verdantartifice.primalmagick.common.research.requirements.AbstractRequirement;
import com.verdantartifice.primalmagick.common.research.requirements.ResearchRequirement;
import com.verdantartifice.primalmagick.common.sounds.SoundsPM;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.spells.SpellPropertiesPM;
import com.verdantartifice.primalmagick.common.spells.SpellProperty;
import com.verdantartifice.primalmagick.common.spells.SpellPropertyConfiguration;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Definition of a solar damage spell.  Does standard damage to the target.  As a secondary effect, sets
 * undead targets on fire.  The length of the fire effect scales with the duration property of the payload.
 * 
 * @author Daedalus4096
 */
public class SolarDamageSpellPayload extends AbstractDamageSpellPayload<SolarDamageSpellPayload> {
    public static final SolarDamageSpellPayload INSTANCE = new SolarDamageSpellPayload();
    
    public static final MapCodec<SolarDamageSpellPayload> CODEC = MapCodec.unit(SolarDamageSpellPayload.INSTANCE);
    public static final StreamCodec<ByteBuf, SolarDamageSpellPayload> STREAM_CODEC = StreamCodec.unit(SolarDamageSpellPayload.INSTANCE);
    
    public static final String TYPE = "solar_damage";
    protected static final AbstractRequirement<?> REQUIREMENT = new ResearchRequirement(new ResearchEntryKey(ResearchEntries.SPELL_PAYLOAD_SOLAR));

    public static AbstractRequirement<?> getRequirement() {
        return REQUIREMENT;
    }
    
    public static SolarDamageSpellPayload getInstance() {
        return INSTANCE;
    }
    
    @Override
    protected List<SpellProperty> getPropertiesInner() {
        return ImmutableList.<SpellProperty>builder().addAll(super.getPropertiesInner()).add(SpellPropertiesPM.DURATION.get()).build();
    }

    @Override
    public SpellPayloadType<SolarDamageSpellPayload> getType() {
        return SpellPayloadsPM.SOLAR_DAMAGE.get();
    }

    @Override
    public Source getSource() {
        return Sources.SUN;
    }

    @Override
    public void playSounds(Level world, BlockPos origin) {
        world.playSound(null, origin, SoundsPM.SUNBEAM.get(), SoundSource.PLAYERS, 1.0F, 1.0F + (float)(world.random.nextGaussian() * 0.05D));
    }

    @Override
    protected String getPayloadType() {
        return TYPE;
    }

    @Override
    protected void applySecondaryEffects(HitResult target, Vec3 burstPoint, SpellPackage spell, Level world, LivingEntity caster, ItemStack spellSource) {
        int duration = this.getDurationSeconds(spell, spellSource, world.registryAccess());
        if (target != null && target.getType() == HitResult.Type.ENTITY && duration > 0) {
            EntityHitResult entityTarget = (EntityHitResult)target;
            if (entityTarget.getEntity() != null && entityTarget.getEntity() instanceof LivingEntity) {
                // Make the entity glow and set it on fire if undead
                LivingEntity entity = (LivingEntity)entityTarget.getEntity();
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * duration, 0));
                if (entity.isInvertedHealAndHarm()) {
                    entity.igniteForSeconds(duration);
                }
            }
        }
    }
    
    @Override
    public int getBaseManaCost(SpellPropertyConfiguration properties) {
        int power = properties.get(SpellPropertiesPM.POWER.get());
        int duration = properties.get(SpellPropertiesPM.DURATION.get());
        return (1 << Math.max(0, power - 1)) + (duration == 0 ? 0 : (1 << Math.max(0, duration - 1)) >> 1);
    }

    protected int getDurationSeconds(SpellPackage spell, ItemStack spellSource, HolderLookup.Provider registries) {
        return 2 * this.getModdedPropertyValue(SpellPropertiesPM.DURATION.get(), spell, spellSource, registries);
    }

    @Override
    public Component getDetailTooltip(SpellPackage spell, ItemStack spellSource, HolderLookup.Provider registries) {
        return Component.translatable("spells.primalmagick.payload." + this.getPayloadType() + ".detail_tooltip", DECIMAL_FORMATTER.format(this.getBaseDamage(spell, spellSource, registries)),
                DECIMAL_FORMATTER.format(this.getDurationSeconds(spell, spellSource, registries)));
    }
}
