package io.github.gtbauke.unnamedtechmod.datagen.custom.helpers;

import io.github.gtbauke.unnamedtechmod.utils.ItemWithAmount;
import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AlloySmeltingData {
    public NonNullList<RecipeIngredient> ingredients = NonNullList.create();
    public ItemStack result;

    public int alloyCompoundAmount;

    public int cookingTime;
    public float experience;

    public AlloySmeltingData() {}

    public AlloySmeltingData(ItemStack result, NonNullList<RecipeIngredient> ingredients, int alloyCompoundAmount, int cookingTime, float experience) {
        this.result = result;
        this.ingredients = ingredients;
        this.alloyCompoundAmount = alloyCompoundAmount;
        this.cookingTime = cookingTime;
        this.experience = experience;
    }
}