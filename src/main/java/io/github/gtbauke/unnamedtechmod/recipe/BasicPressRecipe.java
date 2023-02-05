package io.github.gtbauke.unnamedtechmod.recipe;

import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class BasicPressRecipe extends AbstractPressRecipe {
    protected BasicPressRecipe(ResourceLocation id, RecipeIngredient ingredient, ItemStack result, float experience, int crushingTime, int minTemp) {
        super(Type.INSTANCE, id, "", ingredient, result, experience, crushingTime, minTemp);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Type implements RecipeType<BasicPressRecipe> {
        private Type() {}

        public static final BasicPressRecipe.Type INSTANCE = new BasicPressRecipe.Type();
        public static final String ID = "basic_press";
    }

    public static class Serializer implements RecipeSerializer<BasicPressRecipe> {
        public static final BasicPressRecipe.Serializer INSTANCE = new BasicPressRecipe.Serializer();
        public static final ResourceLocation ID = new ResourceLocation(UnnamedTechMod.MOD_ID, BasicPressRecipe.Type.ID);

        @Override
        public BasicPressRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            RecipeIngredient input = RecipeIngredient.fromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));

            int cookingTime = GsonHelper.getAsInt(pSerializedRecipe, "pressingTime", 200);
            int minTemp = GsonHelper.getAsInt(pSerializedRecipe, "minimumTemperature", 200);
            float experience = GsonHelper.getAsFloat(pSerializedRecipe, "experience", 1);

            return new BasicPressRecipe(pRecipeId, input, output, experience, cookingTime, minTemp);
        }

        @Override
        public @Nullable BasicPressRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            RecipeIngredient input = RecipeIngredient.fromNetwork(pBuffer);
            int cookingTime = pBuffer.readInt();
            int minTemp = pBuffer.readInt();
            float experience = pBuffer.readFloat();
            ItemStack output = pBuffer.readItem();

            return new BasicPressRecipe(pRecipeId, input, output, experience, cookingTime, minTemp);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, BasicPressRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeInt(pRecipe.processingTime);
            pBuffer.writeInt(pRecipe.minTemp);
            pBuffer.writeFloat(pRecipe.experience);
            pBuffer.writeItemStack(pRecipe.getResultItem(), false);
        }
    }
}
