package io.github.gtbauke.unnamedtechmod.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class ManualMaceratorRecipe extends AbstractMaceratorRecipe{
    protected ManualMaceratorRecipe(ResourceLocation id, NonNullList<RecipeIngredient> ingredients, ItemStack result, float experience, int crushingTime) {
        super(Type.INSTANCE, id, "", ingredients, result, experience, crushingTime);
    }

    public static class Type implements RecipeType<ManualMaceratorRecipe> {
        private Type() {}

        public static final ManualMaceratorRecipe.Type INSTANCE = new ManualMaceratorRecipe.Type();
        public static final String ID = "manual_macerator";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<ManualMaceratorRecipe> {
        public static final ManualMaceratorRecipe.Serializer INSTANCE = new ManualMaceratorRecipe.Serializer();
        public static final ResourceLocation ID = new ResourceLocation(UnnamedTechMod.MOD_ID, ManualMaceratorRecipe.Type.ID);

        @Override
        public ManualMaceratorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredientsArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<RecipeIngredient> recipeIngredients = NonNullList.withSize(ingredientsArray.size(), RecipeIngredient.EMPTY);

            for (int i = 0; i < recipeIngredients.size(); i++) {
                recipeIngredients.set(i, RecipeIngredient.fromJson(ingredientsArray.get(i)));
            }

            int cookingTime = GsonHelper.getAsInt(pSerializedRecipe, "cookingTime", 1);
            float experience = GsonHelper.getAsFloat(pSerializedRecipe, "experience", 0);

            return new ManualMaceratorRecipe(pRecipeId, recipeIngredients, output, experience, cookingTime);
        }

        @Override
        public @Nullable ManualMaceratorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<RecipeIngredient> recipeIngredients = NonNullList.withSize(pBuffer.readInt(), RecipeIngredient.EMPTY);

            for (int i = 0; i < recipeIngredients.size(); i++) {
                recipeIngredients.set(i, RecipeIngredient.fromNetwork(pBuffer));
            }

            int cookingTime = pBuffer.readInt();
            float experience = pBuffer.readFloat();
            ItemStack output = pBuffer.readItem();

            return new ManualMaceratorRecipe(pRecipeId, recipeIngredients, output, experience, cookingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ManualMaceratorRecipe pRecipe) {

        }
    }
}
