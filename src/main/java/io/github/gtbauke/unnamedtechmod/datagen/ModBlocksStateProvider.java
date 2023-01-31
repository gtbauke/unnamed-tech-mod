package io.github.gtbauke.unnamedtechmod.datagen;

import com.mojang.logging.LogUtils;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractAlloySmelterBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public class ModBlocksStateProvider extends BlockStateProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModBlocksStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, UnnamedTechMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.TIN_ORE.get());
        simpleBlockWithItem(ModBlocks.DEEPSLATE_TIN_ORE.get());

        simpleBlockWithItem(ModBlocks.RAW_TIN_BLOCK.get());
        simpleBlockWithItem(ModBlocks.LIGHT_BRICKS.get());

        machineBlock(ModBlocks.BASIC_ALLOY_SMELTER.get(), AbstractAlloySmelterBlock.LIT);
        machineBlockWithBottom(ModBlocks.MANUAL_MACERATOR.get(), AbstractMaceratorBlock.WORKING);
    }

    private void machineBlockWithBottom(Block block, BooleanProperty workingProperty) {
        var registryName = ForgeRegistries.BLOCKS.getKey(block).getPath();

        ModelFile defaultStateFile = models().orientableWithBottom(
                registryName,
                modTexture(registryName + "_side"),
                modTexture(registryName + "_front"),
                modTexture(registryName + "_bottom"),
                modTexture(registryName + "_top")
        );

        // TODO needs a more generic way for animated textures
        ModelFile activeStateFile = models().orientableWithBottom(
                registryName + "_on",
                modTexture(registryName + "_side"),
                modTexture(registryName + "_front_on"),
                modTexture(registryName + "_bottom"),
                modTexture(registryName + "_top_on")
        );

        getVariantBuilder(block)
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.EAST)
                .with(workingProperty, false)
                .modelForState()
                .rotationY(90)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.WEST)
                .with(workingProperty, false)
                .modelForState()
                .rotationY(270)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.NORTH)
                .with(workingProperty, false)
                .modelForState()
                .rotationY(0)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.SOUTH)
                .with(workingProperty, false)
                .modelForState()
                .rotationY(180)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.EAST)
                .with(workingProperty, true)
                .modelForState()
                .rotationY(90)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.WEST)
                .with(workingProperty, true)
                .modelForState()
                .rotationY(270)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.NORTH)
                .with(workingProperty, true)
                .modelForState()
                .rotationY(0)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.SOUTH)
                .with(workingProperty, true)
                .modelForState()
                .rotationY(180)
                .modelFile(activeStateFile)
                .addModel();

        itemOnly(block);
    }

    private void machineBlock(Block block, BooleanProperty workingProperty) {
        var registryName = ForgeRegistries.BLOCKS.getKey(block).getPath();

        ModelFile defaultStateFile = models().orientable(
                registryName,
                modTexture(registryName + "_side"),
                modTexture(registryName + "_front"),
                modTexture(registryName + "_top")
        );

        ModelFile activeStateFile = models().orientable(
                registryName + "_on",
                modTexture(registryName + "_side"),
                modTexture(registryName + "_front_on"),
                modTexture(registryName + "_top")
        );

        getVariantBuilder(block)
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.EAST)
                .with(workingProperty, false)
                .modelForState()
                .rotationY(90)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.WEST)
                .with(workingProperty, false)
                .modelForState()
                .rotationY(270)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.NORTH)
                .with(workingProperty, false)
                .modelForState()
                .rotationY(0)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.SOUTH)
                .with(workingProperty, false)
                .modelForState()
                .rotationY(180)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.EAST)
                .with(workingProperty, true)
                .modelForState()
                .rotationY(90)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.WEST)
                .with(workingProperty, true)
                .modelForState()
                .rotationY(270)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.NORTH)
                .with(workingProperty, true)
                .modelForState()
                .rotationY(0)
                .modelFile(activeStateFile)
                .addModel()
                .partialState()
                .with(AbstractAlloySmelterBlock.FACING, Direction.SOUTH)
                .with(workingProperty, true)
                .modelForState()
                .rotationY(180)
                .modelFile(activeStateFile)
                .addModel();

        itemOnly(block);
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
