package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItemsModelProvider extends ItemModelProvider {
    public ModItemsModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, UnnamedTechMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.RAW_TIN_ORE.get());
    }

    private ItemModelBuilder simpleItem(Item item) {
        String registryName = ForgeRegistries.ITEMS.getKey(item).getPath();
        ResourceLocation parentResourceLocation = new ResourceLocation("item/generated");
        ResourceLocation itemResourceLocation = new ResourceLocation(
                UnnamedTechMod.MOD_ID,
                "item/" + registryName
        );

        return withExistingParent(registryName, parentResourceLocation)
                .texture("layer0", itemResourceLocation);
    }

    private ItemModelBuilder handheldItem(Item item) {
        String registryName = ForgeRegistries.ITEMS.getKey(item).getPath();
        ResourceLocation parentResourceLocation = new ResourceLocation("item/handheld");
        ResourceLocation itemResourceLocation = new ResourceLocation(
                UnnamedTechMod.MOD_ID,
                "item/" + registryName
        );

        return withExistingParent(registryName, parentResourceLocation)
                .texture("layer0", itemResourceLocation);
    }
}
