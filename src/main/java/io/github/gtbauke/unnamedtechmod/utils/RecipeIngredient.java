package io.github.gtbauke.unnamedtechmod.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class RecipeIngredient {
    private final Ingredient ingredient;
    private final int amount;

    public RecipeIngredient(Ingredient ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public static RecipeIngredient EMPTY = new RecipeIngredient(Ingredient.EMPTY, 0);

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack asItemStack() {
        return new ItemStack(ingredient.getItems()[0].getItem(), amount);
    }

    public static RecipeIngredient fromJson(JsonElement pJson) {
        if (!(pJson instanceof JsonObject)) {
            throw new IllegalStateException("Recipe ingredients must be an objecr");
        }

        int amount = GsonHelper.getAsInt((JsonObject) pJson, "count");
        Ingredient item = Ingredient.fromJson(pJson);

        return new RecipeIngredient(item, amount);
    }

    public static RecipeIngredient fromNetwork(FriendlyByteBuf pBuffer) {
        int amount = pBuffer.readInt();
        Ingredient item = Ingredient.fromNetwork(pBuffer);

        return new RecipeIngredient(item, amount);
    }

    public void toNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(amount);
        ingredient.toNetwork(pBuffer);
    }

    public JsonElement toJson() {
        JsonElement element = ingredient.toJson();

        if (!element.isJsonObject()) {
            throw new IllegalStateException("Recipe Ingredients must be objects");
        }

        JsonObject obj = element.getAsJsonObject();
        obj.addProperty("count", amount);

        return obj;
    }
}
