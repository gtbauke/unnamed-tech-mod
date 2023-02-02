package io.github.gtbauke.unnamedtechmod.datagen;

import com.mojang.logging.LogUtils;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.BasicHeaterBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractAlloySmelterBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMachineBlock;
import io.github.gtbauke.unnamedtechmod.block.properties.HeaterType;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public class ModBlocksStateProvider extends BlockStateProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModBlocksStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, UnnamedTechMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels(){
        simpleBlockWithItem(ModBlocks.TIN_ORE.get());
        simpleBlockWithItem(ModBlocks.DEEPSLATE_TIN_ORE.get());

        simpleBlockWithItem(ModBlocks.RAW_TIN_BLOCK.get());
        simpleBlockWithItem(ModBlocks.LIGHT_BRICKS.get());

        machineBlock(ModBlocks.BASIC_ALLOY_SMELTER.get(), AbstractAlloySmelterBlock.LIT);
        machineBlockWithBottom(ModBlocks.MANUAL_MACERATOR.get(), AbstractMaceratorBlock.WORKING);

        ModelFile.ExistingModelFile defaultStateFile = models().getExistingFile(new ResourceLocation(UnnamedTechMod.MOD_ID, "block/basic_heater"));

        var registryName = ForgeRegistries.BLOCKS.getKey(ModBlocks.BASIC_HEATER.get()).getPath();
        ModelFile connectedToPressStateFile = models().orientable(
                registryName + "_connected_to_press",
                modTexture("heater_press_connected"),
                modTexture("heater_press_connected_front"),
                modTexture("manual_macerator_top"));

        getVariantBuilder(ModBlocks.BASIC_HEATER.get())
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.EAST)
                .with(BasicHeaterBlock.TYPE, HeaterType.UNCONNECTED)
                .modelForState()
                .rotationY(90)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.WEST)
                .with(BasicHeaterBlock.TYPE, HeaterType.UNCONNECTED)
                .modelForState()
                .rotationY(270)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.NORTH)
                .with(BasicHeaterBlock.TYPE, HeaterType.UNCONNECTED)
                .modelForState()
                .rotationY(0)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.SOUTH)
                .with(BasicHeaterBlock.TYPE, HeaterType.UNCONNECTED)
                .modelForState()
                .rotationY(180)
                .modelFile(defaultStateFile)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.EAST)
                .with(BasicHeaterBlock.TYPE, HeaterType.CONNECTED_TO_PRESS)
                .modelForState()
                .rotationY(90)
                .modelFile(connectedToPressStateFile)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.WEST)
                .with(BasicHeaterBlock.TYPE, HeaterType.CONNECTED_TO_PRESS)
                .modelForState()
                .rotationY(270)
                .modelFile(connectedToPressStateFile)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.NORTH)
                .with(BasicHeaterBlock.TYPE, HeaterType.CONNECTED_TO_PRESS)
                .modelForState()
                .rotationY(0)
                .modelFile(connectedToPressStateFile)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.SOUTH)
                .with(BasicHeaterBlock.TYPE, HeaterType.CONNECTED_TO_PRESS)
                .modelForState()
                .rotationY(180)
                .modelFile(connectedToPressStateFile)
                .addModel();

        itemOnly(ModBlocks.BASIC_HEATER.get());

        var registryName2 = ForgeRegistries.BLOCKS.getKey(ModBlocks.BASIC_PRESS.get()).getPath();
        ModelFile defaultStateFile2 = models().orientable(
                registryName2,
                modTexture(registryName2 + "_side"),
                modTexture(registryName2 + "_front"),
                modTexture(registryName2 + "_top")
        );

        getVariantBuilder(ModBlocks.BASIC_PRESS.get())
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.EAST)
                .modelForState()
                .rotationY(90)
                .modelFile(defaultStateFile2)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.WEST)
                .modelForState()
                .rotationY(270)
                .modelFile(defaultStateFile2)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.NORTH)
                .modelForState()
                .rotationY(0)
                .modelFile(defaultStateFile2)
                .addModel()
                .partialState()
                .with(AbstractMachineBlock.FACING, Direction.SOUTH)
                .modelForState()
                .rotationY(180)
                .modelFile(defaultStateFile2)
                .addModel();

        itemOnly(ModBlocks.BASIC_PRESS.get());
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
