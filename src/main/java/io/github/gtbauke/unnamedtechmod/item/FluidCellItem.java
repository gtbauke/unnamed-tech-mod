package io.github.gtbauke.unnamedtechmod.item;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class FluidCellItem extends BucketItem {
    public FluidCellItem(Supplier<? extends Fluid> supplier, Properties builder) {
        super(supplier, builder);
    }
}
