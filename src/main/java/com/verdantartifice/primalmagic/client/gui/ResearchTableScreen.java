package com.verdantartifice.primalmagic.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;
import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagic.common.capabilities.PrimalMagicCapabilities;
import com.verdantartifice.primalmagic.common.containers.ResearchTableContainer;
import com.verdantartifice.primalmagic.common.theorycrafting.AbstractProject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI screen for the research table block.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ResearchTableScreen extends ContainerScreen<ResearchTableContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagic.MODID, "textures/gui/research_table.png");
    private static final ResourceLocation OVERLAY = new ResourceLocation(PrimalMagic.MODID, "textures/gui/research_table_overlay.png");
    
    protected long lastCheck = 0L;
    protected boolean progressing = false;
    protected IPlayerKnowledge knowledge;
    protected AbstractProject project = null;
    protected Button completeProjectButton = null;

    public ResearchTableScreen(ResearchTableContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 230;
        this.ySize = 222;
    }
    
    @Override
    protected void init() {
        super.init();
        this.knowledge = PrimalMagicCapabilities.getKnowledge(this.getMinecraft().player);
        if (this.knowledge == null) {
            throw new IllegalStateException("No knowledge provider found for player");
        }
        this.project = this.knowledge.getActiveResearchProject();
        this.initButtons();
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        // Determine if we need to update the GUI based on how long it's been since the last refresh
        long millis = System.currentTimeMillis();
        if (millis > this.lastCheck) {
            // Update more frequently if waiting for the server to process a progression
            this.lastCheck = this.progressing ? (millis + 250L) : (millis + 2000L);
            this.project = this.knowledge.getActiveResearchProject();
            this.initButtons();
        }

        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        
        if (this.isProjectReady()) {
            // TODO render title text
            // TODO render description text
        } else if (this.container.getWritingImplementStack().isEmpty() || this.container.getPaperStack().isEmpty()) {
            // Render missing writing materials text
            ITextComponent text = new TranslationTextComponent("primalmagic.research_table.missing_writing_supplies");
            int width = mc.fontRenderer.getStringWidth(text.getFormattedText());
            mc.fontRenderer.drawString(text.getFormattedText(), 34 + ((162 - width) / 2), 7 + ((128 - mc.fontRenderer.FONT_HEIGHT) / 2), Color.BLACK.getRGB());
        } else {
            // Render ready to start text
            ITextComponent text = new TranslationTextComponent("primalmagic.research_table.ready");
            int width = mc.fontRenderer.getStringWidth(text.getFormattedText());
            mc.fontRenderer.drawString(text.getFormattedText(), 34 + ((162 - width) / 2), 7 + ((128 - mc.fontRenderer.FONT_HEIGHT) / 2), Color.BLACK.getRGB());
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Render the GUI background
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        // If a research project is ready to go, render the page overlay
        if (this.isProjectReady()) {
            this.minecraft.getTextureManager().bindTexture(OVERLAY);
            this.blit(this.guiLeft + 34, this.guiTop + 7, 0, 0, 162, 128);
        }
    }
    
    protected boolean isProjectReady() {
        return this.project != null && !this.container.getWritingImplementStack().isEmpty() && !this.container.getPaperStack().isEmpty();
    }
    
    public void setProgressing() {
        this.progressing = true;
        this.lastCheck = 0L;
    }
    
    protected void initButtons() {
        this.buttons.clear();
        this.children.clear();
        this.completeProjectButton = null;
        
        if (this.project == null && !this.container.getWritingImplementStack().isEmpty() && !this.container.getPaperStack().isEmpty()) {
            if (this.progressing) {
                // Render starting widget
                ITextComponent text = new TranslationTextComponent("primalmagic.research_table.starting");
                this.addButton(new WaitingWidget(this.guiLeft + 38, this.guiTop + 111, text.getFormattedText()));
            } else {
                // Render start project button
                ITextComponent text = new TranslationTextComponent("primalmagic.research_table.start");
                this.addButton(new StartProjectButton(this.guiLeft + 38, this.guiTop + 111, text.getFormattedText(), this));
            }
        } else if (this.isProjectReady()) {
            if (this.progressing) {
                // Render completing widget
                ITextComponent text = new TranslationTextComponent("primalmagic.research_table.completing");
                this.addButton(new WaitingWidget(this.guiLeft + 38, this.guiTop + 111, text.getFormattedText()));
            } else {
                // TODO render complete project button
            }
        }
    }
    
    /**
     * GUI widget for a disabled text button to use while waiting for the server to update.
     * 
     * @author Daedalus4096
     */
    protected static class WaitingWidget extends Widget {
        public WaitingWidget(int xIn, int yIn, String msg) {
            super(xIn, yIn, 154, 20, msg);
            this.active = false;
        }
    }
    
    /**
     * GUI button to start a new research project in the research table.
     * 
     * @author Daedalus4096
     */
    protected static class StartProjectButton extends Button {
        protected ResearchTableScreen screen;
        
        public StartProjectButton(int xIn, int yIn, String text, ResearchTableScreen screen) {
            super(xIn, yIn, 154, 20, text, new Handler());
            this.screen = screen;
        }
        
        public ResearchTableScreen getScreen() {
            return this.screen;
        }
        
        private static class Handler implements IPressable {
            @Override
            public void onPress(Button button) {
                if (button instanceof StartProjectButton) {
                    // Send a packet to the server and tell the screen to update more frequently until resolved
                    StartProjectButton spb = (StartProjectButton)button;
                    // TODO send packet
                    spb.getScreen().setProgressing();
                }
            }
        }
    }
}
