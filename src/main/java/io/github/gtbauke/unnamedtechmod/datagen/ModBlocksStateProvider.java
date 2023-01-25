package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractAlloySmelterBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
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

        simpleBlockWithItem(ModBlocks.LIGHT_BRICKS.get());

        String basicAlloySmelter = ForgeRegistries.BLOCKS.getKey(ModBlocks.BASIC_ALLOY_SMELTER.get()).getPath();

        ModelFile defaultStateFile = models().orientable(
                basicAlloySmelter,
                modTexture(basicAlloySmelter + "_side"),
                modTexture(basicAlloySmelter + "_front"),
                modTexture(basicAlloySmelter + "_top")
        );

        ModelFile activeStateFile = models().orientable(
                basicAlloySmelter + "_on",
                modTexture(basicAlloySmelter + "_side"),
                modTexture(basicAlloySmelter + "_front_on"),
                modTexture(basicAlloySmelter + "_top")
        );

        getVariantBuilder(ModBlocks.BASIC_ALLOY_SMELTER.get())
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.EAST)
                .with(AbstractAlloySmelterBlock.LIT, false)
                .modelForState()
                .rotationY(90)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.WEST)
                .with(AbstractAlloySmelterBlock.LIT, false)
                .modelForState()
                .rotationY(270)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.NORTH)
                .with(AbstractAlloySmelterBlock.LIT, false)
                .modelForState()
                .rotationY(0)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.SOUTH)
                .with(AbstractAlloySmelterBlock.LIT, false)
                .modelForState()
                .rotationY(180)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.EAST)
                .with(AbstractAlloySmelterBlock.LIT, true)
                .modelForState()
                .rotationY(90)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.WEST)
                .with(AbstractAlloySmelterBlock.LIT, true)
                .modelForState()
                .rotationY(270)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.NORTH)
                .with(AbstractAlloySmelterBlock.LIT, true)
                .modelForState()
                .rotationY(0)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.SOUTH)
                .with(AbstractAlloySmelterBlock.LIT, true)
                .modelForState()
                .rotationY(180)
                .modelFile(activeStateFile)
                .addModel();

        getVariantBuilder(ModBlocks.MANUAL_MACERATOR.get())
                .partialState()
                .with(AbstractMaceratorBlock.FACING, Direction.EAST)
                .with(AbstractMaceratorBlock.WORKING, false)
                .modelForState()
                .rotationY(90)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractMaceratorBlock.FACING, Direction.WEST)
                .with(AbstractMaceratorBlock.WORKING, false)
                .modelForState()
                .rotationY(270)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractMaceratorBlock.FACING, Direction.NORTH)
                .with(AbstractMaceratorBlock.WORKING, false)
                .modelForState()
                .rotationY(0)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractMaceratorBlock.FACING, Direction.SOUTH)
                .with(AbstractMaceratorBlock.WORKING, false)
                .modelForState()
                .rotationY(180)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractMaceratorBlock.FACING, Direction.EAST)
                .with(AbstractMaceratorBlock.WORKING, true)
                .modelForState()
                .rotationY(90)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractMaceratorBlock.FACING, Direction.WEST)
                .with(AbstractMaceratorBlock.WORKING, true)
                .modelForState()
                .rotationY(270)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractMaceratorBlock.FACING, Direction.NORTH)
                .with(AbstractMaceratorBlock.WORKING, true)
                .modelForState()
                .rotationY(0)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractMaceratorBlock.FACING, Direction.SOUTH)
                .with(AbstractMaceratorBlock.WORKING, true)
                .modelForState()
                .rotationY(180)
                .modelFile(activeStateFile)
                .addModel();

        itemOnly(ModBlocks.BASIC_ALLOY_SMELTER.get());
        itemOnly(ModBlocks.MANUAL_MACERATOR.get());
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

    private void horizontalBlockWithItem(Block block) {
        String registryName = ForgeRegistries.BLOCKS.getKey(block).getPath();
        ResourceLocation front = new ResourceLocation(
                UnnamedTechMod.MOD_ID,
                "block/" + registryName + "_front"
        );

        ResourceLocation side = new ResourceLocation(
                UnnamedTechMod.MOD_ID,
                "block/" + registryName + "_side"
        );

        ResourceLocation top = new ResourceLocation(
                UnnamedTechMod.MOD_ID,
                "block/" + registryName + "_top"
        );

        horizontalBlock(block, side, front, top);
        itemModels().orientable(registryName, side, front, top);
    }

    private void itemOnly(Block block) {
        String registryName = ForgeRegistries.BLOCKS.getKey(block).getPath();
        ResourceLocation blockItemResourceLocation = new ResourceLocation(
                UnnamedTechMod.MOD_ID,
                "block/" + registryName
        );

        itemModels().withExistingParent(registryName, blockItemResourceLocation);
    }

    private ResourceLocation modTexture(String name) {
        return new ResourceLocation(UnnamedTechMod.MOD_ID, "block/" + name);
    }
}
