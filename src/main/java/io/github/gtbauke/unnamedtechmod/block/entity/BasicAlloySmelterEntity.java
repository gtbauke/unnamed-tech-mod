package io.github.gtbauke.unnamedtechmod.block.entity;

import io.github.gtbauke.unnamedtechmod.block.entity.base.AlloySmelterTileBase;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractAlloySmeltingRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import io.github.gtbauke.unnamedtechmod.screen.BasicAlloySmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BasicAlloySmelterEntity extends AlloySmelterTileBase {
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

    public BasicAlloySmelterEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BASIC_ALLOY_SMELTER.get(), pPos, pBlockState, BasicAlloySmelterRecipe.Type.INSTANCE);
    }

    @Override
    protected BasicAlloySmelterRecipe getRecipe(ItemStack itemLeft, ItemStack itemRight, ItemStack alloyCompound) {
        if (itemLeft.getItem() instanceof AirItem && itemRight.getItem() instanceof  AirItem) {
            return null;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        BasicAlloySmelterRecipe recipe = recipeManager.getRecipeFor(
                BasicAlloySmelterRecipe.Type.INSTANCE,
                new SimpleContainer(itemLeft, itemRight, alloyCompound),
                level
        ).orElse(null);

        return recipe;
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
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

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.basic_alloy_smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BasicAlloySmelterMenu(pContainerId, pPlayerInventory, this, this.dataAccess);
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}
