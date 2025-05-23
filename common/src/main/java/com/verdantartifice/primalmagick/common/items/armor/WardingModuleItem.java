package com.verdantartifice.primalmagick.common.items.armor;

import com.google.common.collect.ImmutableList;
import com.verdantartifice.primalmagick.Constants;
import com.verdantartifice.primalmagick.common.capabilities.ManaStorage;
import com.verdantartifice.primalmagick.common.components.DataComponentsPM;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.misc.DeviceTier;
import com.verdantartifice.primalmagick.common.misc.ITieredDevice;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.tags.ItemTagsPM;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

/**
 * Item definition for a warding module, a magitech device that can be attached to hard armor
 * to provide the wearer with temporary hit points at the cost of mana.
 * 
 * @author Daedalus4096
 */
public class WardingModuleItem extends Item implements ITieredDevice {
    public static final String TAG_NAME = String.join(":", Constants.MOD_ID, "WardingModule");
    
    /**
     * The total amount of centimana that can be infused into a warded armor stack at once.
     */
    public static final int MANA_CAPACITY = 10000;
    
    /**
     * The amount of centimana that can be infused into the warded armor per tick.
     */
    public static final int CHARGE_RATE = 10000;
    
    /**
     * The amount of centimana needed to regenerate one point of ward.
     */
    public static final int REGEN_COST = 500;
    
    // FIXME Use the WARDABLE_ARMOR tag as the source of truth if/when the RegisterItemDecorationsEvent is made to fire *after* tag data loads
    @SuppressWarnings("unchecked")
    protected static final List<Supplier<? extends Item>> APPLICABLE_ITEMS = ImmutableList.<Supplier<? extends Item>>builder().add(
            ItemsPM.PRIMALITE_HEAD, ItemsPM.PRIMALITE_CHEST, ItemsPM.PRIMALITE_LEGS, ItemsPM.PRIMALITE_FEET,
            ItemsPM.HEXIUM_HEAD, ItemsPM.HEXIUM_CHEST, ItemsPM.HEXIUM_LEGS, ItemsPM.HEXIUM_FEET,
            ItemsPM.HALLOWSTEEL_HEAD, ItemsPM.HALLOWSTEEL_CHEST, ItemsPM.HALLOWSTEEL_LEGS, ItemsPM.HALLOWSTEEL_FEET).build();
    
    protected final DeviceTier tier;
    
    public WardingModuleItem(DeviceTier tier, Item.Properties properties) {
        super(properties);
        this.tier = tier;
    }
    
    @Override
    public DeviceTier getDeviceTier() {
        return this.tier;
    }
    
    public boolean hasWard() {
        return this.getWardLevel() > 0;
    }
    
    public int getWardLevel() {
        return switch (this.getDeviceTier()) {
            case ENCHANTED -> 1;
            case FORBIDDEN -> 2;
            case HEAVENLY -> 3;
            default -> 0;
        };
    }

    public ItemStack applyWard(ItemStack stack) {
        if (this.hasWard() && canApplyWard(stack)) {
            stack.set(DataComponentsPM.WARD_LEVEL.get(), this.getWardLevel());
            if (!stack.has(DataComponentsPM.CAPABILITY_MANA_STORAGE.get())) {
                // TODO Properly handle case where item already has a mana storage component; shouldn't be any items that do that yet
                stack.set(DataComponentsPM.CAPABILITY_MANA_STORAGE.get(), new ManaStorage(MANA_CAPACITY, CHARGE_RATE, REGEN_COST, Sources.EARTH));
            }
        }
        return stack;
    }

    public static boolean canApplyWard(ItemStack stack) {
        // FIXME Determine why a simple tag test fails during Neoforge tests
        return getApplicableItems().stream().anyMatch(itemSupplier -> stack.is(itemSupplier.get()));
    }
    
    public static boolean hasWardAttached(ItemStack stack) {
        return stack.is(ItemTagsPM.WARDABLE_ARMOR) && stack.getOrDefault(DataComponentsPM.WARD_LEVEL.get(), 0) > 0;
    }
    
    public static int getAttachedWardLevel(ItemStack stack) {
        return stack.getOrDefault(DataComponentsPM.WARD_LEVEL.get(), 0);
    }
    
    public static List<Supplier<? extends Item>> getApplicableItems() {
        return APPLICABLE_ITEMS;
    }
}
