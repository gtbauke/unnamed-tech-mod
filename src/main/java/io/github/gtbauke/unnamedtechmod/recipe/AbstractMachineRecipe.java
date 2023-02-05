package io.github.gtbauke.unnamedtechmod.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class AbstractMachineRecipe implements Recipe<SimpleContainer> {
    protected final RecipeType<?> recipeType;
    public final ResourceLocation id;
    protected final String group;

    protected final ItemStack result;

    protected final int processingTime;
    protected final float experience;

    protected AbstractMachineRecipe(RecipeType<?> recipeType, ResourceLocation id, String group, ItemStack result, int processingTime, float experience) {
        this.recipeType = recipeType;
        this.id = id;
        this.group = group;
        this.result = result;
        this.processingTime = processingTime;
        this.experience = experience;
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

    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeType<?> getType() {
        return recipeType;
    }
}
