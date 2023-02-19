package io.github.gtbauke.unnamedtechmod.init;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.BasicAlloySmelterBlock;
import io.github.gtbauke.unnamedtechmod.block.BasicHeaterBlock;
import io.github.gtbauke.unnamedtechmod.block.BasicPressBlock;
import io.github.gtbauke.unnamedtechmod.block.ManualMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractAlloySmelterBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, UnnamedTechMod.MOD_ID);

    public static final RegistryObject<Block> TIN_ORE = register(
            "tin_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2f, 18f)
                    .requiresCorrectToolForDrops()),
            new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
    );

    public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = register(
            "deepslate_tin_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2.7f, 21f)
                    .requiresCorrectToolForDrops()),
            new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
    );

    public static final RegistryObject<Block> RAW_TIN_BLOCK = register(
            "raw_tin_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2f, 18f)
                    .requiresCorrectToolForDrops()),
            new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
    );

    public static final RegistryObject<Block> LIGHT_BRICKS = register(
            "light_bricks",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2f, 18f)
                    .requiresCorrectToolForDrops()),
            new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
    );

    public static final RegistryObject<BasicAlloySmelterBlock> BASIC_ALLOY_SMELTER = register(
            "basic_alloy_smelter",
            () -> new BasicAlloySmelterBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2.5f, 20f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AbstractAlloySmelterBlock.LIT) ? 13 : 0)),
            new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
    );

    public static final RegistryObject<ManualMaceratorBlock> MANUAL_MACERATOR = register(
            "manual_macerator",
            () -> new ManualMaceratorBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2.5f, 20f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AbstractMaceratorBlock.WORKING) ? 2 : 0)),
            new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
    );

    public static final RegistryObject<BasicHeaterBlock> BASIC_HEATER = register(
            "basic_heater",
            () -> new BasicHeaterBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2.5f, 20f)
                    .requiresCorrectToolForDrops().noOcclusion()),
            new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
    );

    public static final RegistryObject<BasicPressBlock> BASIC_PRESS = register(
            "basic_press",
            () -> new BasicPressBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2.5f, 20f)
                    .requiresCorrectToolForDrops().noOcclusion()),
            new Item.Properties().tab(UnnamedTechMod.MOD_TAB)
    );

    public static final RegistryObject<Block> LEAD_ORE = registerOre("lead", () ->
            new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2f, 18f)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = registerOre("deepslate_lead", () ->
            new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2.7f, 18f)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> RAW_LEAD_BLOCK = register("raw_lead_block", () -> new Block(
            BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2, 18)
                    .requiresCorrectToolForDrops()
    ), new Item.Properties().tab(UnnamedTechMod.MOD_TAB));

    private static <T extends Block>RegistryObject<T> register(String name, Supplier<T> supplier, Item.Properties properties) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));

        return block;
    }

    private static <T extends Block>RegistryObject<T> registerOre(String name, Supplier<T> supplier) {
        String finalName = name + "_ore";
        return register(finalName, supplier, new Item.Properties().tab(UnnamedTechMod.MOD_TAB));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
