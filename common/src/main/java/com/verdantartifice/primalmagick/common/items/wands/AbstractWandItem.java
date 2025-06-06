package com.verdantartifice.primalmagick.common.items.wands;

import com.verdantartifice.primalmagick.client.fx.FxDispatcher;
import com.verdantartifice.primalmagick.client.util.ClientUtils;
import com.verdantartifice.primalmagick.common.attunements.AttunementManager;
import com.verdantartifice.primalmagick.common.attunements.AttunementThreshold;
import com.verdantartifice.primalmagick.common.attunements.AttunementType;
import com.verdantartifice.primalmagick.common.capabilities.ManaStorage;
import com.verdantartifice.primalmagick.common.components.DataComponentsPM;
import com.verdantartifice.primalmagick.common.crafting.IWandTransform;
import com.verdantartifice.primalmagick.common.crafting.WandTransforms;
import com.verdantartifice.primalmagick.common.effects.EffectsPM;
import com.verdantartifice.primalmagick.common.enchantments.EnchantmentHelperPM;
import com.verdantartifice.primalmagick.common.enchantments.EnchantmentsPM;
import com.verdantartifice.primalmagick.common.items.IHasCustomRenderer;
import com.verdantartifice.primalmagick.common.items.armor.IManaDiscountGear;
import com.verdantartifice.primalmagick.common.research.ResearchEntries;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.research.keys.ResearchEntryKey;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.spells.SpellManager;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.stats.StatsManager;
import com.verdantartifice.primalmagick.common.stats.StatsPM;
import com.verdantartifice.primalmagick.common.wands.IInteractWithWand;
import com.verdantartifice.primalmagick.common.wands.IWand;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Base item definition for a wand.  Wands store mana for use in crafting and, optionally, casting spells.
 * They are replenished by drawing from mana fonts or being charged in a wand charger.  The wand's mana is
 * stored internally as centimana (hundredths of mana points).
 * 
 * @author Daedalus4096
 */
public abstract class AbstractWandItem extends Item implements IWand, IHasCustomRenderer {
    protected static final DecimalFormat MANA_FORMATTER = new DecimalFormat("#######.##");
    protected static final ResearchEntryKey WAND_TRANSFORM_HINT_KEY = new ResearchEntryKey(ResearchEntries.WAND_TRANSFORM_HINT);
    
    public AbstractWandItem(Properties properties) {
        super(properties);
    }

    @Deprecated(forRemoval = true, since = "6.0.2-beta")
    @SuppressWarnings("removal")
    private ManaStorage getManaStorage(ItemStack stack) {
        // FIXME Remove in next major revision
        // If the wand already has a mana storage capability attached, return it. Otherwise, convert the stack from the
        // old component type to the new one and then return the new one.
        if (stack.has(DataComponentsPM.CAPABILITY_MANA_STORAGE.get())) {
            return stack.get(DataComponentsPM.CAPABILITY_MANA_STORAGE.get());
        } else {
            ManaStorage retVal = ManaStorage.emptyWand(this.getMaxMana(stack));
            retVal.setMana(stack.getOrDefault(DataComponentsPM.STORED_CENTIMANA.get(), SourceList.EMPTY));
            stack.set(DataComponentsPM.CAPABILITY_MANA_STORAGE.get(), retVal);
            stack.remove(DataComponentsPM.STORED_CENTIMANA.get());
            return retVal;
        }
    }

    @Override
    public int getMana(ItemStack stack, Source source) {
        if (this.getMaxMana(stack) == -1) {
            // If the given wand stack has infinite mana, return that
            return -1;
        } else {
            // Otherwise get the current centimana for that source from the stack's data
            int retVal = 0;
            if (stack != null && source != null) {
                retVal = this.getManaStorage(stack).getManaStored(source);
            }
            return retVal;
        }
    }
    
    public MutableComponent getManaText(ItemStack stack, Source source) {
        int mana = this.getMana(stack, source);
        if (mana == -1) {
            // If the given wand stack has infinite mana, show the infinity symbol
            return Component.literal(Character.toString('\u221E'));
        } else {
            // Otherwise show the current whole mana value for that source from the stack's data
            return Component.literal(MANA_FORMATTER.format(mana / 100.0D));
        }
    }

    @Override
    public SourceList getAllMana(ItemStack stack) {
        SourceList retVal = SourceList.EMPTY;
        SourceList stored = this.getManaStorage(stack).getAllManaStored();
        boolean isInfinite = this.getMaxMana(stack) == -1;
        for (Source source : Sources.getAllSorted()) {
            if (isInfinite) {
                // If the stack has infinite mana, set that into the returned source list (not merge; it would keep the default zero)
                retVal = retVal.set(source, -1);
            } else {
                // Otherwise, merge the current centimana into the returned source list
                retVal = retVal.merge(source, stored.getAmount(source));
            }
        }
        return retVal;
    }

    public MutableComponent getMaxManaText(ItemStack stack) {
        int mana = this.getMaxMana(stack);
        if (mana == -1) {
            // If the given wand stack has infinite mana, show the infinity symbol
            return Component.literal(Character.toString('\u221E'));
        } else {
            // Otherwise show the max centimana for that source from the stack's data
            return Component.literal(MANA_FORMATTER.format(mana / 100.0D));
        }
    }

    @Deprecated(forRemoval = true, since = "6.0.2-beta")
    @SuppressWarnings("removal")
    private void updateManaStorageWith(ItemStack stack, Source source, int amount) {
        // FIXME Remove in next major revision
        // If the wand already has a mana storage capability attached, update it. Otherwise, convert the stack from the
        // old component type to the new one and then update the new one.
        if (stack.has(DataComponentsPM.CAPABILITY_MANA_STORAGE.get())) {
            stack.update(DataComponentsPM.CAPABILITY_MANA_STORAGE.get(), ManaStorage.EMPTY, mana -> mana.copyWith(source, amount));
            stack.set(DataComponentsPM.LAST_UPDATED.get(), System.currentTimeMillis());   // FIXME Is there a better way of marking this stack as dirty?
        } else {
            ManaStorage newStorage = ManaStorage.emptyWand(this.getMaxMana(stack));
            newStorage.setMana(stack.getOrDefault(DataComponentsPM.STORED_CENTIMANA.get(), SourceList.EMPTY));
            newStorage.setMana(source, amount);
            stack.set(DataComponentsPM.CAPABILITY_MANA_STORAGE.get(), newStorage);
            stack.remove(DataComponentsPM.STORED_CENTIMANA.get());
        }
    }

    protected void setMana(@NotNull ItemStack stack, @NotNull Source source, int amount) {
        // Save the given amount of centimana for the given source into the stack's data
        this.updateManaStorageWith(stack, source, amount);
    }

    @Override
    public int addMana(ItemStack stack, Source source, int amount) {
        return this.addMana(stack, source, amount, this.getMaxMana(stack));
    }

    protected int addMana(ItemStack stack, Source source, int amount, int max) {
        // If the parameters are invalid or the given wand stack has infinite mana, do nothing
        if (stack == null || source == null || this.getMaxMana(stack) == -1) {
            return 0;
        }

        // Otherwise, increment and set the new centimana total for the source into the wand's data, up to
        // the given centimana threshold, returning any leftover centimana that wouldn't fit
        int toStore = this.getMana(stack, source) + amount;
        int leftover = Math.max(toStore - max, 0);
        this.setMana(stack, source, Math.min(toStore, max));
        return leftover;
    }

    @Override
    public boolean consumeMana(ItemStack stack, Player player, Source source, int amount, HolderLookup.Provider registries) {
        if (stack == null || source == null) {
            return false;
        }
        if (this.containsMana(stack, player, source, amount, registries)) {
            // If the wand stack contains enough mana, process the consumption and return success
            if (this.getMaxMana(stack) != -1) {
                // Only actually consume something if the wand doesn't have infinite mana
                this.setMana(stack, source, this.getMana(stack, source) - (amount == 0 ? 0 : Math.max(1, this.getModifiedCost(stack, player, source, amount, registries))));
            }
            
            if (player != null) {
                int realAmount = amount / 100;
                
                // Record the spent mana statistic change with pre-discount mana
                StatsManager.incrementValue(player, StatsPM.MANA_SPENT_TOTAL, realAmount);
                StatsManager.incrementValue(player, source.getManaSpentStat(), realAmount);
                
                // Update temporary attunement value
                AttunementManager.incrementAttunement(player, source, AttunementType.TEMPORARY, Mth.floor(Math.sqrt(realAmount)));
            }
            
            return true;
        } else {
            // Otherwise return failure
            return false;
        }
    }

    @Override
    public boolean consumeMana(ItemStack stack, Player player, SourceList sources, HolderLookup.Provider registries) {
        if (stack == null || sources == null) {
            return false;
        }
        if (this.containsMana(stack, player, sources, registries)) {
            // If the wand stack contains enough mana, process the consumption and return success
            boolean isInfinite = (this.getMaxMana(stack) == -1);
            SourceList.Builder deltaBuilder = SourceList.builder();
            for (Source source : sources.getSources()) {
                int amount = sources.getAmount(source);
                int realAmount = amount / 100;
                if (!isInfinite) {
                    // Only actually consume something if the wand doesn't have infinite mana
                    this.setMana(stack, source, this.getMana(stack, source) - this.getModifiedCost(stack, player, source, amount, registries));
                }
                
                if (player != null) {
                    // Record the spent mana statistic change with pre-discount mana
                    StatsManager.incrementValue(player, StatsPM.MANA_SPENT_TOTAL, realAmount);
                    StatsManager.incrementValue(player, source.getManaSpentStat(), realAmount);
                }
                
                // Compute the amount of temporary attunement to be added to the player
                deltaBuilder.with(source, Mth.floor(Math.sqrt(realAmount)));
            }
            SourceList attunementDeltas = deltaBuilder.build();
            if (player != null && !attunementDeltas.isEmpty()) {
                // Update attunements in a batch
                AttunementManager.incrementAttunement(player, AttunementType.TEMPORARY, attunementDeltas);
            }
            return true;
        } else {
            // Otherwise return failure
            return false;
        }
    }

    @Override
    public boolean removeManaRaw(ItemStack stack, Source source, int amount) {
        if (stack == null || source == null) {
            return false;
        }
        if (this.containsManaRaw(stack, source, amount)) {
            // If the wand stack contains enough mana, process the consumption and return success
            if (this.getMaxMana(stack) != -1) {
                // Only actually consume something if the wand doesn't have infinite mana
                this.setMana(stack, source, this.getMana(stack, source) - (amount == 0 ? 0 : Math.max(1, (int)amount)));
            }
            
            return true;
        } else {
            // Otherwise return failure
            return false;
        }
    }

    @Override
    public boolean containsMana(ItemStack stack, Player player, Source source, int amount, HolderLookup.Provider registries) {
        // A wand stack with infinite mana always contains the requested amount of mana
        return this.getMaxMana(stack) == -1 || this.getMana(stack, source) >= this.getModifiedCost(stack, player, source, amount, registries);
    }

    @Override
    public boolean containsMana(ItemStack stack, Player player, SourceList sources, HolderLookup.Provider registries) {
        for (Source source : sources.getSources()) {
            if (!this.containsMana(stack, player, source, sources.getAmount(source), registries)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsManaRaw(ItemStack stack, Source source, int amount) {
        // A wand stack with infinite mana always contains the requested amount of mana
        return this.getMaxMana(stack) == -1 || this.getMana(stack, source) >= amount;
    }

    @Override
    public int getTotalCostModifier(ItemStack stack, @Nullable Player player, Source source, HolderLookup.Provider registries) {
        // Start with the base modifier, as determined by wand cap
        int modifier = this.getBaseCostModifier(stack);
        
        // Subtract discounts from wand enchantments
        int efficiencyLevel = EnchantmentHelperPM.getEnchantmentLevel(stack, EnchantmentsPM.MANA_EFFICIENCY, registries);
        if (efficiencyLevel > 0) {
            modifier += (2 * efficiencyLevel);
        }
        
        if (player != null) {
            // Subtract discounts from equipped player gear
            int gearDiscount = 0;
            for (ItemStack gearStack : player.getAllSlots()) {
                if (gearStack.getItem() instanceof IManaDiscountGear discountItem) {
                    gearDiscount += discountItem.getManaDiscount(gearStack, player, source);
                }
            }
            if (gearDiscount > 0) {
                modifier += gearDiscount;
            }
            
            // Subtract discounts from attuned sources
            if (AttunementManager.meetsThreshold(player, source, AttunementThreshold.MINOR)) {
                modifier += 5;
            }
            
            // Subtract discounts from temporary conditions
            if (player.hasEffect(EffectsPM.MANAFRUIT.getHolder())) {
                // 1% at amp 0, 3% at amp 1, 5% at amp 2, etc
                modifier += ((2 * player.getEffect(EffectsPM.MANAFRUIT.getHolder()).getAmplifier()) + 1);
            }
            
            // Add penalties from temporary conditions
            if (player.hasEffect(EffectsPM.MANA_IMPEDANCE.getHolder())) {
                // 5% at amp 0, 10% at amp 1, 15% at amp 2, etc
                modifier -= (5 * (player.getEffect(EffectsPM.MANA_IMPEDANCE.getHolder()).getAmplifier() + 1));
            }
        }
        
        return modifier;
    }

    @Override
    public int getModifiedCost(@Nullable ItemStack stack, @Nullable Player player, @Nullable Source source, int baseCost, HolderLookup.Provider registries) {
        return (int)Math.floor(baseCost / (1 + (this.getTotalCostModifier(stack, player, source, registries) / 100.0D)));
    }

    @Override
    public SourceList getModifiedCost(@Nullable ItemStack stack, @Nullable Player player, SourceList baseCost, HolderLookup.Provider registries) {
        SourceList retVal = SourceList.EMPTY;
        baseCost.getSources().forEach(s -> retVal.set(s, this.getModifiedCost(stack, player, s, baseCost.getAmount(s), registries)));
        return retVal;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        
        Player player = Services.PLATFORM.isClientDist() ? ClientUtils.getCurrentPlayer() : null;
        boolean showDetails = Services.PLATFORM.isClientDist() && ClientUtils.hasShiftDown();
        if (showDetails) {
            // Add detailed mana information
            for (Source source : Sources.getAllSorted()) {
                // Only include a mana source in the listing if it's been discovered
                if (source.isDiscovered(player)) {
                    Component nameComp = source.getNameText();
                    int modifier = this.getTotalCostModifier(stack, player, source, context.registries());
                    Component line = Component.translatable("tooltip.primalmagick.source.mana", nameComp, this.getManaText(stack, source), this.getMaxManaText(stack), modifier);
                    tooltip.add(line);
                }
            }
            
            // Add inscribed spell listing
            List<SpellPackage> spells = this.getSpells(stack);
            int activeIndex = this.getActiveSpellIndex(stack);
            tooltip.add(Component.translatable("tooltip.primalmagick.spells.wand_header", this.getSpellCapacityText(stack)));
            if (spells.isEmpty()) {
                tooltip.add(Component.translatable("tooltip.primalmagick.spells.none"));
            } else {
                for (int index = 0; index < spells.size(); index++) {
                    SpellPackage spell = spells.get(index);
                    if (index == activeIndex) {
                        tooltip.add(Component.translatable("tooltip.primalmagick.spells.name_selected", spell.getDisplayName()));
                        tooltip.addAll(SpellManager.getSpellPackageDetailTooltip(spell, stack, true, context.registries()));
                    } else {
                        tooltip.add(Component.translatable("tooltip.primalmagick.spells.name_unselected", spell.getDisplayName()));
                    }
                }
            }
        } else {
            // Add mana summary
            boolean first = true;
            Component summaryText = Component.literal("");
            for (Source source : Sources.getAllSorted()) {
                // Only include a mana source in the summary if it's been discovered
                if (source.isDiscovered(player)) {
                    Component manaText = this.getManaText(stack, source).withStyle(source.getChatColor());
                    if (first) {
                        summaryText = manaText;
                    } else {
                        summaryText = Component.translatable("tooltip.primalmagick.source.mana_summary_fragment", summaryText, manaText);
                    }
                    first = false;
                }
            }
            tooltip.add(summaryText);
            
            // Add active spell
            SpellPackage activeSpell = this.getActiveSpell(stack);
            Component activeSpellName = (activeSpell == null) ?
                    Component.translatable("tooltip.primalmagick.none") :
                    activeSpell.getDisplayName();
            tooltip.add(Component.translatable("tooltip.primalmagick.spells.short_wand_header", activeSpellName));
            
            // Add more info tooltip
            tooltip.add(Component.translatable("tooltip.primalmagick.more_info").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }
    
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
    
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }
    
    @Override
    public BlockPos getPositionInUse(ItemStack wandStack) {
        // Look up the world coordinates of the wand-interactable tile entity currently in use from data components
        return wandStack.get(DataComponentsPM.WAND_USE_POSITION.get());
    }
    
    @Override
    public void setPositionInUse(ItemStack wandStack, BlockPos pos) {
        // Save the position of the wand-interactable tile entity so it can be looked up later
        wandStack.set(DataComponentsPM.WAND_USE_POSITION.get(), pos.immutable());
    }
    
    @Override
    public void clearPositionInUse(ItemStack wandStack) {
        wandStack.remove(DataComponentsPM.WAND_USE_POSITION.get());
    }
    
    protected static boolean isTargetWandInteractable(Level level, Player player, HitResult hit) {
        if (hit != null && hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            if (level.getBlockState(pos).getBlock() instanceof IInteractWithWand || level.getBlockEntity(pos) instanceof IInteractWithWand) {
                return true;
            }
            return WandTransforms.getAll().stream().anyMatch(t -> t.isValid(level, player, pos));
        } else {
            return false;
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        SpellPackage activeSpell = this.getActiveSpell(stack);
        if (activeSpell != null && !SpellManager.isOnCooldown(playerIn)) {
            // If the wand has an active spell and spells are off the player's cooldown, attempt to cast the spell on right-click
            SpellManager.setCooldown(playerIn, activeSpell.getCooldownTicks());
            if (worldIn.isClientSide) {
                return InteractionResultHolder.success(stack);
            } else {
                HitResult hit = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
                if (isTargetWandInteractable(worldIn, playerIn, hit)) {
                    // If the current mouseover target in range has special interaction with wands, then suppress the spell cast
                    return InteractionResultHolder.pass(stack);
                } else if (this.consumeMana(stack, playerIn, activeSpell.getManaCost(), worldIn.registryAccess())) {
                    // If the wand contains enough mana, consume it and cast the spell
                    activeSpell.cast(worldIn, playerIn, stack);
                    playerIn.swing(handIn);
                    return InteractionResultHolder.success(stack);
                } else {
                    return InteractionResultHolder.fail(stack);
                }
            }
        } else {
            return InteractionResultHolder.pass(stack);
        }
    }
    
    public abstract InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context);

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int count) {
        // If the player continues to hold the interact button, continue the interaction with the last wand-sensitive block/tile interacted with
        if (living instanceof Player player) {
            BlockPos wandPos = this.getPositionInUse(stack);
            if (wandPos != null && level.getBlockEntity(wandPos) instanceof IInteractWithWand wandable) {
                Vec3 playerPos = player.position().add(0.0D, player.getEyeHeight() / 2.0D, 0.0D);
                wandable.onWandUseTick(stack, level, player, playerPos, count);
            } else if (wandPos != null) {
                for (IWandTransform transform : WandTransforms.getAll()) {
                    if (transform.isValid(level, player, wandPos)) {
                        if (level.isClientSide) {
                            // Trigger visual effects during channel
                            FxDispatcher.INSTANCE.spellImpact(wandPos.getX() + 0.5D, wandPos.getY() + 0.5D, wandPos.getZ() + 0.5D, 2, Sources.HALLOWED.getColor());
                        }
                        if (this.getUseDuration(stack, living) - count >= WandTransforms.CHANNEL_DURATION) {
                            if (!level.isClientSide) {
                                // Only execute the transform on the server side
                                transform.execute(level, player, wandPos);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
        
        // Give a hint the first time the player aborts a wand transform early
        BlockPos wandPos = this.getPositionInUse(stack);
        if (wandPos != null && !worldIn.isClientSide && entityLiving instanceof Player player && !WAND_TRANSFORM_HINT_KEY.isKnownBy(player)) {
            for (IWandTransform transform : WandTransforms.getAll()) {
                if (transform.isValid(worldIn, player, wandPos) && this.getUseDuration(stack, entityLiving) - timeLeft < WandTransforms.CHANNEL_DURATION) {
                    ResearchManager.completeResearch(player, WAND_TRANSFORM_HINT_KEY);
                    player.sendSystemMessage(Component.translatable("event.primalmagick.wand_transform_hint").withStyle(ChatFormatting.GREEN));
                    break;
                }
            }
        }

        // Once interaction ceases, clear the last-interacted coordinates
        this.clearPositionInUse(stack);
    }
}
