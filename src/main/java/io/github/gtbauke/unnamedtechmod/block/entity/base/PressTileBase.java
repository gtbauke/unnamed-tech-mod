package io.github.gtbauke.unnamedtechmod.block.entity.base;

import io.github.gtbauke.unnamedtechmod.block.BasicPressBlock;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.WrappedHandler;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractAlloySmeltingRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractMaceratorRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractPressRecipe;
import io.github.gtbauke.unnamedtechmod.utils.Constants;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
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
    public static final int DATA_LIT_DURATION = 2;
    public static final int DATA_PRESSING_DURATION = 3;
    public static final int DATA_PRESSING_PROGRESS = 4;
    public static final int DATA_PRESSING_TOTAL_TIME = 5;
    public static final int DATA_TEMPERATURE = 6;
    public static final int PRESSING_TIME_STANDARD = 200;
    public static final int BURN_TIME_STANDARD = 1600;

    public static final int INVENTORY_SIZE = 3;

    protected int pressingTime;
    protected int temperature;
    protected int litTime;
    protected int litDuration;
    protected int pressingDuration;
    protected int pressingProgress;
    protected int pressingTotalTime;
    protected int ticksSinceFuelAddedOrRemoved;

    protected Item lastFuelItem = Items.AIR;

    protected int fuelMaxTemp = 200;

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
                    case DATA_LIT_DURATION -> PressTileBase.this.litDuration;
                    case DATA_PRESSING_DURATION -> PressTileBase.this.pressingDuration;
                    case DATA_PRESSING_PROGRESS -> PressTileBase.this.pressingProgress;
                    case DATA_PRESSING_TOTAL_TIME -> PressTileBase.this.pressingTotalTime;
                    case DATA_TEMPERATURE -> PressTileBase.this.temperature;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case DATA_PRESSING_TIME -> PressTileBase.this.pressingTime = pValue;
                    case DATA_LIT_TIME -> PressTileBase.this.litTime = pValue;
                    case DATA_LIT_DURATION -> PressTileBase.this.litDuration = pValue;
                    case DATA_PRESSING_DURATION -> PressTileBase.this.pressingDuration = pValue;
                    case DATA_PRESSING_PROGRESS -> PressTileBase.this.pressingProgress = pValue;
                    case DATA_PRESSING_TOTAL_TIME -> PressTileBase.this.pressingTotalTime = pValue;
                    case DATA_TEMPERATURE -> PressTileBase.this.temperature = pValue;
                }
            }

            @Override
            public int getCount() {
                return 7;
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
        return recipe == null ? PRESSING_TIME_STANDARD : recipe.getProcessingTime();
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return false;
    }

    @Override
    public void clearContent() {

    }

    public boolean isPressing() {
        return pressingTime > 0;
    }

    public boolean isLit() {
        return litTime > 0;
    }

    public int getRecipeMinTemp() {
        AbstractPressRecipe recipe = getRecipe(getItem(INPUT));
        return recipe == null ? 0 : recipe.getMinTemp();
    }

    public boolean canPress() {
        AbstractPressRecipe recipe = getRecipe(getItem(INPUT));

        if (recipe == null) {
            return false;
        }

        ItemStack output = recipe.getResultItem();
        ItemStack outputSlot = getItem(OUTPUT);

        boolean aboveTemp = temperature >= recipe.getMinTemp();
        boolean inputValid = getItem(INPUT).getCount() >= recipe.getIngredientAmount();

        return outputSlot.isEmpty() || outputSlot.is(output.getItem()) && outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize() && aboveTemp;
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
        if (!pState.getValue(BasicPressBlock.CONNECTED_TO_HEATER)) return;

        boolean wasLit = pEntity.isLit();
        boolean isDirty = false;

        float a = -0.01f;
        float b = 0.2f;
        float g = 10; // -b/2a

        if (wasLit) {
            --pEntity.litTime;
            int x = ++pEntity.ticksSinceFuelAddedOrRemoved;

            if (x > g * pEntity.fuelMaxTemp) {
                pEntity.temperature = pEntity.fuelMaxTemp;
            } else {
                float normalizedX = (float) x / pEntity.fuelMaxTemp;
                pEntity.temperature = (int)((a * normalizedX * normalizedX + b * normalizedX) * pEntity.fuelMaxTemp);
            }
        }
        else {
            int x = ++pEntity.ticksSinceFuelAddedOrRemoved;

            if (x > g * pEntity.fuelMaxTemp) {
                pEntity.temperature = 0;
            }
            else {
                float normalizedX = ((float) x / pEntity.fuelMaxTemp) - g;
                pEntity.temperature = (int) (-a * normalizedX * normalizedX * pEntity.fuelMaxTemp);
            }
        }

        ItemStack fuelSlot = pEntity.getItem(FUEL);
        boolean hasFuel = !fuelSlot.isEmpty();

        ItemStack fuel = pEntity.getItem(FUEL);
        if ((pEntity.isLit() || !fuel.isEmpty())) {
            AbstractPressRecipe recipe = pEntity.getRecipe(pEntity.getItem(INPUT));

            if (!pEntity.isLit()) {
                pEntity.litTime = ForgeHooks.getBurnTime(fuel, pEntity.recipeType);
                pEntity.litDuration = pEntity.litTime;

                if (pEntity.isLit()) {
                    isDirty = true;

                    if (fuelSlot.hasCraftingRemainingItem()) {
                        ItemStack itemStack = fuelSlot.getCraftingRemainingItem();
                        pEntity.itemStackHandler.setStackInSlot(FUEL, itemStack);
                        Item item = itemStack.getItem();
                        if (item != pEntity.lastFuelItem)
                        {
                            pEntity.lastFuelItem = item;
                            pEntity.fuelMaxTemp = getMaxTempForFuel(item);
                            pEntity.ticksSinceFuelAddedOrRemoved = 0;
                        }
                    } else if (hasFuel) {
                        Item item = fuelSlot.getItem();
                        if (item != pEntity.lastFuelItem)
                        {
                            pEntity.lastFuelItem = item;
                            pEntity.fuelMaxTemp = getMaxTempForFuel(item);
                            pEntity.ticksSinceFuelAddedOrRemoved = 0;
                        }
                        fuelSlot.shrink(1);

                        if (fuelSlot.isEmpty()) {
                            pEntity.itemStackHandler.setStackInSlot(FUEL, fuelSlot.getCraftingRemainingItem());
                        }
                    }
                    else {
                        pEntity.ticksSinceFuelAddedOrRemoved = 0;
                    }
                }
            }

            if (pEntity.isLit() && pEntity.canPress()) {
                ++pEntity.pressingProgress;

                if (pEntity.pressingProgress >= pEntity.pressingTotalTime) {
                    pEntity.pressingProgress = 0;
                    pEntity.pressingTotalTime = getTotalCookingTime(pLevel, pEntity);

                    ItemStack output = recipe.getResultItem();
                    ItemStack outputSlot = pEntity.getItem(OUTPUT);
                    ItemStack input = pEntity.getItem(INPUT);

                    boolean outputSlotEmpty = outputSlot.isEmpty();

                    if (outputSlotEmpty) {
                        pEntity.setItem(OUTPUT, output.copy());
                    } else if (outputSlot.is(output.getItem())) {
                        outputSlot.grow(output.getCount());
                    }

                    input.shrink(recipe.getIngredientAmount());

                    isDirty = true;
                }
            } else {
                pEntity.pressingProgress = 0;
                pEntity.temperature = Math.max(pEntity.temperature - 1, 0);
            }
        } else if (!pEntity.isLit() && pEntity.pressingProgress > 0) {
            pEntity.pressingProgress = Mth.clamp(pEntity.pressingProgress - 2, 0, pEntity.pressingTotalTime);
        }

        if (wasLit != pEntity.isLit()) {
            isDirty = true;
            //pLevel.setBlock(pPos, pState.setValue(BasicPressBlock, pEntity.isLit()), 3);
        }

        if (isDirty) {
            pEntity.setChanged();
        }
    }

    private static int getMaxTempForFuel(Item item) {
        if (item == Items.COAL || item == Items.CHARCOAL || item == Items.COAL_BLOCK) {
            return Constants.COAL_BURN_TEMP;
        }
        else if (item == Items.PAPER || item == Items.BOOK || item == Items.BOOKSHELF) {
            return Constants.PAPER_BURN_TEMP;
        }
        else if (item == Items.LAVA_BUCKET || item == Items.BLAZE_ROD || item == Items.BLAZE_POWDER) {
            return Constants.LAVA_BURN_TEMP;
        }

        return Constants.WOOD_BURN_TEMP;
    }
}