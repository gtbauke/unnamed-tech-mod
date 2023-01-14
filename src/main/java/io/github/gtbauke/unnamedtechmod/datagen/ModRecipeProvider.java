package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.utils.ModTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(ModBlocks.RAW_TIN_BLOCK.get())
                .define('R', ModItems.RAW_TIN_ORE.get())
                .pattern("RRR")
                .pattern("RRR")
                .pattern("RRR")
                .unlockedBy("has_raw_tin", inventoryTrigger(
                        ItemPredicate.Builder.item().of(ModTags.Items.RAW_MATERIALS_TIN).build())
                ).save(pFinishedRecipeConsumer);

        ShapelessRecipeBuilder.shapeless(ModItems.RAW_TIN_ORE.get(), 9)
                .requires(ModBlocks.RAW_TIN_BLOCK.get())
                .unlockedBy("has_raw_tin_block", inventoryTrigger(
                        ItemPredicate.Builder.item().of(ModTags.Items.STORAGE_BLOCKS_RAW_TIN).build()
                )).save(pFinishedRecipeConsumer);
    }
}
