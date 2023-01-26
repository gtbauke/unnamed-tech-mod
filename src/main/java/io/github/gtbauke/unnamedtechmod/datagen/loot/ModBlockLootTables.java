package io.github.gtbauke.unnamedtechmod.datagen.loot;

import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockLootTables extends BlockLoot {
    @Override
    protected void addTables() {
        add(ModBlocks.TIN_ORE.get(), block -> createOreDrop(
                block,
                ModItems.RAW_TIN_ORE.get()
        ));

        add(ModBlocks.DEEPSLATE_TIN_ORE.get(), block -> createOreDrop(
                block,
                ModItems.RAW_TIN_ORE.get()
        ));

        dropSelf(ModBlocks.RAW_TIN_BLOCK.get());
        dropSelf(ModBlocks.LIGHT_BRICKS.get());

        dropSelf(ModBlocks.BASIC_ALLOY_SMELTER.get());
        dropSelf(ModBlocks.MANUAL_MACERATOR.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
