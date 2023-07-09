package com.verdantartifice.primalmagick.client.gui;

import java.awt.Color;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.gui.widgets.AffinityWidget;
import com.verdantartifice.primalmagick.client.gui.widgets.research_table.KnowledgeTotalWidget;
import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagick.common.containers.AnalysisTableContainer;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import com.verdantartifice.primalmagick.common.network.packets.misc.AnalysisActionPacket;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * GUI screen for analysis table block.
 * 
 * @author Daedalus4096
 */
public class AnalysisTableScreen extends AbstractContainerScreen<AnalysisTableContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/analysis_table.png");
    
    protected Level world;

    public AnalysisTableScreen(AnalysisTableContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.world = inv.player.level();
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.initWidgets();
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component text = null;
        ItemStack lastScannedStack = this.menu.getLastScannedStack();
        
        // Generate text in the case that no item has been analyzed, or the item has no affinities
        if (lastScannedStack == null || lastScannedStack.isEmpty()) {
            text = Component.translatable("primalmagick.analysis.no_item");
        } else {
            SourceList sources = AffinityManager.getInstance().getAffinityValues(lastScannedStack, this.world);
            if (sources == null || sources.isEmpty()) {
                text = Component.translatable("primalmagick.analysis.no_affinities");
            }
        }
        
        // Render any generated text
        if (text != null) {
            int width = this.font.width(text.getString());
            int x = 1 + (this.getXSize() - width) / 2;
            int y = 10 + (16 - this.font.lineHeight) / 2;
            guiGraphics.drawString(this.font, text, x, y, Color.BLACK.getRGB(), false);
        }
    }

    protected void initWidgets() {
        this.clearWidgets();
        
        this.addRenderableWidget(new AnalyzeButton(this.menu, this.leftPos, this.topPos));
        
        // Render observation tracker widget
        this.addRenderableWidget(new KnowledgeTotalWidget(this.leftPos + 8, this.topPos + 60, IPlayerKnowledge.KnowledgeType.OBSERVATION));
        
        // Show affinity widgets, if the last scanned stack has affinities
        ItemStack lastScannedStack = this.menu.getLastScannedStack();
        if (lastScannedStack != null && !lastScannedStack.isEmpty()) {
            SourceList sources = AffinityManager.getInstance().getAffinityValues(lastScannedStack, this.world);
            if (sources != null && !sources.isEmpty()) {
                int widgetSetWidth = sources.getSourcesSorted().size() * 18;
                int x = this.leftPos + 1 + (this.getXSize() - widgetSetWidth) / 2;
                int y = this.topPos + 10;
                for (Source source : sources.getSourcesSorted()) {
                    this.addRenderableWidget(new AffinityWidget(source, sources.getAmount(source), x, y));
                    x += 18;
                }
            }
        }
    }
    
    protected static class AnalyzeButton extends ImageButton {
        private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/analysis_button.png");
        protected static final Component ANALYZE_BUTTON_TOOLTIP_1 = Component.translatable("tooltip.primalmagick.analyze_button.1");
        protected static final Component ANALYZE_BUTTON_TOOLTIP_2 = Component.translatable("tooltip.primalmagick.analyze_button.2").withStyle(ChatFormatting.RED);

        public AnalyzeButton(AnalysisTableContainer menu, int leftPos, int topPos) {
            super(leftPos + 78, topPos + 34, 20, 18, 0, 0, 19, BUTTON_TEXTURE, 256, 256, button -> {
                PacketHandler.sendToServer(new AnalysisActionPacket(menu.containerId));
            });
            this.setTooltip(Tooltip.create(CommonComponents.joinLines(ANALYZE_BUTTON_TOOLTIP_1, ANALYZE_BUTTON_TOOLTIP_2)));
        }
    }
}
