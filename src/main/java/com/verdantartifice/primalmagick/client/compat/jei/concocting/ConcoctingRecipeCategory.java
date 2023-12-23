package com.verdantartifice.primalmagick.client.compat.jei.concocting;

import java.util.List;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.compat.jei.JeiHelper;
import com.verdantartifice.primalmagick.client.compat.jei.JeiRecipeTypesPM;
import com.verdantartifice.primalmagick.client.compat.jei.RecipeCategoryPM;
import com.verdantartifice.primalmagick.client.util.RecipeUtils;
import com.verdantartifice.primalmagick.common.crafting.IConcoctingRecipe;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.sources.SourceList;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ConcoctingRecipeCategory extends RecipeCategoryPM<RecipeHolder<IConcoctingRecipe>> {
    public static final ResourceLocation UID = PrimalMagick.resource("concocter");
    private static final ResourceLocation BACKGROUND_TEXTURE = PrimalMagick.resource("textures/gui/jei/arcane_workbench.png");
    private static final ResourceLocation RESEARCH_TEXTURE = PrimalMagick.resource("textures/item/grimoire.png");
    private static final int MANA_COST_X_OFFSET = 64;
    private static final int MANA_COST_Y_OFFSET = 1;
    private static final int RESEARCH_X_OFFSET = 64;
    private static final int RESEARCH_Y_OFFSET = 36;

    private final ICraftingGridHelper craftingGridHelper;
    private final IDrawableStatic manaCostIcon;
    private final IDrawableStatic researchIcon;

    public ConcoctingRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, UID, "block.primalmagick.concocter");
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();
        this.manaCostIcon = guiHelper.createDrawable(BACKGROUND_TEXTURE, 116, 0, 16, 16);
        this.researchIcon = guiHelper.drawableBuilder(RESEARCH_TEXTURE, 0, 0, 32, 32).setTextureSize(32, 32).build();
        this.setBackground(guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, 116, 54));
        this.setIcon(new ItemStack(ItemsPM.CONCOCTER.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<IConcoctingRecipe> recipe, IFocusGroup focuses) {
        // Initialize recipe output
        this.craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(RecipeUtils.getResultItem(recipe.value())));
        
        // Initialize recipe inputs
        List<List<ItemStack>> inputs = recipe.value().getIngredients().stream().map(ingredient -> List.of(ingredient.getItems())).toList();
        this.craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputs, 0, 0);
    }

    @Override
    public void draw(RecipeHolder<IConcoctingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IConcoctingRecipe recipe = recipeHolder.value();
        if (recipe.getManaCosts() != null && !recipe.getManaCosts().isEmpty()) {
            this.manaCostIcon.draw(guiGraphics, MANA_COST_X_OFFSET, MANA_COST_Y_OFFSET);
        }
        if (recipe.getRequiredResearch() != null && !recipe.getRequiredResearch().getKeys().isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
            this.researchIcon.draw(guiGraphics, RESEARCH_X_OFFSET * 2, RESEARCH_Y_OFFSET * 2);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public List<Component> getTooltipStrings(RecipeHolder<IConcoctingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        IConcoctingRecipe recipe = recipeHolder.value();
        SourceList manaCosts = recipe.getManaCosts();
        CompoundResearchKey compoundResearch = recipe.getRequiredResearch();
        if ( manaCosts != null && !manaCosts.isEmpty() && 
             mouseX >= MANA_COST_X_OFFSET && mouseX < MANA_COST_X_OFFSET + this.manaCostIcon.getWidth() &&
             mouseY >= MANA_COST_Y_OFFSET && mouseY < MANA_COST_Y_OFFSET + this.manaCostIcon.getHeight() ) {
            return JeiHelper.getManaCostTooltipStrings(manaCosts);
        } else if ( compoundResearch != null && !compoundResearch.getKeys().isEmpty() &&
                    mouseX >= RESEARCH_X_OFFSET && mouseX < RESEARCH_X_OFFSET + this.researchIcon.getWidth() &&
                    mouseY >= RESEARCH_Y_OFFSET && mouseY < RESEARCH_Y_OFFSET + this.researchIcon.getHeight() ) {
            return JeiHelper.getRequiredResearchTooltipStrings(compoundResearch);
        } else {
            return super.getTooltipStrings(recipeHolder, recipeSlotsView, mouseX, mouseY);
        }
    }

    @Override
    public RecipeType<RecipeHolder<IConcoctingRecipe>> getRecipeType() {
        return JeiRecipeTypesPM.CONCOCTING;
    }
}
