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

    protected final RecipeIngredient ingredient;
    protected final ItemStack result;

    protected final int crushingTime;
    protected final float experience;

    protected AbstractMaceratorRecipe(RecipeType<?> recipeType, ResourceLocation id, String group, RecipeIngredient ingredient, ItemStack result, float experience, int crushingTime) {
        this.recipeType = recipeType;
        this.id = id;
        this.group = group;

        this.ingredient = ingredient;
        this.result = result;

        this.experience = experience;
        this.crushingTime = crushingTime;
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
        return NonNullList.withSize(1, ingredient.getIngredient());
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
        return ingredient.getIngredient().test(new ItemStack(item));
    }

    public int getUsedAmountOf(ItemLike input) {
        return ingredient.getAmount();
    }
}
