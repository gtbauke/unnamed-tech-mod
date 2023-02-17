package io.github.gtbauke.unnamedtechmod.datagen;

import com.mojang.logging.LogUtils;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.BasicHeaterBlock;
import io.github.gtbauke.unnamedtechmod.block.BasicPressBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractAlloySmelterBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMachineBlock;
import io.github.gtbauke.unnamedtechmod.block.properties.HeaterType;
import io.github.gtbauke.unnamedtechmod.datagen.helpers.ExtendedVariantBlockStateBuilder;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.function.Function;

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

        // ==== BASIC HEATER ==== //

        ModelFile.ExistingModelFile basicHeaterDefaultStateFile = models().getExistingFile(new ResourceLocation(UnnamedTechMod.MOD_ID, "block/basic_heater"));

        var bHeaterRegistryName = ForgeRegistries.BLOCKS.getKey(ModBlocks.BASIC_HEATER.get()).getPath();
        ModelFile bHeaterConnectedToPressStateFile = models().orientable(
                bHeaterRegistryName + "_connected_to_press",
                modTexture("heater_press_connected"),
                modTexture("heater_press_connected_front"),
                modTexture("manual_macerator_top"));

        ModelFile bHeaterConnectedToPressStateFileLit = models().orientable(
                bHeaterRegistryName + "_connected_to_press_on",
                modTexture("heater_press_connected"),
                modTexture("heater_press_connected_front_on"),
                modTexture("manual_macerator_top"));

        ExtendedVariantBlockStateBuilder.getExtendedVariantBuilder(getVariantBuilder(ModBlocks.BASIC_HEATER.get()))
                .addModelForHorizontalDirectionsWith(
                        basicHeaterDefaultStateFile,
                        (p) -> p
                                .with(BasicHeaterBlock.TYPE, HeaterType.UNCONNECTED)
                                .with(BasicHeaterBlock.LIT, false)
                ).addModelForHorizontalDirectionsWith(
                        bHeaterConnectedToPressStateFile,
                        (p) -> p
                                .with(BasicHeaterBlock.TYPE, HeaterType.CONNECTED_TO_PRESS)
                                .with(BasicHeaterBlock.LIT, false)
                ).addModelForHorizontalDirectionsWith(
                        basicHeaterDefaultStateFile,
                        (p) -> p
                                .with(BasicHeaterBlock.TYPE, HeaterType.UNCONNECTED)
                                .with(BasicHeaterBlock.LIT, true)
                ).addModelForHorizontalDirectionsWith(
                        bHeaterConnectedToPressStateFileLit,
                        (p) -> p
                                .with(BasicHeaterBlock.TYPE, HeaterType.CONNECTED_TO_PRESS)
                                .with(BasicHeaterBlock.LIT, true)
                );

        itemOnly(ModBlocks.BASIC_HEATER.get());

        // ==== BASIC PRESS ==== //

        var bPressRegistryName = ForgeRegistries.BLOCKS.getKey(ModBlocks.BASIC_PRESS.get()).getPath();
        ModelFile bPressStateFile = models().orientable(
                bPressRegistryName,
                modTexture(bPressRegistryName + "_side"),
                modTexture(bPressRegistryName + "_front"),
                modTexture(bPressRegistryName + "_top")
        );

        ModelFile bPressStateFileOn = models().orientable(
                bPressRegistryName + "_on",
                modTexture(bPressRegistryName + "_side"),
                modTexture(bPressRegistryName + "_front_on"),
                modTexture(bPressRegistryName + "_top")
        );

        ExtendedVariantBlockStateBuilder.getExtendedVariantBuilder(getVariantBuilder(ModBlocks.BASIC_PRESS.get()))
                .addModelForHorizontalDirectionsWith(
                        bPressStateFile,
                        (p) -> p
                                .with(BasicPressBlock.WORKING, false)
                ).addModelForHorizontalDirectionsWith(
                        bPressStateFileOn,
                        (p) -> p
                                .with(BasicPressBlock.WORKING, true)
                );

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

        ExtendedVariantBlockStateBuilder.getExtendedVariantBuilder(getVariantBuilder(block))
                .addModelForHorizontalDirectionsWith(
                        defaultStateFile,
                        (p) -> p
                                .with(workingProperty, false)
                ).addModelForHorizontalDirectionsWith(
                        activeStateFile,
                        (p) -> p
                                .with(workingProperty, true)
                );

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

        ExtendedVariantBlockStateBuilder.getExtendedVariantBuilder(getVariantBuilder(block))
                .addModelForHorizontalDirectionsWith(
                        defaultStateFile,
                        (p) -> p
                                .with(workingProperty, false)
                ).addModelForHorizontalDirectionsWith(
                        activeStateFile,
                        (p) -> p
                                .with(workingProperty, true)
                );

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
