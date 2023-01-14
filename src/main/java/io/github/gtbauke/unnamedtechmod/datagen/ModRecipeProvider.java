package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.utils.ModTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
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

        oreSmelting(pFinishedRecipeConsumer, List.of(
                ModItems.RAW_TIN_ORE.get(),
                ModItems.TIN_DUST.get()
        ), ModItems.TIN_INGOT.get(), 0.7f, 200, "tin_ingot");

        oreBlasting(pFinishedRecipeConsumer, List.of(
                ModItems.RAW_TIN_ORE.get(),
                ModItems.TIN_DUST.get()
        ), ModItems.TIN_INGOT.get(), 0.7f, 200, "tin_ingot");
    }
}
