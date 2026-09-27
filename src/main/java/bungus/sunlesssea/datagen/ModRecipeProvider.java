package bungus.sunlesssea.datagen;

import bungus.sunlesssea.block.ModBlocks;
import bungus.sunlesssea.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SILT_BLOCK.get())
                .pattern("XX ")
                .pattern("XX ")
                .pattern("   ")
                .define('X', ModItems.SILT.get())
                .unlockedBy(getHasName(ModItems.SILT.get()),has(ModItems.SILT.get()))
                .save(pWriter);
    }
}
