package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocksStateProvider extends BlockStateProvider {
    public ModBlocksStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, UnnamedTechMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.TIN_ORE.get());
        simpleBlockWithItem(ModBlocks.DEEPSLATE_TIN_ORE.get());

        simpleBlockWithItem(ModBlocks.RAW_TIN_BLOCK.get());
    }

    private void simpleBlockWithItem(Block block) {
        String registryName = ForgeRegistries.BLOCKS.getKey(block).getPath();
        ResourceLocation blockItemResourceLocation = new ResourceLocation(
                UnnamedTechMod.MOD_ID,
                "block/" + registryName
        );

        simpleBlock(block);
        itemModels().withExistingParent(registryName, blockItemResourceLocation);
    }
}
