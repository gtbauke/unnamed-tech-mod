package io.github.gtbauke.unnamedtechmod.datagen.custom.helpers;

import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class MaceratorData {
    public RecipeIngredient ingredient;
    public ItemStack result;

    public int crushingTime;
    public float experience;

    public MaceratorData() {}

    public MaceratorData(ItemStack result, RecipeIngredient ingredient, int crushingTime, float experience) {
        this.result = result;
        this.ingredient = ingredient;
        this.crushingTime = crushingTime;
        this.experience = experience;
    }
}
