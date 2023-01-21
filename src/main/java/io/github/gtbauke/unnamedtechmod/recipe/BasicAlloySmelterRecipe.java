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
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

public class BasicAlloySmelterRecipe extends AbstractAlloySmeltingRecipe {
    public static class Type implements RecipeType<BasicAlloySmelterRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "basic_alloy_smelting";
    }

    public static final ResourceLocation id = new ResourceLocation(
            UnnamedTechMod.MOD_ID,
            Type.ID
    );

    protected BasicAlloySmelterRecipe(ResourceLocation id, NonNullList<RecipeIngredient> ingredients, int alloyCompoundAmount, ItemStack result, float experience, int cookingTime) {
        super(Type.INSTANCE, id, "", ingredients, alloyCompoundAmount, result, experience, cookingTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<BasicAlloySmelterRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(UnnamedTechMod.MOD_ID, Type.ID);

        @Override
        public BasicAlloySmelterRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredientsArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<RecipeIngredient> recipeIngredients = NonNullList.withSize(ingredientsArray.size(), RecipeIngredient.EMPTY);

            for (int i = 0; i < recipeIngredients.size(); i++) {
                recipeIngredients.set(i, RecipeIngredient.fromJson(ingredientsArray.get(i)));
            }

            int alloyCompoundAmount = GsonHelper.getAsInt(pSerializedRecipe, "alloyCompoundAmount", 0);
            int cookingTime = GsonHelper.getAsInt(pSerializedRecipe, "cookingTime", 1);
            float experience = GsonHelper.getAsFloat(pSerializedRecipe, "experience", 0);

            return new BasicAlloySmelterRecipe(pRecipeId, recipeIngredients, alloyCompoundAmount, output, experience, cookingTime);
        }

        @Override
        public @Nullable BasicAlloySmelterRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<RecipeIngredient> recipeIngredients = NonNullList.withSize(pBuffer.readInt(), RecipeIngredient.EMPTY);

            for (int i = 0; i < recipeIngredients.size(); i++) {
                recipeIngredients.set(i, RecipeIngredient.fromNetwork(pBuffer));
            }

            int alloyCompoundAmount = pBuffer.readInt();
            int cookingTime = pBuffer.readInt();
            float experience = pBuffer.readFloat();
            ItemStack output = pBuffer.readItem();

            return new BasicAlloySmelterRecipe(pRecipeId, recipeIngredients, alloyCompoundAmount, output, experience, cookingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, BasicAlloySmelterRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.ingredients.size());

            for (RecipeIngredient ingredient : pRecipe.ingredients) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeInt(pRecipe.alloyCompoundAmount);
            pBuffer.writeInt(pRecipe.cookingTime);
            pBuffer.writeFloat(pRecipe.experience);

            pBuffer.writeItemStack(pRecipe.getResultItem(), false);
        }
    }
}
