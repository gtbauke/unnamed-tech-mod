package io.github.gtbauke.unnamedtechmod.datagen.custom.helpers;

import io.github.gtbauke.unnamedtechmod.utils.ItemWithAmount;
import net.minecraft.world.item.Item;

public class AlloySmeltingData {
    public Item result;
    public ItemWithAmount left;
    public ItemWithAmount right;
    public int alloyCompoundAmount;
    public int count;
    public int cookingTime;
    public float experience;

    public AlloySmeltingData() {}

    public AlloySmeltingData(Item result, ItemWithAmount left, ItemWithAmount right, int alloyCompoundAmount, int count, int cookingTime, float experience) {
        this.result = result;
        this.left = left;
        this.right = right;
        this.alloyCompoundAmount = alloyCompoundAmount;
        this.count = count;
        this.cookingTime = cookingTime;
        this.experience = experience;
    }
}