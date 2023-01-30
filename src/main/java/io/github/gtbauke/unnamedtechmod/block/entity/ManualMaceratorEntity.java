package io.github.gtbauke.unnamedtechmod.block.entity;

import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.base.MaceratorTileBase;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractMaceratorRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.ManualMaceratorRecipe;
import io.github.gtbauke.unnamedtechmod.screen.ManualMaceratorMenu;
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
import net.minecraft.world.level.block.Block;
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

    public void crankUsed() {
        if (!this.canCrush()) return;

        if (!this.isCrushing()) {
            BlockPos pos = this.worldPosition;
            BlockState state = level.getBlockState(pos).setValue(AbstractMaceratorBlock.WORKING, true);
            this.level.setBlock(pos, state, 3);
            setChanged(this.level, pos, state);
        }

        this.crushingTime = 30;
    }

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

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.manual_macerator");
    }

    @Override
    protected AbstractMaceratorRecipe getRecipe(ItemStack itemLeft) {
        if (itemLeft.getItem() instanceof AirItem) {
            return null;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        ManualMaceratorRecipe recipe = recipeManager.getRecipeFor(
                ManualMaceratorRecipe.Type.INSTANCE,
                new SimpleContainer(itemLeft),
                level
        ).orElse(null);

        return recipe;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ManualMaceratorMenu(pContainerId, pPlayerInventory, this, this.dataAccess);
    }

}
