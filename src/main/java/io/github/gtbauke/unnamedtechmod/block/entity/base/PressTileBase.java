package io.github.gtbauke.unnamedtechmod.block.entity.base;

import io.github.gtbauke.unnamedtechmod.block.BasicPressBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.WrappedHandler;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractAlloySmeltingRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractPressRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class PressTileBase extends TileEntityInventory implements
        RecipeHolder,
        StackedContentsCompatible {
    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    public static final int FUEL = 2;

    public static final int DATA_PRESSING_TIME = 0;
    public static final int DATA_LIT_TIME = 1;
    public static final int DATA_PRESSING_DURATION = 2;
    public static final int DATA_PRESSING_PROGRESS = 3;
    public static final int DATA_PRESSING_TOTAL_TIME = 4;
    public static final int PRESSING_TIME_STANDARD = 200;
    public static final int BURN_TIME_STANDARD = 1600;

    public static final int INVENTORY_SIZE = 3;

    protected int pressingTime;
    protected int litTime;
    protected int pressingDuration;
    protected int pressingProgress;
    protected int pressingTotalTime;

    public final RecipeType<? extends AbstractPressRecipe> recipeType;
    protected final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    protected final ContainerData dataAccess;

    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() ->
                            new WrappedHandler(itemStackHandler, (i) -> i == OUTPUT,
                                    (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() ->
                            new WrappedHandler(itemStackHandler, (index) -> index == INPUT,
                                    (index, stack) -> itemStackHandler.isItemValid(INPUT, stack))),
                    Direction.SOUTH, LazyOptional.of(() ->
                            new WrappedHandler(itemStackHandler, (index) -> index == INPUT,
                                    (index, stack) -> itemStackHandler.isItemValid(INPUT, stack))),
                    Direction.EAST, LazyOptional.of(() ->
                            new WrappedHandler(itemStackHandler, (index) -> index == INPUT,
                                    (index, stack) -> itemStackHandler.isItemValid(INPUT, stack))),
                    Direction.WEST, LazyOptional.of(() ->
                            new WrappedHandler(itemStackHandler, (index) -> index == INPUT,
                                    (index, stack) -> itemStackHandler.isItemValid(INPUT, stack))));

    public PressTileBase(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractPressRecipe> recipeType) {
        super(tileEntityType, pos, state, INVENTORY_SIZE);
        this.recipeType = recipeType;
        this.dataAccess = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case DATA_PRESSING_TIME -> PressTileBase.this.pressingTime;
                    case DATA_LIT_TIME -> PressTileBase.this.litTime;
                    case DATA_PRESSING_DURATION -> PressTileBase.this.pressingDuration;
                    case DATA_PRESSING_PROGRESS -> PressTileBase.this.pressingProgress;
                    case DATA_PRESSING_TOTAL_TIME -> PressTileBase.this.pressingTotalTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case DATA_PRESSING_TIME -> PressTileBase.this.pressingTime = pValue;
                    case DATA_LIT_TIME -> PressTileBase.this.litTime = pValue;
                    case DATA_PRESSING_DURATION -> PressTileBase.this.pressingDuration = pValue;
                    case DATA_PRESSING_PROGRESS -> PressTileBase.this.pressingProgress = pValue;
                    case DATA_PRESSING_TOTAL_TIME -> PressTileBase.this.pressingTotalTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
        this.itemStackHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return isItemValidForSlot(slot, stack);
            }
        };
    }

    protected abstract AbstractPressRecipe getRecipe(ItemStack input);

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == OUTPUT) {
            return false;
        }

        if (index == INPUT) {
            return true;
        }

        if (index == FUEL) {
            ItemStack itemStack = getItem(FUEL);
            return ForgeHooks.getBurnTime(stack, recipeType) > 0 || (
                    stack.getItem() == Items.BUCKET &&
                            itemStack.getItem() != Items.BUCKET);
        }

        return false;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return null;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemStack = itemStackHandler.getStackInSlot(pSlot);
        boolean flag = !pStack.isEmpty() && pStack.sameItem(itemStack) && ItemStack.tagMatches(pStack, itemStack);

        super.setItem(pSlot, pStack);

        if (pSlot == INPUT && !flag) {
            pressingTotalTime = getTotalCookingTime(level, this);
            pressingProgress = 0;
            setChanged();
        }
    }

    public static int getTotalCookingTime(Level pLevel, PressTileBase pBlockEntity) {
        ItemStack input = pBlockEntity.itemStackHandler.getStackInSlot(INPUT);

        AbstractPressRecipe recipe = pBlockEntity.getRecipe(input);
        return recipe == null ? PRESSING_TIME_STANDARD : 200;//recipe.getCookingTime();
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return false;
    }

    @Override
    public void clearContent() {

    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction facing) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(facing == null) {
                return lazyItemHandler.cast();
            }

            if(directionWrappedHandlerMap.containsKey(facing)) {
                Direction localDir = this.getBlockState().getValue(BasicPressBlock.FACING);

                if(facing == Direction.UP || facing == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(facing).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(facing.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(facing.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(facing).cast();
                    case WEST -> directionWrappedHandlerMap.get(facing.getCounterClockWise()).cast();
                };
            }
        }

        return super.getCapability(cap, facing);
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, PressTileBase pEntity) {
    }
}
