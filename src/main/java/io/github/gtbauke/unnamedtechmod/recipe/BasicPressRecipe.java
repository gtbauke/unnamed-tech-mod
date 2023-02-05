package io.github.gtbauke.unnamedtechmod.recipe;

import net.minecraft.world.item.crafting.RecipeType;

public class BasicPressRecipe extends AbstractPressRecipe {
    public static class Type implements RecipeType<BasicPressRecipe> {
        private Type() {}

        public static final BasicPressRecipe.Type INSTANCE = new BasicPressRecipe.Type();
        public static final String ID = "basic_press";
    }
}
