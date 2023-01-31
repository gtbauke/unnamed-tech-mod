package io.github.gtbauke.unnamedtechmod.integration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.entity.base.AlloySmelterTileBase;
import io.github.gtbauke.unnamedtechmod.block.entity.base.MaceratorTileBase;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.recipe.ManualMaceratorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ManualMaceratorRecipeCategory implements IRecipeCategory<ManualMaceratorRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(UnnamedTechMod.MOD_ID, ManualMaceratorRecipe.Type.ID);
    public final static ResourceLocation TEXTURE = new ResourceLocation(UnnamedTechMod.MOD_ID, "textures/gui/manual_macerator.png");

    private final IDrawable background;
    private final IDrawable icon;

    private final int regularCookTime = AlloySmelterTileBase.BURN_TIME_STANDARD;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public ManualMaceratorRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MANUAL_MACERATOR.get()));

        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer crushTime) {
                        return helper.drawableBuilder(TEXTURE, 176, 0, 27, 16)
                                .buildAnimated(crushTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
    }

    @Override
    public RecipeType<ManualMaceratorRecipe> getRecipeType() {
        return JEIModPlugin.MANUAL_MACERATOR_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.manual_macerator");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    protected IDrawableAnimated getArrow(ManualMaceratorRecipe recipe) {
        int crushTime = recipe.getCrushingTime();
        if (crushTime <= 0) {
            crushTime = MaceratorTileBase.CRUSH_TIME_STANDARD;
        }

        return this.cachedArrows.getUnchecked(crushTime);
    }

    @Override
    public void draw(ManualMaceratorRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        IDrawableAnimated arrow = getArrow(recipe);
        arrow.draw(stack, 71, 35);

        drawExperience(recipe, stack, 10);
        drawCookTime(recipe, stack, 55);
    }

    protected void drawExperience(ManualMaceratorRecipe recipe, PoseStack poseStack, int y) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(experienceString);
            fontRenderer.draw(poseStack, experienceString, getWidth() - stringWidth - 5, y, 0xFF808080);
        }
    }

    protected void drawCookTime(ManualMaceratorRecipe recipe, PoseStack poseStack, int y) {
        int crushTime = recipe.getCrushingTime();
        if (crushTime > 0) {
            int cookTimeSeconds = crushTime / 20;
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(timeString);
            fontRenderer.draw(poseStack, timeString, getWidth() - stringWidth - 5, y, 0xFF808080);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ManualMaceratorRecipe recipe, IFocusGroup focuses) {
        ItemStack ingredient = recipe.getIngredient().asItemStack();

        builder.addSlot(RecipeIngredientRole.INPUT, 41, 36).addItemStack(ingredient);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addItemStack(recipe.getResultItem());
    }
}
