package io.github.gtbauke.unnamedtechmod.block.entity;

import io.github.gtbauke.unnamedtechmod.block.BasicPressBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.base.PressTileBase;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractPressRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.BasicPressRecipe;
import io.github.gtbauke.unnamedtechmod.screen.BasicPressMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BasicPressEntity extends PressTileBase {
    public BasicPressEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_PRESS.get(), pos, state, BasicPressRecipe.Type.INSTANCE);
    }

    public void crankUsed() {
        if (!this.canPress()) return;

        if (!this.isPressing()) {
            BlockPos pos = this.worldPosition;
            BlockState state = level.getBlockState(pos).setValue(BasicPressBlock.WORKING, true);
            this.level.setBlock(pos, state, Block.UPDATE_ALL);
            setChanged(this.level, pos, state);
        }

        this.pressingTime = 30;
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
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BasicPressMenu(pContainerId, pPlayerInventory, this, this.dataAccess);
    }

    @Override
    public void fillStackedContents(StackedContents pHelper) {

    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.basic_press");
    }

    @Override
    protected AbstractPressRecipe getRecipe(ItemStack input) {
        if (input.getItem() instanceof AirItem) {
            return null;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        AbstractPressRecipe recipe = recipeManager.getRecipeFor(
                BasicPressRecipe.Type.INSTANCE,
                new SimpleContainer(input),
                level
        ).orElse(null);

        return recipe;
    }
}
