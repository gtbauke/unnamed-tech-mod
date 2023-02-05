package io.github.gtbauke.unnamedtechmod.datagen.custom.helpers;

import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import net.minecraft.world.item.ItemStack;

public class PressData {
    public RecipeIngredient ingredient;
    public ItemStack result;

    public int pressingTime;
    public int minimumTemperature;
    public float experience;

    public PressData() {}

    public PressData(ItemStack result, RecipeIngredient ingredient, int crushingTime, float experience, int minimumTemperature) {
        this.result = result;
        this.ingredient = ingredient;
        this.pressingTime = crushingTime;
        this.experience = experience;
        this.minimumTemperature = minimumTemperature;
    }
}
