package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.utils.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, UnnamedTechMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.TIN_ORE.get())
                .add(ModBlocks.DEEPSLATE_TIN_ORE.get())
                .add(ModBlocks.LIGHT_BRICKS.get())
                .add(ModBlocks.BASIC_ALLOY_SMELTER.get())
                .add(ModBlocks.MANUAL_MACERATOR.get())
                .add(ModBlocks.LEAD_ORE.get())
                .add(ModBlocks.DEEPSLATE_LEAD_ORE.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(ModTags.Blocks.ORES_TIN)
                .add(ModBlocks.RAW_TIN_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .addTag(ModTags.Blocks.ORES_LEAD)
                .add(ModBlocks.RAW_LEAD_BLOCK.get());

        getBuilder(ModTags.Blocks.ORES_TIN)
                .add(ModBlocks.TIN_ORE.get())
                .add(ModBlocks.DEEPSLATE_TIN_ORE.get());

        getBuilder(ModTags.Blocks.ORES_LEAD)
                .add(ModBlocks.LEAD_ORE.get())
                .add(ModBlocks.DEEPSLATE_LEAD_ORE.get());

        getBuilder(Tags.Blocks.ORES)
                .addTag(ModTags.Blocks.ORES_TIN)
                .addTag(ModTags.Blocks.ORES_LEAD);

        getBuilder(ModTags.Blocks.STORAGE_BLOCKS_RAW_TIN)
                .add(ModBlocks.RAW_TIN_BLOCK.get());

        getBuilder(ModTags.Blocks.STORAGE_BLOCKS_RAW_LEAD)
                .add(ModBlocks.RAW_LEAD_BLOCK.get());
    }

    protected TagsProvider.TagAppender<Block> getBuilder(TagKey<Block> tag) {
        return tag(tag);
    }
}
