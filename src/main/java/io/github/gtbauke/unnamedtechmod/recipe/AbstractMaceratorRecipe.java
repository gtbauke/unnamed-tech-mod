package io.github.gtbauke.unnamedtechmod.recipe;

import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public abstract class AbstractMaceratorRecipe implements Recipe<SimpleContainer> {
    protected final RecipeType<?> recipeType;
    public final ResourceLocation id;
    protected final String group;

    protected final NonNullList<RecipeIngredient> ingredients;
    protected final ItemStack result;

    protected final int crushingTime;
    protected final float experience;

    protected AbstractMaceratorRecipe(RecipeType<?> recipeType, ResourceLocation id, String group, NonNullList<RecipeIngredient> ingredients, ItemStack result, float experience, int crushingTime) {
        this.recipeType = recipeType;
        this.id = id;
        this.group = group;

        this.ingredients = ingredients;
        this.result = result;

        this.experience = experience;
        this.crushingTime = crushingTime;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pContainer.getContainerSize() != ingredients.size() + 1) {
            return false;
        }

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack stack = pContainer.getItem(i);

            boolean ingredientsContainsStack = contains(stack);

            if (!ingredientsContainsStack) {
                return false;
            }
        }

        return true;
    }

    public boolean contains(ItemStack stack) {
        for (int i = 0; i < ingredients.size(); i++) {
            RecipeIngredient ingredient = ingredients.get(i);

            if (ingredient.getIngredient().test(stack)) {
                if (stack.getCount() >= ingredient.getAmount()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public float getExperience() {
        return experience;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public int getCrushingTime() {
        return crushingTime;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> recipeIngredients = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

        for (int i = 0; i < recipeIngredients.size(); i++) {
            recipeIngredients.set(i, ingredients.get(i).getIngredient());
        }

        return recipeIngredients;
    }


    public NonNullList<RecipeIngredient> getRecipeIngredients() {
        return ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeType<?> getType() {
        return recipeType;
    }

    public boolean isIngredient(ItemLike item) {
        for (RecipeIngredient ingredient : ingredients) {
            if (!ingredient.getIngredient().test(new ItemStack(item))) {
                return false;
            }
        }

        return true;
    }

    public boolean ingredientsContain(SimpleContainer inputs) {
        return ingredients.subList(0, ingredients.size() - 1).stream().allMatch(ingredient -> inputs.hasAnyMatching(stack ->
                ingredient.getIngredient().test(stack) &&
                        ingredient.getAmount() == stack.getCount()
        ));
    }

    public int getUsedAmountOf(ItemLike input) {
        RecipeIngredient ingredient = ingredients.stream()
                .filter(i -> i.getIngredient().test(new ItemStack(input)))
                .findFirst()
                .orElse(null);

        return ingredient.getAmount();
    }
}
