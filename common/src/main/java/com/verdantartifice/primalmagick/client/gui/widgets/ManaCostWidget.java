package com.verdantartifice.primalmagick.client.gui.widgets;

import com.google.common.collect.ImmutableList;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.wands.IWand;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Display widget for showing how much of a given source something has as a mana cost.
 * 
 * @author Daedalus4096
 */
public class ManaCostWidget extends AbstractSourceWidget {
    protected static final DecimalFormat MANA_FORMATTER = new DecimalFormat("#######.##");
    
    protected final Supplier<ItemStack> wandStackSupplier;
    protected final Player player;
    
    public ManaCostWidget(Source source, int amount, int xIn, int yIn, Supplier<ItemStack> wandStackSupplier, Player player) {
        super(source, amount, xIn, yIn);
        this.wandStackSupplier = wandStackSupplier;
        this.player = player;
    }
    
    protected double getCostModifier() {
        Minecraft mc = Minecraft.getInstance();
        ItemStack wandStack = this.wandStackSupplier.get();
        return wandStack.getItem() instanceof IWand wand ? wand.getTotalCostModifier(wandStack, this.player, this.source, mc.level.registryAccess()) : 0D;
    }
    
    protected double getModifiedAmount() {
        Minecraft mc = Minecraft.getInstance();
        ItemStack wandStack = this.wandStackSupplier.get();
        return wandStack.getItem() instanceof IWand wand ? wand.getModifiedCost(wandStack, this.player, this.source, this.getAmount(), mc.level.registryAccess()) : this.getAmount();
    }

    @Override
    protected String getAmountString() {
        return formatInWholeMana(this.getModifiedAmount());
    }

    protected static String formatInWholeMana(double amount) {
        return MANA_FORMATTER.format(amount / 100.0D);
    }

    @Override
    protected int getAmountStringColor() {
        // FIXME Convert to effectiveness model
        double costModifier = this.getCostModifier();
        if (costModifier > 1) {
            return Color.RED.getRGB();
        } else if (costModifier < 1) {
            return Color.GREEN.getRGB();
        } else {
            return super.getAmountStringColor();
        }
    }

    @Override
    protected List<Component> getTooltipLines() {
        int baseCost = this.getAmount();
        double modifiedCost = this.getModifiedAmount();
        double costDelta = Math.abs(modifiedCost - (double)baseCost);
        List<Component> retVal;
        
        if (modifiedCost > baseCost) {
            retVal = ImmutableList.<Component>of(
                    Component.translatable("label.primalmagick.crafting.mana.base", formatInWholeMana(baseCost)),
                    Component.translatable("label.primalmagick.crafting.mana.penalty", Component.literal(formatInWholeMana(costDelta)).withStyle(ChatFormatting.RED)),
                    Component.translatable("label.primalmagick.crafting.mana.modified", formatInWholeMana(modifiedCost), this.getSourceText()));
        } else if (modifiedCost < baseCost) {
            retVal = ImmutableList.<Component>of(
                    Component.translatable("label.primalmagick.crafting.mana.base", formatInWholeMana(baseCost)),
                    Component.translatable("label.primalmagick.crafting.mana.bonus", Component.literal(formatInWholeMana(costDelta)).withStyle(ChatFormatting.GREEN)),
                    Component.translatable("label.primalmagick.crafting.mana.modified", formatInWholeMana(modifiedCost), this.getSourceText()));
        } else {
            retVal = Collections.singletonList(Component.translatable("label.primalmagick.crafting.mana", formatInWholeMana(baseCost), this.getSourceText()));
        }
        
        return retVal;
    }
}
