package io.github.gtbauke.unnamedtechmod.recipe;

import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.utils.AlloySmelting;
import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAlloySmeltingRecipe implements Recipe<SimpleContainer> {
    protected final RecipeType<?> recipeType;
    public final ResourceLocation id;
    protected final String group;

    protected final NonNullList<RecipeIngredient> ingredients;
    protected final ItemStack result;

    protected final int alloyCompoundAmount;
    protected final int cookingTime;
    protected final float experience;

    protected AbstractAlloySmeltingRecipe(RecipeType<?> recipeType, ResourceLocation id, String group, NonNullList<RecipeIngredient> ingredients, int alloyCompoundAmount, ItemStack result, float experience, int cookingTime) {
        this.recipeType = recipeType;
        this.id = id;
        this.group = group;

        this.ingredients = ingredients;
        this.result = result;

        this.alloyCompoundAmount = alloyCompoundAmount;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        boolean containsEachIngredient = ingredients.stream().map(ingredient -> {
            boolean containerHas = pContainer.hasAnyMatching(stack ->
                    ingredient.getIngredient().test(stack) &&
                    ingredient.getAmount() == stack.getCount()
            );

            return containerHas;
        }).allMatch(p -> true);

        boolean hasCorrectAlloyCompoundAmount = pContainer.getItem(AlloySmelting.ALLOY_COMPOUND).getCount() >= alloyCompoundAmount;

        return containsEachIngredient && hasCorrectAlloyCompoundAmount;
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

    public int getCookingTime() {
        return cookingTime;
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


    public int getAlloyCompoundAmount() {
        return alloyCompoundAmount;
    }

    public boolean ingredientsContain(SimpleContainer inputs) {
        return ingredients.stream().allMatch(ingredient -> inputs.hasAnyMatching(stack ->
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
