package io.github.gtbauke.unnamedtechmod.datagen;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.utils.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, UnnamedTechMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        copy(ModTags.Blocks.ORES_TIN, ModTags.Items.ORES_TIN);
        copy(Tags.Blocks.ORES, Tags.Items.ORES);

        copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_TIN, ModTags.Items.STORAGE_BLOCKS_RAW_TIN);

        builder(ModTags.Items.RAW_MATERIALS_TIN, ModItems.RAW_TIN_ORE.get());
        builder(ModTags.Items.ALLOYS, ModItems.REDSTONE_COPPER_ALLOY.get());

        builder(ModTags.Items.DUSTS_TIN, ModItems.TIN_DUST.get());
        getBuilder(Tags.Items.DUSTS)
                .addTag(ModTags.Items.DUSTS_TIN);

        builder(ModTags.Items.PLATES_TIN, ModItems.TIN_PLATE.get());

        builder(ModTags.Items.INGOTS_TIN, ModItems.TIN_INGOT.get());
        getBuilder(Tags.Items.INGOTS)
                .addTag(ModTags.Items.INGOTS_TIN);
    }

    private void builder(TagKey<Item> tag, ItemLike... items) {
        getBuilder(tag).add(
                Arrays.stream(items).map(ItemLike::asItem).toArray(Item[]::new)
        );
    }

    protected TagsProvider.TagAppender<Item> getBuilder(TagKey<Item> tag) {
        return tag(tag);
    }
}
