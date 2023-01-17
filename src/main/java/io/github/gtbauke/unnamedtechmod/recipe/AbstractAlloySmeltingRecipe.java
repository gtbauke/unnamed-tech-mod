package io.github.gtbauke.unnamedtechmod.recipe;

import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.utils.AlloySmelting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAlloySmeltingRecipe implements Recipe<SimpleContainer> {
    protected final RecipeType<?> recipeType;
    public final ResourceLocation id;
    protected final String group;
    protected final ItemStack left;
    protected final ItemStack right;
    protected final int alloyCompoundAmount;

    protected final ItemStack result;
    protected final float experience;
    protected final int cookingTime;

    protected AbstractAlloySmeltingRecipe(RecipeType<?> recipeType, ResourceLocation id, String group, ItemStack left, ItemStack right, int alloyCompoundAmount, ItemStack result, float experience, int cookingTime) {
        this.recipeType = recipeType;
        this.id = id;
        this.group = group;
        this.left = left;
        this.right = right;
        this.alloyCompoundAmount = alloyCompoundAmount;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        Ingredient leftIngredient = Ingredient.of(left);
        int leftAmount = left.getCount();

        Ingredient rightIngredient = Ingredient.of(right);
        int rightAmount = right.getCount();

        boolean isCorrectIngredientLeft = leftIngredient.test(pContainer.getItem(AlloySmelting.LEFT_INPUT));
        boolean isCorrectAmountLeft = pContainer.getItem(AlloySmelting.LEFT_INPUT).getCount() >= leftAmount;

        boolean isCorrectIngredientRight = rightIngredient.test(pContainer.getItem(AlloySmelting.RIGHT_INPUT));
        boolean isCorrectAmountRight = pContainer.getItem(AlloySmelting.RIGHT_INPUT).getCount() >= rightAmount;

        boolean hasCorrectAlloyCompoundAmount = pContainer.getItem(AlloySmelting.ALLOY_COMPOUND).getCount() >= alloyCompoundAmount;

        return isCorrectIngredientLeft && isCorrectAmountLeft &&
                isCorrectIngredientRight && isCorrectAmountRight &&
                hasCorrectAlloyCompoundAmount;
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
        return NonNullList.of(Ingredient.of(left), Ingredient.of(right));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeType<?> getType() {
        return recipeType;
    }

    public ItemStack getLeft() {
        return left;
    }

    public ItemStack getRight() {
        return right;
    }

    public int getAlloyCompoundAmount() {
        return alloyCompoundAmount;
    }
}
