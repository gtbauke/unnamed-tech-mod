package io.github.gtbauke.unnamedtechmod.datagen.custom;

import com.google.gson.JsonObject;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.datagen.custom.helpers.MaceratorData;
import io.github.gtbauke.unnamedtechmod.recipe.ManualMaceratorRecipe;
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

public class ManualMaceratorRecipeBuilder implements RecipeBuilder {
    private final MaceratorData data = new MaceratorData();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public ManualMaceratorRecipeBuilder(ItemLike result, int count) {
        data.result = new ItemStack(result, count);
    }

    public static ManualMaceratorRecipeBuilder crushing(ItemLike result, int amount) {
        return new ManualMaceratorRecipeBuilder(result, amount);
    }

    public static ManualMaceratorRecipeBuilder crushing(ItemLike result) {
        return new ManualMaceratorRecipeBuilder(result, 1);
    }

    public ManualMaceratorRecipeBuilder experience(float value) {
        data.experience = value;
        return this;
    }

    public ManualMaceratorRecipeBuilder crushingTime(int value) {
        data.crushingTime = value;
        return this;
    }

    public ManualMaceratorRecipeBuilder requires(ItemLike ingredient){
        data.ingredient = new RecipeIngredient(Ingredient.of(ingredient), 1);
        return this;
    }

    @Override
    public ManualMaceratorRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public ManualMaceratorRecipeBuilder group(@Nullable String pGroupName) {
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

        pFinishedRecipeConsumer.accept(new ManualMaceratorRecipeBuilder.Result(
                pRecipeId, data, advancement, loc));
    }

    public static class Result implements FinishedRecipe{
        private final ResourceLocation id;
        private final MaceratorData data;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation pId, MaceratorData data, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {
            this.id = pId;
            this.data = data;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("item", Utils.getCraftingRegistryPath(data.ingredient.asItemStack().getItem()));
            if (data.ingredient.getAmount() > 1) {
                ingredient.addProperty("count", data.ingredient.getAmount());
            }

            JsonObject result = new JsonObject();
            result.addProperty("item", Utils.getCraftingRegistryPath(data.result.getItem()));
            if (data.result.getCount() > 1) {
                result.addProperty("count", data.result.getCount());
            }

            pJson.add("input", ingredient);
            pJson.addProperty("crushingTime", data.crushingTime);
            pJson.addProperty("experience", data.experience);
            pJson.add("output", result);
        }

        @Override
        public ResourceLocation getId() {
            String registryName = ForgeRegistries.ITEMS.getKey(data.result.getItem()).getPath();
            return new ResourceLocation(UnnamedTechMod.MOD_ID,
                    ManualMaceratorRecipe.Type.ID + "/" + registryName + "_from_manual_macerator");
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ManualMaceratorRecipe.Serializer.INSTANCE;
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
