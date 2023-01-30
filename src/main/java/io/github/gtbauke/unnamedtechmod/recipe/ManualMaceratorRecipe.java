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

public class ManualMaceratorRecipe extends AbstractMaceratorRecipe {
    protected ManualMaceratorRecipe(ResourceLocation id, RecipeIngredient ingredient, ItemStack result, float experience, int crushingTime) {
        super(Type.INSTANCE, id, "", ingredient, result, experience, crushingTime);
    }

    public static class Type implements RecipeType<ManualMaceratorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new ManualMaceratorRecipe.Type();
        public static final String ID = "manual_macerator";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<ManualMaceratorRecipe> {
        public static final Serializer INSTANCE = new ManualMaceratorRecipe.Serializer();
        public static final ResourceLocation ID = new ResourceLocation(UnnamedTechMod.MOD_ID, ManualMaceratorRecipe.Type.ID);

        @Override
        public ManualMaceratorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            RecipeIngredient input = RecipeIngredient.fromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));

            int cookingTime = GsonHelper.getAsInt(pSerializedRecipe, "crushingTime", 200);
            float experience = GsonHelper.getAsFloat(pSerializedRecipe, "experience", 1);

            return new ManualMaceratorRecipe(pRecipeId, input, output, experience, cookingTime);
        }

        @Override
        public @Nullable ManualMaceratorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            RecipeIngredient input = RecipeIngredient.fromNetwork(pBuffer);
            int cookingTime = pBuffer.readInt();
            float experience = pBuffer.readFloat();
            ItemStack output = pBuffer.readItem();

            return new ManualMaceratorRecipe(pRecipeId, input, output, experience, cookingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ManualMaceratorRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeInt(pRecipe.crushingTime);
            pBuffer.writeFloat(pRecipe.experience);
            pBuffer.writeItemStack(pRecipe.getResultItem(), false);
        }
    }
}
