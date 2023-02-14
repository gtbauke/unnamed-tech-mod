package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.datagen.custom.BasicAlloySmeltingRecipeBuilder;
import io.github.gtbauke.unnamedtechmod.datagen.custom.BasicPressRecipeBuilder;
import io.github.gtbauke.unnamedtechmod.datagen.custom.ManualMaceratorRecipeBuilder;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.recipe.ManualMaceratorRecipe;
import io.github.gtbauke.unnamedtechmod.utils.ModTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
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

        ShapelessRecipeBuilder.shapeless(ModItems.LIGHT_CLAY_BALL.get(), 3)
                .requires(Items.CLAY_BALL, 3)
                .requires(Blocks.SAND)
                .unlockedBy("has_sand_and_clay", inventoryTrigger(
                        ItemPredicate.Builder.item().of(Items.CLAY_BALL, Blocks.SAND).build()
                )).save(pFinishedRecipeConsumer);

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ModItems.LIGHT_CLAY_BALL.get()),
                ModItems.LIGHT_BRICK.get(), 0.6f, 200
        ).unlockedBy("has_item", has(ModItems.LIGHT_CLAY_BALL.get())).save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(ModBlocks.LIGHT_BRICKS.get())
                .define('L', ModItems.LIGHT_BRICK.get())
                .pattern("LL")
                .pattern("LL")
                .unlockedBy("has_item", has(ModItems.LIGHT_BRICK.get()))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(ModBlocks.BASIC_ALLOY_SMELTER.get())
                .define('L', ModBlocks.LIGHT_BRICKS.get())
                .define('S', Blocks.STONE)
                .define('I', Items.IRON_INGOT)
                .pattern("SIS")
                .pattern("LIL")
                .pattern("LLL")
                .unlockedBy("has_item", has(ModBlocks.LIGHT_BRICKS.get()))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(ModBlocks.MANUAL_MACERATOR.get())
                .define('L', ModBlocks.LIGHT_BRICKS.get())
                .define('#', Blocks.STONE)
                .define('S', Items.STICK)
                .define('I', Items.IRON_INGOT)
                .pattern("#S#")
                .pattern("ISI")
                .pattern("LLL")
                .unlockedBy("has_item", has(ModBlocks.LIGHT_BRICKS.get()))
                .save(pFinishedRecipeConsumer);

        oreSmelting(pFinishedRecipeConsumer, List.of(
                ModItems.RAW_TIN_ORE.get(),
                ModItems.TIN_DUST.get()
        ), ModItems.TIN_INGOT.get(), 0.7f, 200, "tin_ingot");

        oreBlasting(pFinishedRecipeConsumer, List.of(
                ModItems.RAW_TIN_ORE.get(),
                ModItems.TIN_DUST.get()
        ), ModItems.TIN_INGOT.get(), 0.7f, 200, "tin_ingot");

        oreSmelting(pFinishedRecipeConsumer, List.of(
                ModItems.COPPER_DUST.get()
        ), Items.COPPER_INGOT, 0.7f, 200, "copper_ingot");

        oreBlasting(pFinishedRecipeConsumer, List.of(
                ModItems.COPPER_DUST.get()
        ), Items.COPPER_INGOT, 0.7f, 200, "copper_ingot");

        oreSmelting(pFinishedRecipeConsumer, List.of(
                ModItems.IRON_DUST.get()
        ), Items.IRON_INGOT, 0.7f, 200, "iron_ingot");

        oreBlasting(pFinishedRecipeConsumer, List.of(
                ModItems.IRON_DUST.get()
        ), Items.IRON_INGOT, 0.7f, 200, "iron_ingot");

        oreSmelting(pFinishedRecipeConsumer, List.of(
                ModItems.GOLD_DUST.get()
        ), Items.GOLD_INGOT, 0.7f, 200, "gold_ingot");

        oreBlasting(pFinishedRecipeConsumer, List.of(
                ModItems.GOLD_DUST.get()
        ), Items.GOLD_INGOT, 0.7f, 200, "gold_ingot");

        oreSmelting(pFinishedRecipeConsumer, List.of(
                ModItems.BRONZE_DUST.get()
        ), ModItems.BRONZE_INGOT.get(), 0.7f, 200, "bronze_ingot");

        oreBlasting(pFinishedRecipeConsumer, List.of(
                ModItems.BRONZE_DUST.get()
        ), ModItems.BRONZE_INGOT.get(), 0.7f, 200, "bronze_ingot");

        ShapedRecipeBuilder.shaped(ModItems.EMPTY_FLUID_CELL.get(), 3)
                .define('T', ModItems.TIN_PLATE.get())
                .define('G', Items.GLASS_PANE.asItem())
                .define('I', Items.IRON_INGOT)
                .pattern("ITI")
                .pattern("TGT")
                .pattern("ITI")
                .unlockedBy("has_item", has(ModItems.TIN_INGOT.get()))
                .save(pFinishedRecipeConsumer);

        BasicAlloySmeltingRecipeBuilder.alloySmelting(ModItems.REDSTONE_COPPER_ALLOY.get())
                .requires(Items.COPPER_INGOT)
                .requires(Items.REDSTONE, 3)
                .alloyCompoundAmount(1)
                .cookingTime(200)
                .experience(0.7f)
                .unlockedBy("has_item", has(ModBlocks.BASIC_ALLOY_SMELTER.get()))
                .save(pFinishedRecipeConsumer);

        BasicAlloySmeltingRecipeBuilder.alloySmelting(ModItems.BRONZE_INGOT.get(), 2)
                .requires(ModItems.COPPER_DUST.get())
                .requires(ModItems.TIN_DUST.get(), 3)
                .alloyCompoundAmount(1)
                .experience(0.7f)
                .cookingTime(200)
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(
                        ModItems.TIN_INGOT.get(), Items.COPPER_INGOT
                ).build()))
                .save(pFinishedRecipeConsumer);

        ManualMaceratorRecipeBuilder.crushing(ModItems.IRON_DUST.get(), 2)
                .requires(Items.RAW_IRON)
                .crushingTime(200)
                .experience(0.7f)
                .unlockedBy("has_item", has(ModBlocks.MANUAL_MACERATOR.get()))
                .save(pFinishedRecipeConsumer);

        ManualMaceratorRecipeBuilder.crushing(ModItems.IRON_DUST.get(), 1)
                .requires(Items.IRON_INGOT)
                .crushingTime(200)
                .experience(0.7f)
                .unlockedBy("has_item", has(ModBlocks.MANUAL_MACERATOR.get()))
                .save(pFinishedRecipeConsumer);

        BasicPressRecipeBuilder.pressing(ModItems.IRON_PLATE.get(), 1)
                .requires(Items.IRON_INGOT)
                .pressingTime(200)
                .experience(0.8f)
                .minimumTemperature(700)
                .unlockedBy("has_item", has(ModBlocks.BASIC_PRESS.get()))
                .save(pFinishedRecipeConsumer);

        BasicPressRecipeBuilder.pressing(ModItems.COPPER_PLATE.get(), 1)
                .requires(Items.COPPER_INGOT)
                .pressingTime(200)
                .experience(0.8f)
                .minimumTemperature(470)
                .unlockedBy("has_item", has(ModBlocks.BASIC_PRESS.get()))
                .save(pFinishedRecipeConsumer);
    }
}
