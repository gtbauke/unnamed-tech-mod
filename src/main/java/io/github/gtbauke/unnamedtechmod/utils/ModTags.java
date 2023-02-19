package io.github.gtbauke.unnamedtechmod.utils;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final class Blocks {
        public static final TagKey<Block> ORES_TIN = forge("ores/tin");
        public static final TagKey<Block> ORES_LEAD = forge("ores/lead");

        public static final TagKey<Block> STORAGE_BLOCKS_RAW_TIN = forge("storage_blocks/raw_tin");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_LEAD = forge("storage_blocks/raw_lead");

        public static TagKey<Block> mod(String path) {
            return BlockTags.create(new ResourceLocation(UnnamedTechMod.MOD_ID, path));
        }

        public static TagKey<Block> forge(String path) {
            return BlockTags.create(new ResourceLocation("forge", path));
        }
    }

    public static final class Items {
        public static final TagKey<Item> ORES_TIN = forge("ores/tin");
        public static final TagKey<Item> ORES_LEAD = forge("ores/lead");
        public static final TagKey<Item> ALLOYS = mod("alloys/alloy");
        public static final TagKey<Item> ALLOY_COMPOUND = mod("alloys/compound");

        public static final TagKey<Item> STORAGE_BLOCKS_RAW_TIN = forge("storage_blocks/raw_tin");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_LEAD = forge("storage_blocks/raw_lead");

        public static final TagKey<Item> RAW_MATERIALS_TIN = forge("raw_materials/tin");
        public static final TagKey<Item> RAW_MATERIALS_LEAD = forge("raw_materials/lead");

        public static final TagKey<Item> DUSTS_TIN = forge("dusts/tin");
        public static final TagKey<Item> DUSTS_IRON = forge("dusts/iron");
        public static final TagKey<Item> DUSTS_GOLD = forge("dusts/gold");
        public static final TagKey<Item> DUSTS_COPPER = forge("dusts/copper");
        public static final TagKey<Item> DUSTS_BRONZE = forge("dusts/bronze");
        public static final TagKey<Item> DUSTS_LEAD = forge("dusts/lead");

        public static final TagKey<Item> PLATES_TIN = forge("plates/tin");
        public static final TagKey<Item> PLATES_IRON = forge("plates/iron");
        public static final TagKey<Item> PLATES_GOLD = forge("plates/gold");
        public static final TagKey<Item> PLATES_COPPER = forge("plates/copper");
        public static final TagKey<Item> PLATES_BRONZE = forge("plates/bronze");
        public static final TagKey<Item> PLATES_LEAD = forge("plates/lead");

        public static final TagKey<Item> INGOTS_TIN = forge("ingots/tin");
        public static final TagKey<Item> INGOTS_BRONZE = forge("ingots/bronze");
        public static final TagKey<Item> INGOTS_LEAD = forge("ingots/lead");

        public static TagKey<Item> mod(String path) {
            return ItemTags.create(new ResourceLocation(UnnamedTechMod.MOD_ID, path));
        }

        public static TagKey<Item> forge(String path) {
            return ItemTags.create(new ResourceLocation("forge", path));
        }
    }
}
