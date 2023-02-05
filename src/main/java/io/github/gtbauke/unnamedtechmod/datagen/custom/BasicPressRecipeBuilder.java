package io.github.gtbauke.unnamedtechmod.datagen.custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.datagen.custom.helpers.PressData;
import io.github.gtbauke.unnamedtechmod.recipe.BasicPressRecipe;
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

public class BasicPressRecipeBuilder implements RecipeBuilder {
    private final PressData data = new PressData();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public BasicPressRecipeBuilder(ItemLike result, int count) {
        data.result = new ItemStack(result.asItem(), count);
    }

    @Override
    public BasicPressRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public static BasicPressRecipeBuilder pressing(ItemLike result, int count) {
        return new BasicPressRecipeBuilder(result, count);
    }

    public static BasicPressRecipeBuilder pressing(ItemLike result) {
        return new BasicPressRecipeBuilder(result, 1);
    }


    public BasicPressRecipeBuilder experience(float value) {
        data.experience = value;
        return this;
    }

    public BasicPressRecipeBuilder pressingTime(int value) {
        data.pressingTime = value;
        return this;
    }

    public BasicPressRecipeBuilder minimumTemperature(int value) {
        data.minimumTemperature = value;
        return this;
    }

    public BasicPressRecipeBuilder requires(ItemLike ingredient){
        data.ingredient = new RecipeIngredient(Ingredient.of(ingredient), 1);
        return this;
    }

    public BasicPressRecipeBuilder requires(ItemLike ingredient, int count){
        data.ingredient = new RecipeIngredient(Ingredient.of(ingredient), count);
        return this;
    }

    @Override
    public BasicPressRecipeBuilder group(@Nullable String pGroupName) {
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

        pFinishedRecipeConsumer.accept(new BasicPressRecipeBuilder.Result(
                pRecipeId, data, advancement, loc));
    }

    public static class Result implements FinishedRecipe{
        private final ResourceLocation id;
        private final PressData data;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation pId, PressData data, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {
            this.id = pId;
            this.data = data;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            JsonElement ingredient = data.ingredient.toJson();

            JsonObject result = new JsonObject();
            result.addProperty("item", Utils.getCraftingRegistryPath(data.result.getItem()));
            if (data.result.getCount() > 1) {
                result.addProperty("count", data.result.getCount());
            }

            pJson.add("input", ingredient);
            pJson.addProperty("pressingTime", data.pressingTime);
            pJson.addProperty("minimumTemperature", data.minimumTemperature);
            pJson.addProperty("experience", data.experience);
            pJson.add("output", result);
        }

        @Override
        public ResourceLocation getId() {
            String registryName = ForgeRegistries.ITEMS.getKey(data.result.getItem()).getPath();
            return new ResourceLocation(UnnamedTechMod.MOD_ID,
                    BasicPressRecipe.Type.ID + "/" + registryName + "_from_basic_press");
        }

        @Override
        public RecipeSerializer<?> getType() {
            return BasicPressRecipe.Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
