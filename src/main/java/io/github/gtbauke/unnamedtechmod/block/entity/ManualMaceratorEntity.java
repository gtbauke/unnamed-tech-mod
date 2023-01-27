package io.github.gtbauke.unnamedtechmod.block.entity;

import io.github.gtbauke.unnamedtechmod.block.ManualMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.base.MaceratorTileBase;
import io.github.gtbauke.unnamedtechmod.recipe.ManualMaceratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManualMaceratorEntity extends MaceratorTileBase {
    private ItemStackHandler itemStackHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isItemValidForSlot(slot, stack);
        }
    };

    public ManualMaceratorEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MANUAL_MACERATOR.get(), pos, state, ManualMaceratorRecipe.Type.INSTANCE);
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return switch (pSide){
            case UP -> new int[0];
            case DOWN -> new int[]{OUTPUT};
            default -> new int[]{INPUT};
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        if (pDirection == Direction.UP || pDirection == Direction.DOWN || pIndex == OUTPUT) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return pDirection == Direction.DOWN && pIndex == OUTPUT;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return itemStackHandler.extractItem(pSlot, pAmount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = itemStackHandler.getStackInSlot(pSlot);
        itemStackHandler.setStackInSlot(pSlot, ItemStack.EMPTY);
        return stack;
    }
}
