package io.github.gtbauke.unnamedtechmod.recipe;

import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class AbstractPressRecipe extends AbstractMachineRecipe implements Recipe<SimpleContainer> {
    protected final RecipeIngredient ingredient;
    protected final int minTemp;

    protected AbstractPressRecipe(RecipeType<?> recipeType, ResourceLocation id, String group, RecipeIngredient ingredient, ItemStack result, float experience, int crushingTime, int minTemp) {
        super(recipeType, id, group, result, crushingTime, experience);

        this.ingredient = ingredient;
        this.minTemp = minTemp;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pContainer.getContainerSize() != 1 || pLevel.isClientSide()) {
            return false;
        }

        ItemStack stack = pContainer.getItem(0);

        return contains(stack);
    }

    public boolean contains(ItemStack stack) {
        if (ingredient.getIngredient().test(stack)) {
            return stack.getCount() >= ingredient.getAmount();
        }

        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, ingredient.getIngredient());
    }

    public int getIngredientAmount() {
        return ingredient.getAmount();
    }

    public int getMinTemp() {
        return minTemp;
    }
}
