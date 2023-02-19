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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
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
        nineBlockStorageRecipes(pFinishedRecipeConsumer, ModItems.RAW_TIN_ORE.get(), ModBlocks.RAW_TIN_BLOCK.get());
        nineBlockStorageRecipes(pFinishedRecipeConsumer, ModItems.RAW_LEAD_ORE.get(), ModBlocks.RAW_LEAD_BLOCK.get());

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

        ore(pFinishedRecipeConsumer, List.of(
                ModItems.RAW_TIN_ORE.get(),
                ModItems.TIN_DUST.get()
        ), ModItems.TIN_INGOT.get(), 0.7f, 200, "tin_ingot");

        ore(pFinishedRecipeConsumer, List.of(
                ModItems.RAW_LEAD_ORE.get(),
                ModItems.LEAD_DUST.get()
        ), ModItems.LEAD_INGOT.get(), 0.7f, 200, "lead_ingot");

        ore(pFinishedRecipeConsumer, List.of(
                ModItems.COPPER_DUST.get()
        ), Items.COPPER_INGOT, 0.7f, 200, "copper_ingot");

        ore(pFinishedRecipeConsumer, List.of(
                ModItems.IRON_DUST.get()
        ), Items.IRON_INGOT, 0.7f, 200, "iron_ingot");

        ore(pFinishedRecipeConsumer, List.of(
                ModItems.GOLD_DUST.get()
        ), Items.GOLD_INGOT, 0.7f, 200, "gold_ingot");

        ore(pFinishedRecipeConsumer, List.of(
                ModItems.BRONZE_DUST.get()
        ), ModItems.BRONZE_INGOT.get(), 0.7f, 200, "bronze_ingot");

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

    private void ore(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> inputs, Item output, float experience, int time, String group) {
        oreSmelting(pFinishedRecipeConsumer, inputs, output, experience, time, group);
        oreBlasting(pFinishedRecipeConsumer, inputs, output, experience, time, group);
    }
}
