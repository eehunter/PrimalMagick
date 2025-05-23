package com.verdantartifice.primalmagick.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verdantartifice.primalmagick.common.research.requirements.AbstractRequirement;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Definition for a concocting recipe.  Similar to a shapeless arcane recipe, but used by the concocter
 * instead of an arcane workbench.
 * 
 * @author Daedalus4096
 */
public class ConcoctingRecipe extends AbstractStackCraftingRecipe<CraftingInput> implements IShapelessRecipePM<CraftingInput>, IConcoctingRecipe {
    protected final Optional<AbstractRequirement<?>> requirement;
    protected final SourceList manaCosts;
    protected final NonNullList<Ingredient> recipeItems;
    protected final boolean isSimple;

    public ConcoctingRecipe(String group, ItemStack output, NonNullList<Ingredient> items, Optional<AbstractRequirement<?>> requirement, SourceList manaCosts) {
        super(group, output);
        this.requirement = requirement;
        this.manaCosts = manaCosts;
        this.recipeItems = items;
        this.isSimple = items.stream().allMatch(Services.INGREDIENTS::isSimple);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializersPM.CONCOCTING.get();
    }

    @Override
    public @NotNull SourceList getManaCosts() {
        return this.manaCosts;
    }

    @Override
    public Optional<AbstractRequirement<?>> getRequirement() {
        return this.requirement;
    }

    @Override
    public boolean isSimple() {
        return this.isSimple;
    }

    public static class Serializer implements RecipeSerializer<ConcoctingRecipe> {
        @Override
        public MapCodec<ConcoctingRecipe> codec() { 
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(sar -> sar.group),
                    ItemStack.CODEC.fieldOf("result").forGetter(sar -> sar.output),
                    Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
                        Ingredient[] ingArray = ingredients.stream().filter(Predicate.not(Ingredient::isEmpty)).toArray(Ingredient[]::new);
                        if (ingArray.length == 0) {
                            return DataResult.error(() -> "No ingredients for concocting recipe");
                        } else if (ingArray.length > ShapedArcaneRecipe.MAX_WIDTH * ShapedArcaneRecipe.MAX_HEIGHT) {
                            return DataResult.error(() -> "Too many ingredients for concocting recipe");
                        } else {
                            return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingArray));
                        }
                    }, DataResult::success).forGetter(sar -> sar.recipeItems),
                    AbstractRequirement.dispatchCodec().optionalFieldOf("requirement").forGetter(sar -> sar.requirement),
                    SourceList.CODEC.optionalFieldOf("mana", SourceList.EMPTY).forGetter(sar -> sar.manaCosts)
                ).apply(instance, ConcoctingRecipe::new));
        }
        
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ConcoctingRecipe> streamCodec() {
            return StreamCodec.of(ConcoctingRecipe.Serializer::toNetwork, ConcoctingRecipe.Serializer::fromNetwork);
        }
        
        private static ConcoctingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Optional<AbstractRequirement<?>> requirement = buffer.readBoolean() ? Optional.ofNullable(AbstractRequirement.dispatchStreamCodec().decode(buffer)) : Optional.empty();
            
            SourceList manaCosts = SourceList.fromNetwork(buffer);
            
            int count = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);
            ingredients.replaceAll(ing -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            return new ConcoctingRecipe(group, result, ingredients, requirement, manaCosts);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ConcoctingRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.requirement.ifPresentOrElse(req -> {
                buffer.writeBoolean(true);
                AbstractRequirement.dispatchStreamCodec().encode(buffer, req);
            }, () -> {
                buffer.writeBoolean(false);
            });
            SourceList.toNetwork(buffer, recipe.manaCosts);
            
            buffer.writeVarInt(recipe.recipeItems.size());
            for (Ingredient ingredient : recipe.recipeItems) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
