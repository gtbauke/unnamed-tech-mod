package io.github.gtbauke.unnamedtechmod.integration;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BasicAlloySmeltingRecipeCategory implements IRecipeCategory<BasicAlloySmelterRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(UnnamedTechMod.MOD_ID, BasicAlloySmelterRecipe.Type.ID);
    public final static ResourceLocation TEXTURE = new ResourceLocation(UnnamedTechMod.MOD_ID, "textures/gui/basic_alloy_smelter.png");

    private final IDrawable background;
    private final IDrawable icon;

    public BasicAlloySmeltingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BASIC_ALLOY_SMELTER.get()));
    }

    @Override
    public RecipeType<BasicAlloySmelterRecipe> getRecipeType() {
        return JEIModPlugin.BASIC_ALLOY_SMELTING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.basic_alloy_smelter");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BasicAlloySmelterRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 34, 17).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addItemStack(recipe.getResultItem());
    }
}
