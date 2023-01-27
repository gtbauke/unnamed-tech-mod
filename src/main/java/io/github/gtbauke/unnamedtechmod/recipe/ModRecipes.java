package io.github.gtbauke.unnamedtechmod.recipe;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, UnnamedTechMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<BasicAlloySmelterRecipe>> BASIC_ALLOY_SMELTER_SERIALIZER =
            SERIALIZERS.register(BasicAlloySmelterRecipe.Type.ID, () -> BasicAlloySmelterRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ManualMaceratorRecipe>> MANUAL_MACERATOR_SERIALIZER =
            SERIALIZERS.register(ManualMaceratorRecipe.Type.ID, () -> ManualMaceratorRecipe.Serializer.INSTANCE);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
