package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UnnamedTechMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        ModItemsModelProvider itemModelProvider = new ModItemsModelProvider(generator, existingFileHelper);
        ModBlocksStateProvider blocksStateProvider = new ModBlocksStateProvider(generator, existingFileHelper);

        ModBlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator, existingFileHelper);
        ModItemTagsProvider itemTagsProvider = new ModItemTagsProvider(generator, blockTagsProvider, existingFileHelper);

        ModLootTableProvider lootTableProvider = new ModLootTableProvider(generator);

        ModRecipeProvider recipeProvider = new ModRecipeProvider(generator);

        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), itemTagsProvider);

        generator.addProvider(event.includeServer(), lootTableProvider);

        generator.addProvider(event.includeServer(), blocksStateProvider);
        generator.addProvider(event.includeServer(), itemModelProvider);

        generator.addProvider(event.includeServer(), recipeProvider);
    }
}
