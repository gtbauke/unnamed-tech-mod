package io.github.gtbauke.unnamedtechmod.block.entity.base;

import com.google.common.collect.Lists;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.WrappedHandler;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractMaceratorRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
    protected int crushingProgress = 100;
    protected int crushingTotalTime = 200;
    public final RecipeType<? extends AbstractMaceratorRecipe> recipeType;

    protected final ContainerData dataAccess;

    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (i) -> i == OUTPUT, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (index) -> index == INPUT,
                            (index, stack) -> itemStackHandler.isItemValid(INPUT, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (index) -> index == INPUT,
                            (index, stack) -> itemStackHandler.isItemValid(INPUT, stack))),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (index) -> index == INPUT,
                            (index, stack) -> itemStackHandler.isItemValid(INPUT, stack))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (index) -> index == INPUT,
                            (index, stack) -> itemStackHandler.isItemValid(INPUT, stack))));

    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();


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

    protected static int getTotalCrushingTime(Level pLevel, MaceratorTileBase pBlockEntity) {
        AbstractMaceratorRecipe recipe = pBlockEntity.getRecipe(pBlockEntity.itemStackHandler.getStackInSlot(INPUT));

        return recipe != null ? recipe.getCrushingTime() : CRUSH_TIME_STANDARD;
    }

    protected abstract AbstractMaceratorRecipe getRecipe(ItemStack itemLeft);

    protected boolean isCrushing() {
        LOGGER.debug("recipe null: {}", getRecipe(getItem(INPUT)) == null);
        return crushingTime > 0 && canCrush();
    }

    public boolean canCrush() {
        AbstractMaceratorRecipe recipe = getRecipe(getItem(INPUT));

        if (recipe == null) {
            return false;
        }

        ItemStack output = recipe.getResultItem();
        ItemStack outputSlot = getItem(OUTPUT);

        boolean outputSlotEmpty = outputSlot.isEmpty();
        return outputSlotEmpty || outputSlot.is(output.getItem()) && outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize();
    }

    public void setCustomName(Component name) {
        this.name = name;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        crushingTime = pTag.getInt("CrushingTime");
        crushingProgress = pTag.getInt("CrushingTime");
        crushingTotalTime = pTag.getInt("CrushingTimeTotal");

        CompoundTag compoundTag = pTag.getCompound("RecipesUsed");

        for (String s : compoundTag.getAllKeys()) {
            recipesUsed.put(new ResourceLocation(s), compoundTag.getInt(s));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putInt("CrushingTime", crushingTime);
        pTag.putInt("CrushingTime", crushingProgress);
        pTag.putInt("CrushingTimeTotal", crushingTotalTime);

        CompoundTag nbt = new CompoundTag();
        recipesUsed.forEach((recipeId, craftedAmount) -> {
            nbt.putInt(recipeId.toString(), craftedAmount);
        });

        pTag.put("RecipesUsed", nbt);
    }

    public void dropContents() {
        for (int i = 0; i <= 4; i++) {
            ItemStack stack = getItem(i);

            Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), stack);
        }
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
            crushingTotalTime = getTotalCrushingTime(level, this);
            setChanged();
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
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
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {
        if (pRecipe == null) return;

        ResourceLocation resourceLocation = pRecipe.getId();
        recipesUsed.addTo(resourceLocation, 1);
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player pPlayer) {
        RecipeHolder.super.awardUsedRecipes(pPlayer);
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer pPlayer) {
        List<Recipe<?>> list = getRecipesToAwardAndPopExperience(pPlayer.getLevel(), pPlayer.position());
        pPlayer.awardRecipes(list);

        recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec) {
        List<Recipe<?>> list = Lists.newArrayList();

        for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                createExperience(pLevel, pPopVec, entry.getIntValue(), ((AbstractMaceratorRecipe)recipe).getExperience());
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel pLevel, Vec3 pPopVec, int pRecipeIndex, float pExperience) {
        int i = Mth.floor((float)pRecipeIndex * pExperience);
        float f = Mth.frac((float)pRecipeIndex * pExperience);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        ExperienceOrb.award(pLevel, pPopVec, i);
    }

    @Override
    public void fillStackedContents(StackedContents pHelper) {
        for(int i = 0; i < itemStackHandler.getSlots(); i++) {
            ItemStack itemStack = itemStackHandler.getStackInSlot(i);
            pHelper.accountStack(itemStack);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction facing) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(facing == null) {
                return lazyItemHandler.cast();
            }

            if(directionWrappedHandlerMap.containsKey(facing)) {
                Direction localDir = this.getBlockState().getValue(AbstractMaceratorBlock.FACING);

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

    public boolean stillValid(Player pPlayer) {
        if (level.getBlockEntity(worldPosition) != this) {
            return false;
        }

        return pPlayer.distanceToSqr(
                (double)this.worldPosition.getX() + 0.5D,
                (double)this.worldPosition.getY() + 0.5D,
                (double)this.worldPosition.getZ() + 0.5D
        ) <= 64.0D;
    }

    private static final Logger LOGGER = UnnamedTechMod.LOGGER;
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MaceratorTileBase pEntity) {
        boolean isCrushing = pEntity.isCrushing();
        boolean isDirty = false;

        if (isCrushing) {
            --pEntity.crushingTime;
        }

        ItemStack input = pEntity.getItem(INPUT);

        if (!input.isEmpty() && isCrushing){
            AbstractMaceratorRecipe recipe = pEntity.getRecipe(input);

            if (recipe != null) {
                ItemStack output = recipe.getResultItem();
                ItemStack outputSlot = pEntity.getItem(OUTPUT);

                boolean outputSlotEmpty = outputSlot.isEmpty();
                boolean canCrush = outputSlotEmpty || outputSlot.is(output.getItem()) && outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize();

                if (canCrush) {
                    ++pEntity.crushingProgress;

                    if (pEntity.crushingProgress >= pEntity.crushingTotalTime) {
                        pEntity.crushingProgress = 0;
                        pEntity.crushingTotalTime = getTotalCrushingTime(pLevel, pEntity);

                        if (outputSlotEmpty) {
                            pEntity.setItem(OUTPUT, output.copy());
                        } else if (outputSlot.getItem() == output.getItem()) {
                            outputSlot.grow(output.getCount());
                        }

                        input.shrink(1);

                        isDirty = true;
                    }
                } else {
                    pEntity.crushingProgress = 0;
                }
            }
        }

        if (isCrushing != pEntity.isCrushing()) {
            isDirty = true;
            pState = pState.setValue(AbstractMaceratorBlock.WORKING, pEntity.isCrushing());
            pLevel.setBlock(pPos, pState, 3);
        }

        if (isDirty) {
            setChanged(pLevel, pPos, pState);
        }

        LOGGER.debug("Crushing: " + pEntity.isCrushing() + " | Crushing Time: " + pEntity.crushingTime + " | Crushing Progress: " + pEntity.crushingProgress + " | Crushing Total Time: " + pEntity.crushingTotalTime + " | Duration" + pEntity.crushingDuration + " | Input: " + pEntity.getItem(INPUT) + " | Output: " + pEntity.getItem(OUTPUT));
    }
}
