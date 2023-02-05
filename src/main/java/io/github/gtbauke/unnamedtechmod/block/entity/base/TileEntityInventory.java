package io.github.gtbauke.unnamedtechmod.block.entity.base;

import io.github.gtbauke.unnamedtechmod.utils.AlloySmelting;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TileEntityInventory extends BlockEntity implements
        WorldlyContainer,
        MenuProvider,
        Nameable {
    public ItemStackHandler itemStackHandler;
    public int containerSize;
    protected Component name;

    protected LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    public TileEntityInventory(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, int inventorySize) {
        super(tileEntityType, pos, state);
        this.containerSize = inventorySize;
    }

    @Override
    public abstract int[] getSlotsForFace(Direction pSide);

    @Override
    public abstract boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection);

    @Override
    public abstract boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection);

    public abstract boolean isItemValidForSlot(int index, ItemStack stack);

    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    public void setHandler(ItemStackHandler handler) {
        copyHandlerContents(handler);
    }

    public void setCustomName(Component name) {
        this.name = name;
    }

    private void copyHandlerContents(ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            itemStackHandler.setStackInSlot(i, handler.getStackInSlot(i));
        }
    }

    public void dropContents() {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            ItemStack stack = getItem(i);

            Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), stack);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public int getContainerSize() {
        return containerSize;
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < itemStackHandler.getSlots(); i++) {
            ItemStack itemStack = itemStackHandler.getStackInSlot(i);

            if (!itemStack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return itemStackHandler.getStackInSlot(pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        itemStackHandler.setStackInSlot(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (level.getBlockEntity(worldPosition) != this) {
            return false;
        }

        return !(pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
    }

    @Override
    public abstract boolean canPlaceItem(int pIndex, ItemStack pStack);

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public abstract AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer);

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        load(tag);
        setChanged();

        level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition),
                level.getBlockState(worldPosition).getBlock().defaultBlockState(),
                level.getBlockState(worldPosition), 2, 3);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        setChanged();
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);

        return tag;
    }

    @Override
    public int getMaxStackSize() {
        return WorldlyContainer.super.getMaxStackSize();
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemStackHandler.deserializeNBT(pTag.getCompound("inventory"));
    }

    public CompoundTag save(CompoundTag tag) {
        super.saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemStackHandler.serializeNBT());
        super.saveAdditional(pTag);
    }
}
