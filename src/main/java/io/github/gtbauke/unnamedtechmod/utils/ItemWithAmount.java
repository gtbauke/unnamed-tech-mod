package io.github.gtbauke.unnamedtechmod.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ItemWithAmount {
    private final Item item;
    private final int amount;

    public ItemWithAmount(ItemLike item, int amount) {
        this.item = item.asItem();
        this.amount = amount;
    }

    public Ingredient asIngredient() {
        return Ingredient.of(item);
    }

    public int getCount() { return amount; }

    public Item getItem() { return item; }
}
