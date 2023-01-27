package io.github.gtbauke.unnamedtechmod.block.entity.base;

import io.github.gtbauke.unnamedtechmod.recipe.AbstractAlloySmeltingRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractMaceratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class MaceratorTileBase extends TileEntityInventory implements
        RecipeHolder,
        StackedContentsCompatible {

    public static final int INPUT = 0;
    public static final int OUTPUT = 1;

    public static final int DATA_CRUSHING_TIME = 0;
    public static final int DATA_CRUSHING_DURATION = 1;
    public static final int DATA_CRUSHING_PROGRESS = 2;
    public static final int DATA_CRUSHING_TOTAL_TIME = 3;
    public static final int CRUSH_TIME_STANDARD = 200;
    public static final int INVENTORY_SIZE = 2;

    protected int crushingTime;
    protected int crushingDuration;
    protected int crushingProgress;
    protected int crushingTotalTime;
    public final RecipeType<? extends AbstractMaceratorRecipe> recipeType;

    protected final ContainerData dataAccess;


    public MaceratorTileBase(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractMaceratorRecipe> recipeType) {
        super(tileEntityType, pos, state, INVENTORY_SIZE);
        this.recipeType = recipeType;
        this.dataAccess = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case DATA_CRUSHING_TIME -> MaceratorTileBase.this.crushingTime;
                    case DATA_CRUSHING_DURATION -> MaceratorTileBase.this.crushingDuration;
                    case DATA_CRUSHING_PROGRESS -> MaceratorTileBase.this.crushingProgress;
                    case DATA_CRUSHING_TOTAL_TIME -> MaceratorTileBase.this.crushingTotalTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case DATA_CRUSHING_TIME -> MaceratorTileBase.this.crushingTime = pValue;
                    case DATA_CRUSHING_DURATION -> MaceratorTileBase.this.crushingDuration = pValue;
                    case DATA_CRUSHING_PROGRESS -> MaceratorTileBase.this.crushingProgress = pValue;
                    case DATA_CRUSHING_TOTAL_TIME -> MaceratorTileBase.this.crushingTotalTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public void setCustomName(Component name) {
        this.name = name;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == INPUT;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        if (pSlot == OUTPUT) {
            return;
        }

        ItemStack itemStack = itemStackHandler.getStackInSlot(pSlot);
        boolean flag = !pStack.isEmpty() && pStack.sameItem(itemStack) && ItemStack.tagMatches(pStack, itemStack);

        itemStackHandler.setStackInSlot(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        if (pSlot == INPUT && !flag) {
            crushingProgress = 0;
            setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return pIndex == INPUT;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

    @Override
    public void clearContent() {

    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {

    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void fillStackedContents(StackedContents pHelper) {

    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MaceratorTileBase pEntity) {

    }
}
