package com.verdantartifice.primalmagick.common.loot.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verdantartifice.primalmagick.common.loot.LootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Global loot modifier that allows mobs in the conditioned tag to drop Blood-Scrawled Ravings when killed.
 * 
 * @author Daedalus4096
 */
public class BloodNotesModifier extends LootModifier {
    public static final MapCodec<BloodNotesModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> codecStart(inst).and(
            TagKey.codec(Registries.ENTITY_TYPE).fieldOf("targetTag").forGetter(m -> m.targetTag)
        ).apply(inst, BloodNotesModifier::new));
    
    protected final TagKey<EntityType<?>> targetTag;

    public BloodNotesModifier(LootItemCondition[] conditionsIn, TagKey<EntityType<?>> targetTag) {
        super(conditionsIn);
        this.targetTag = targetTag;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return LootModifiers.bloodNotes(generatedLoot, context, this.targetTag);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
