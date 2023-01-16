package io.github.gtbauke.unnamedtechmod.recipe;

import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
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

    protected BasicAlloySmelterRecipe(ResourceLocation id, ItemStack left, ItemStack right, int alloyCompoundAmount, ItemStack result, float experience, int cookingTime) {
        super(Type.INSTANCE, id, "", left, right, alloyCompoundAmount, result, experience, cookingTime);
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

            ItemStack left = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "left")
            );

            ItemStack right = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "right")
            );

            int alloyCompoundAmount = GsonHelper.getAsInt(pSerializedRecipe, "alloyCompoundAmount", 0);
            int cookingTime = GsonHelper.getAsInt(pSerializedRecipe, "cookingTime", 1);
            float experience = GsonHelper.getAsFloat(pSerializedRecipe, "experience", 0);

            return new BasicAlloySmelterRecipe(pRecipeId, left, right, alloyCompoundAmount, output, experience, cookingTime);
        }

        @Override
        public @Nullable BasicAlloySmelterRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            ItemStack left = pBuffer.readItem();
            ItemStack right = pBuffer.readItem();
            int alloyCompoundAmount = pBuffer.readInt();
            int cookingTime = pBuffer.readInt();
            float experience = pBuffer.readFloat();
            ItemStack output = pBuffer.readItem();

            return new BasicAlloySmelterRecipe(pRecipeId, left, right, alloyCompoundAmount, output, experience, cookingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, BasicAlloySmelterRecipe pRecipe) {
            pBuffer.writeItemStack(pRecipe.left, false);
            pBuffer.writeItemStack(pRecipe.right, false);
            pBuffer.writeInt(pRecipe.alloyCompoundAmount);
            pBuffer.writeInt(pRecipe.cookingTime);
            pBuffer.writeFloat(pRecipe.experience);

            pBuffer.writeItemStack(pRecipe.getResultItem(), false);
        }
    }
}
