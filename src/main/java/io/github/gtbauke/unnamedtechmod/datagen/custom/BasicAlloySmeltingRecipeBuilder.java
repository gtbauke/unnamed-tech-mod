package io.github.gtbauke.unnamedtechmod.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BasicAlloySmeltingRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final int count;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public BasicAlloySmeltingRecipeBuilder(ItemLike ingredient, ItemLike result, int count) {
        this.ingredient = Ingredient.of(ingredient);
        this.result = result.asItem();
        this.count = count;
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.advancement.parent((new ResourceLocation("recipes/root")))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId))
                .requirements(RequirementsStrategy.OR);

        pFinishedRecipeConsumer.accept(new BasicAlloySmeltingRecipeBuilder.Result(
                pRecipeId, this.result, this.count, this.ingredient,
                this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/" +
                this.result.getItemCategory().getRecipeFolderName() + "/" + pRecipeId.getPath())
        ));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final Ingredient ingredient;
        private final int count;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation pId, Item pResult, int pCount, Ingredient ingredient, Advancement.Builder pAdvancement,
                      ResourceLocation pAdvancementId) {
            this.id = pId;
            this.result = pResult;
            this.count = pCount;
            this.ingredient = ingredient;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            JsonArray jsonarray = new JsonArray();
            jsonarray.add(ingredient.toJson());

            pJson.add("ingredients", jsonarray);
            JsonObject jsonobject = new JsonObject();

            String registryName = ForgeRegistries.ITEMS.getKey(this.result).getPath();
            jsonobject.addProperty("item", UnnamedTechMod.MOD_ID + ":" + registryName);
            if (this.count > 1) {
                jsonobject.addProperty("count", this.count);
            }

            pJson.add("output", jsonobject);
        }

        @Override
        public ResourceLocation getId() {
            String registryName = ForgeRegistries.ITEMS.getKey(this.result).getPath();
            return new ResourceLocation(UnnamedTechMod.MOD_ID,
                    registryName + "_from_basic_alloy_smelting");
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
