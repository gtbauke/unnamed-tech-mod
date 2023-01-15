package io.github.gtbauke.unnamedtechmod.block.entity;

import io.github.gtbauke.unnamedtechmod.block.BasicAlloySmelter;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import io.github.gtbauke.unnamedtechmod.screen.BasicAlloySmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.logging.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class BasicAlloySmelterEntity extends BlockEntity implements MenuProvider {
    private ItemStackHandler itemStackHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == Items.IRON_INGOT;
                case 1 -> false;
                case 2 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (index) -> index == 1,
                            (index, stack) -> itemStackHandler.isItemValid(1, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (i) -> i == 1,
                            (index, stack) -> itemStackHandler.isItemValid(1, stack))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (index) -> index == 0 || index == 1,
                            (index, stack) -> itemStackHandler.isItemValid(0, stack) || itemStackHandler.isItemValid(1, stack))));

    public BasicAlloySmelterEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BASIC_ALLOY_SMELTER.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> BasicAlloySmelterEntity.this.progress;
                    case 1 -> BasicAlloySmelterEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> BasicAlloySmelterEntity.this.progress = pValue;
                    case 1 -> BasicAlloySmelterEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.basic_alloy_smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BasicAlloySmelterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyItemHandler.cast();
            }

            if (directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(BasicAlloySmelter.FACING);

                if (side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                };
            }
        }

        return super.getCapability(cap);
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
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemStackHandler.serializeNBT());
        pTag.putInt("basic_alloy_smelter.progress", this.progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        itemStackHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("basic_alloy_smelter.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());

        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BasicAlloySmelterEntity pEntity) {
        if (level.isClientSide()) {
            return;
        }

        if (hasRecipe(pEntity)) {
            pEntity.progress++;
            setChanged(level, blockPos, blockState);

            if (pEntity.progress >= pEntity.maxProgress) {
                craftItem(pEntity);
            }
        } else {
            pEntity.resetProgress();
            setChanged(level, blockPos, blockState);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(BasicAlloySmelterEntity pEntity) {
        SimpleContainer inventory = new SimpleContainer(pEntity.itemStackHandler.getSlots());

        for (int i = 0; i < pEntity.itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, pEntity.itemStackHandler.getStackInSlot(i));
        }

        Optional<BasicAlloySmelterRecipe> recipe = pEntity.level.getRecipeManager()
                .getRecipeFor(BasicAlloySmelterRecipe.Type.INSTANCE, inventory, pEntity.level);

        if (hasRecipe(pEntity)) {
            pEntity.itemStackHandler.extractItem(0, 1, false);
            // pEntity.itemStackHandler.extractItem(1, 1, false);
            pEntity.itemStackHandler.setStackInSlot(2, new ItemStack(recipe.get().getResultItem().getItem(),
                    pEntity.itemStackHandler.getStackInSlot(2).getCount() + 1));

            pEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(BasicAlloySmelterEntity pEntity) {
        SimpleContainer inventory = new SimpleContainer(pEntity.itemStackHandler.getSlots());

        for (int i = 0; i < pEntity.itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, pEntity.itemStackHandler.getStackInSlot(i));
        }

        Optional<BasicAlloySmelterRecipe> recipe = pEntity.level.getRecipeManager()
                .getRecipeFor(BasicAlloySmelterRecipe.Type.INSTANCE, inventory, pEntity.level);

        return recipe.isPresent() && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemInOutputSlot(inventory, recipe.get().getResultItem());
    }

    private static boolean canInsertItemInOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(2).getItem() == itemStack.getItem()
                || inventory.getItem(2).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }
}
