package com.verdantartifice.primalmagick.client.gui.grimoire;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.DisciplineButton;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.EntryButton;
import com.verdantartifice.primalmagick.common.research.ResearchDiscipline;
import com.verdantartifice.primalmagick.common.research.ResearchDisciplines;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.awt.Color;

/**
 * GUI page to show recipe metadata in the grimoire.
 * 
 * @author Daedalus4096
 */
public class RecipeMetadataPage extends AbstractPage {
    protected final RecipeHolder<?> recipeHolder;
    protected final RegistryAccess registryAccess;
    protected final boolean firstPage;
    
    public RecipeMetadataPage(RecipeHolder<?> recipe, RegistryAccess registryAccess) {
        this(recipe, registryAccess, false);
    }
    
    public RecipeMetadataPage(RecipeHolder<?> recipe, RegistryAccess registryAccess, boolean firstPage) {
        this.recipeHolder = recipe;
        this.registryAccess = registryAccess;
        this.firstPage = firstPage;
    }
    
    public boolean isFirstPage() {
        return this.firstPage;
    }
    
    @Override
    protected boolean renderTopTitleBar() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int side, int x, int y, int mouseX, int mouseY) {
        // Draw title
        this.renderTitle(guiGraphics, side, x, y, mouseX, mouseY, null);
        y += 53;
        
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        boolean isEntryMissing = ResearchManager.getEntryForRecipe(this.registryAccess, this.recipeHolder.id()).isEmpty();
        Component noneComponent = Component.translatable("tooltip.primalmagick.none");

        // Render the metadata's discipline header
        guiGraphics.drawString(mc.font, Component.translatable("grimoire.primalmagick.recipe_metadata.discipline").withStyle(ChatFormatting.UNDERLINE), x - 3 + (side * 138), y - 6, Color.BLACK.getRGB(), false);
        y += mc.font.lineHeight;
        
        // Render a label if the recipe has no associated research discipline
        if (isEntryMissing) {
            guiGraphics.drawString(mc.font, noneComponent, x - 3 + (side * 138), y - 4, Color.BLACK.getRGB(), false);
        }
        y += 2 * mc.font.lineHeight;
        
        // Render the metadata's entry header
        guiGraphics.drawString(mc.font, Component.translatable("grimoire.primalmagick.recipe_metadata.entry").withStyle(ChatFormatting.UNDERLINE), x - 3 + (side * 138), y - 6, Color.BLACK.getRGB(), false);
        y += mc.font.lineHeight;
        
        // Render a label if the recipe has no associated research entry
        if (isEntryMissing) {
            guiGraphics.drawString(mc.font, noneComponent, x - 3 + (side * 138), y - 4, Color.BLACK.getRGB(), false);
        }
    }

    @Override
    protected Component getTitleText() {
        ItemStack stack = this.recipeHolder.value().getResultItem(this.registryAccess);
        return stack.getItem().getName(stack);
    }

    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (!this.isFirstPage()) {
            y += 24;
        }
        ResearchEntry entry = ResearchManager.getEntryForRecipe(this.registryAccess, this.recipeHolder.id()).orElse(null);
        if (entry != null) {
            y += mc.font.lineHeight + 3;
            final int discY = y;
            entry.disciplineKeyOpt().ifPresent(disciplineKey -> {
                ResearchDiscipline discipline = ResearchDisciplines.getDiscipline(mc.level.registryAccess(), disciplineKey);
                if (discipline != null) {
                    screen.addWidgetToScreen(new DisciplineButton(x + 12 + (side * 140), discY, Component.translatable(discipline.getNameTranslationKey()), screen, discipline, false, false));
                }
            });
            
            y += 3 * mc.font.lineHeight;
            screen.addWidgetToScreen(new EntryButton(x + 12 + (side * 140), y, Component.translatable(entry.getNameTranslationKey()), screen, entry, false));
        }
    }
}
