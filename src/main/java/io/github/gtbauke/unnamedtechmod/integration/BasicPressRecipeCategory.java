package io.github.gtbauke.unnamedtechmod.integration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.entity.base.PressTileBase;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.recipe.BasicPressRecipe;
import io.github.gtbauke.unnamedtechmod.utils.ModTags;
import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class BasicPressRecipeCategory implements IRecipeCategory<BasicPressRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(UnnamedTechMod.MOD_ID, BasicPressRecipe.Type.ID);
    public final static ResourceLocation TEXTURE = new ResourceLocation(UnnamedTechMod.MOD_ID, "textures/gui/basic_press.png");

    private final IDrawable background;
    private final IDrawable icon;

    private final int regularCookTime = PressTileBase.BURN_TIME_STANDARD;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    protected final IDrawableStatic staticFlame;
    protected final IDrawableStatic staticTemperature;
    protected final IDrawableAnimated animatedFlame;


    public BasicPressRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BASIC_PRESS.get()));

        this.staticFlame = helper.createDrawable(TEXTURE, 176, 0, 14, 14);
        this.staticTemperature = helper.createDrawable(TEXTURE, 176, 24, 16, 35);
        this.animatedFlame = helper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);

        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer cookTime) {
                        return helper.drawableBuilder(TEXTURE, 176, 14, 22, 11)
                                .buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
    }

    protected IDrawableAnimated getArrow(BasicPressRecipe recipe) {
        int cookTime = recipe.getProcessingTime();
        if (cookTime <= 0) {
            cookTime = PressTileBase.BURN_TIME_STANDARD;
        }

        return this.cachedArrows.getUnchecked(cookTime);
    }

    @Override
    public RecipeType<BasicPressRecipe> getRecipeType() {
        return JEIModPlugin.BASIC_PRESS_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.basic_press");
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
    public void draw(BasicPressRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        animatedFlame.draw(stack, 18, 44);

        IDrawableAnimated arrow = getArrow(recipe);
        arrow.draw(stack, 80, 38);

        drawExperience(recipe, stack, 10);
        drawCookTime(recipe, stack, 40);

        int temp = recipe.getMinTemp();
        int SCALE_MIN_TEMP = 0;
        int SCALE_MAX_TEMP = 1200;
        int tempScaled = ((temp - SCALE_MIN_TEMP) * 34) / (SCALE_MAX_TEMP - SCALE_MIN_TEMP);

        staticTemperature.draw(stack, 17, 6, 35 - tempScaled, 0, 0, 0);
    }

    protected void drawExperience(BasicPressRecipe recipe, PoseStack poseStack, int y) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(experienceString);
            fontRenderer.draw(poseStack, experienceString, getWidth() - stringWidth - 5, y, 0xFF808080);
        }
    }

    protected void drawCookTime(BasicPressRecipe recipe, PoseStack poseStack, int y) {
        int cookTime = recipe.getProcessingTime();
        if (cookTime > 0) {
            int cookTimeSeconds = cookTime / 20;
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(timeString);
            fontRenderer.draw(poseStack, timeString, getWidth() - stringWidth - 5, y, 0xFF808080);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BasicPressRecipe recipe, IFocusGroup focuses) {
        RecipeIngredient ingredients = recipe.getIngredient();

        builder.addSlot(RecipeIngredientRole.INPUT, 56, 35).addItemStack(ingredients.asItemStack());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addItemStack(recipe.getResultItem());
    }

    @Override
    public List<Component> getTooltipStrings(BasicPressRecipe recipe, IRecipeSlotsView recipeSlotsView, double pX, double pY) {
        if (pX >= 18 && pX <= 18 + 14 && pY >= 41 - 34 && pY <= 42) {
            return List.of(Component.literal(recipe.getMinTemp() + " K"));
        }

        return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, pX, pY);
    }
}