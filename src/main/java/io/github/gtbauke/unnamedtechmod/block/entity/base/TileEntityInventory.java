package io.github.gtbauke.unnamedtechmod.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class TileEntityInventory extends BlockEntity implements
        WorldlyContainer,
        MenuProvider,
        Nameable {
    public NonNullList<ItemStack> inventory;
    protected Component name;

    public TileEntityInventory(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, int inventorySize) {
        super(tileEntityType, pos, state);
        inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    public abstract int[] getSlotsForFace(Direction pSide);

    @Override
    public abstract boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection);

    @Override
    public abstract boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection);

    public abstract boolean isItemValidForSlot(int index, ItemStack stack);

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return inventory.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(inventory, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(inventory, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemStack = this.inventory.get(pSlot);
        boolean flag = !pStack.isEmpty() && pStack.sameItem(itemStack) && ItemStack.tagMatches(pStack, itemStack);

        inventory.set(pSlot, pStack);
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
    public void clearContent() {
        inventory.clear();
    }

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

        inventory = NonNullList.withSize(getMaxStackSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, inventory);

        if (pTag.contains("CustomName", 8)) {
            name = Component.Serializer.fromJson(pTag.getString("CustomName"));
        }
    }

    public CompoundTag save(CompoundTag tag) {
        super.saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (name != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(name));
        }

        ContainerHelper.saveAllItems(pTag, inventory);
    }
}
