package io.github.gtbauke.unnamedtechmod.integration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.entity.base.AlloySmelterTileBase;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import io.github.gtbauke.unnamedtechmod.utils.AlloySmelting;
import io.github.gtbauke.unnamedtechmod.utils.ModTags;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.items.SlotItemHandler;

public class BasicAlloySmeltingRecipeCategory implements IRecipeCategory<BasicAlloySmelterRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(UnnamedTechMod.MOD_ID, BasicAlloySmelterRecipe.Type.ID);
    public final static ResourceLocation TEXTURE = new ResourceLocation(UnnamedTechMod.MOD_ID, "textures/gui/basic_alloy_smelter.png");

    private final IDrawable background;
    private final IDrawable icon;

    private final int regularCookTime = AlloySmelterTileBase.BURN_TIME_STANDARD;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    protected final IDrawableStatic staticFlame;
    protected final IDrawableAnimated animatedFlame;


    public BasicAlloySmeltingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BASIC_ALLOY_SMELTER.get()));

        this.staticFlame = helper.createDrawable(TEXTURE, 176, 0, 14, 14);
        this.animatedFlame = helper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);

        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer cookTime) {
                        return helper.drawableBuilder(TEXTURE, 176, 14, 24, 16)
                                .buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
    }

    protected IDrawableAnimated getArrow(BasicAlloySmelterRecipe recipe) {
        int cookTime = recipe.getCookingTime();
        if (cookTime <= 0) {
            cookTime = AlloySmelterTileBase.BURN_TIME_STANDARD;
        }

        return this.cachedArrows.getUnchecked(cookTime);
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
    public void draw(BasicAlloySmelterRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        animatedFlame.draw(stack, 46, 36);

        IDrawableAnimated arrow = getArrow(recipe);
        arrow.draw(stack, 79, 34);

        drawExperience(recipe, stack, 10);
        drawCookTime(recipe, stack, 55);
    }

    protected void drawExperience(BasicAlloySmelterRecipe recipe, PoseStack poseStack, int y) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(experienceString);
            fontRenderer.draw(poseStack, experienceString, getWidth() - stringWidth - 5, y, 0xFF808080);
        }
    }

    protected void drawCookTime(BasicAlloySmelterRecipe recipe, PoseStack poseStack, int y) {
        int cookTime = recipe.getCookingTime();
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
    public void setRecipe(IRecipeLayoutBuilder builder, BasicAlloySmelterRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 34, 17).addItemStack(recipe.getLeft());
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 17).addItemStack(recipe.getRight());
        builder.addSlot(RecipeIngredientRole.CATALYST, 34, 53).addIngredients(Ingredient.of(ModTags.Items.ALLOY_COMPOUND));
        // FUEL builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 56, 53).addIngredients(Ingredient.));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addItemStack(recipe.getResultItem());
    }
}
