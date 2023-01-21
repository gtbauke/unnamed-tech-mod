package io.github.gtbauke.unnamedtechmod.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.datagen.custom.helpers.AlloySmeltingData;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import io.github.gtbauke.unnamedtechmod.utils.RecipeIngredient;
import io.github.gtbauke.unnamedtechmod.utils.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BasicAlloySmeltingRecipeBuilder implements RecipeBuilder {
    private final AlloySmeltingData data = new AlloySmeltingData();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public BasicAlloySmeltingRecipeBuilder(ItemLike result, int count) {
        data.result = new ItemStack(result.asItem(), count);
    }

    public static BasicAlloySmeltingRecipeBuilder alloySmelting(ItemLike result) {
        return new BasicAlloySmeltingRecipeBuilder(result, 1);
    }

    public static BasicAlloySmeltingRecipeBuilder alloySmelting(ItemLike result, int count) {
        return new BasicAlloySmeltingRecipeBuilder(result, count);
    }

    public BasicAlloySmeltingRecipeBuilder experience(float value) {
        data.experience = value;
        return this;
    }

    public BasicAlloySmeltingRecipeBuilder cookingTime(int value) {
        data.cookingTime = value;
        return this;
    }

    public BasicAlloySmeltingRecipeBuilder alloyCompoundAmount(int value) {
        data.alloyCompoundAmount = value;
        return this;
    }

    public BasicAlloySmeltingRecipeBuilder requires(ItemLike item, int amount) {
        int pIndex = data.ingredients.size();
        data.ingredients.add(pIndex, new RecipeIngredient(Ingredient.of(item), amount));

        return this;
    }

    public BasicAlloySmeltingRecipeBuilder requires(ItemLike item) {
        int pIndex = data.ingredients.size();
        data.ingredients.add(pIndex, new RecipeIngredient(Ingredient.of(item), 1));

        return this;
    }

    @Override
    public BasicAlloySmeltingRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public BasicAlloySmeltingRecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return data.result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.advancement.parent((new ResourceLocation("recipes/root")))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId))
                .requirements(RequirementsStrategy.OR);

        ResourceLocation loc = new ResourceLocation(pRecipeId.getNamespace(), "recipes/" +
                data.result.getItem().getItemCategory().getRecipeFolderName() + "/" + pRecipeId.getPath());

        pFinishedRecipeConsumer.accept(new BasicAlloySmeltingRecipeBuilder.Result(
                pRecipeId, data, advancement, loc));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final AlloySmeltingData data;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation pId, AlloySmeltingData data, Advancement.Builder pAdvancement,
                      ResourceLocation pAdvancementId) {
            this.id = pId;
            this.data = data;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            JsonArray ingredientsArray = new JsonArray();
            for (RecipeIngredient ingredient : data.ingredients) {
                JsonElement ingredientObj = ingredient.toJson();
                ingredientsArray.add(ingredientObj);
            }

            JsonObject result = new JsonObject();
            result.addProperty("item", Utils.getCraftingRegistryPath(data.result.getItem()));

            if (data.result.getCount() > 1) {
                result.addProperty("count", data.result.getCount());
            }

            pJson.add("ingredients", ingredientsArray);
            pJson.addProperty("alloyCompoundAmount", data.alloyCompoundAmount);
            pJson.addProperty("cookingTime", data.cookingTime);
            pJson.addProperty("experience", data.experience);
            pJson.add("output", result);
        }

        @Override
        public ResourceLocation getId() {
            String registryName = ForgeRegistries.ITEMS.getKey(data.result.getItem()).getPath();
            return new ResourceLocation(UnnamedTechMod.MOD_ID,
                    BasicAlloySmelterRecipe.Type.ID + "/" + registryName + "_from_basic_alloy_smelting");
        }

        @Override
        public RecipeSerializer<?> getType() {
            return BasicAlloySmelterRecipe.Serializer.INSTANCE;
        }

        @javax.annotation.Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @javax.annotation.Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
