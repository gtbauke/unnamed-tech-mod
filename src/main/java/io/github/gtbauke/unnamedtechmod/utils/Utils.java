package io.github.gtbauke.unnamedtechmod.utils;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {
    public static String getCraftingRegistryPath(Item item) {
        String namespace = ForgeRegistries.ITEMS.getKey(item).getNamespace();
        String name = ForgeRegistries.ITEMS.getKey(item).getPath();

        return namespace + ":" + name;
    }
}
