package io.github.gtbauke.unnamedtechmod.integration;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.BasicPressRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.ManualMaceratorRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIModPlugin implements IModPlugin {
    public static RecipeType<BasicAlloySmelterRecipe> BASIC_ALLOY_SMELTING_TYPE =
            new RecipeType<>(BasicAlloySmeltingRecipeCategory.UID, BasicAlloySmelterRecipe.class);

    public static RecipeType<ManualMaceratorRecipe> MANUAL_MACERATOR_TYPE =
            new RecipeType<>(ManualMaceratorRecipeCategory.UID, ManualMaceratorRecipe.class);

    public static RecipeType<BasicPressRecipe> BASIC_PRESS_TYPE =
            new RecipeType<>(BasicPressRecipeCategory.UID, BasicPressRecipe.class);


    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(UnnamedTechMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BasicAlloySmeltingRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));

        registration.addRecipeCategories(new ManualMaceratorRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));

        registration.addRecipeCategories(new BasicPressRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<BasicAlloySmelterRecipe> basicAlloySmelterRecipeList =
                recipeManager.getAllRecipesFor(BasicAlloySmelterRecipe.Type.INSTANCE);

        List<ManualMaceratorRecipe> manualMaceratorRecipeList =
                recipeManager.getAllRecipesFor(ManualMaceratorRecipe.Type.INSTANCE);

        List<BasicPressRecipe> basicPressRecipeList =
                recipeManager.getAllRecipesFor(BasicPressRecipe.Type.INSTANCE);

        registration.addRecipes(BASIC_ALLOY_SMELTING_TYPE, basicAlloySmelterRecipeList);
        registration.addRecipes(MANUAL_MACERATOR_TYPE, manualMaceratorRecipeList);
        registration.addRecipes(BASIC_PRESS_TYPE, basicPressRecipeList);
    }
}
