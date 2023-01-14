package io.github.gtbauke.unnamedtechmod.init;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
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
            new Item.Properties().tab(CreativeModeTab.TAB_MISC)
    );

    public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = register(
            "deepslate_tin_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(2.7f, 21f)
                    .requiresCorrectToolForDrops()),
            new Item.Properties().tab(CreativeModeTab.TAB_MISC)
    );

    private static <T extends Block>RegistryObject<T> register(String name, Supplier<T> supplier, Item.Properties properties) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));

        return block;
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
