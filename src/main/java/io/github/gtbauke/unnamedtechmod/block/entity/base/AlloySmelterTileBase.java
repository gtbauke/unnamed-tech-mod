package io.github.gtbauke.unnamedtechmod.block.entity.base;

import com.google.common.collect.Lists;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractAlloySmelterBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.WrappedHandler;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractAlloySmeltingRecipe;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import io.github.gtbauke.unnamedtechmod.utils.ModTags;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.utils.Log;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AlloySmelterTileBase extends TileEntityInventory implements
        RecipeHolder,
        StackedContentsCompatible {
    public static final int LEFT_INPUT = 0;
    public static final int RIGHT_INPUT = 1;
    public static final int ALLOY_COMPOUND = 2;
    public static final int FUEL = 3;
    public static final int OUTPUT = 4;

    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int INVENTORY_SIZE = 5;

    protected int litTime;
    protected int litDuration;
    protected int cookingProgress;
    protected int cookingTotalTime;

    public final RecipeType<? extends AbstractAlloySmeltingRecipe> recipeType;

    protected final ContainerData dataAccess;

    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    public AlloySmelterTileBase(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractAlloySmeltingRecipe> recipeType) {
        super(tileEntityType, pos, state, INVENTORY_SIZE);
        this.recipeType = recipeType;
        this.dataAccess = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case DATA_LIT_TIME -> AlloySmelterTileBase.this.litTime;
                    case DATA_LIT_DURATION -> AlloySmelterTileBase.this.litDuration;
                    case DATA_COOKING_PROGRESS -> AlloySmelterTileBase.this.cookingProgress;
                    case DATA_COOKING_TOTAL_TIME -> AlloySmelterTileBase.this.cookingTotalTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case DATA_LIT_TIME -> AlloySmelterTileBase.this.litTime = pValue;
                    case DATA_LIT_DURATION -> AlloySmelterTileBase.this.litDuration = pValue;
                    case DATA_COOKING_PROGRESS -> AlloySmelterTileBase.this.cookingProgress = pValue;
                    case DATA_COOKING_TOTAL_TIME -> AlloySmelterTileBase.this.cookingTotalTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    protected boolean isLit() { return litTime > 0; }

    protected abstract AbstractAlloySmeltingRecipe getRecipe(ItemStack itemLeft, ItemStack itemRight, ItemStack alloyCompound);

    public void dropContents() {
        for (int i = 0; i <= 4; i++) {
            ItemStack stack = getItem(i);

            Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), stack);
        }
    }

    public void setCustomName(Component name) {
        this.name = name;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        litTime = pTag.getInt("BurnTime");
        cookingProgress = pTag.getInt("CookTime");
        cookingTotalTime = pTag.getInt("CookTimeTotal");
        litDuration = getBurnDuration(itemStackHandler.getStackInSlot(FUEL));

        CompoundTag compoundTag = pTag.getCompound("RecipesUsed");

        for (String s : compoundTag.getAllKeys()) {
            recipesUsed.put(new ResourceLocation(s), compoundTag.getInt(s));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putInt("BurnTime", litTime);
        pTag.putInt("CookTime", cookingProgress);
        pTag.putInt("CookTimeTotal", cookingTotalTime);

        CompoundTag nbt = new CompoundTag();
        recipesUsed.forEach((recipeId, craftedAmount) -> {
            nbt.putInt(recipeId.toString(), craftedAmount);
        });

        pTag.put("RecipesUsed", nbt);
    }

    public int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        return ForgeHooks.getBurnTime(stack, recipeType);
    }

    public static int getTotalCookingTime(Level pLevel, AlloySmelterTileBase pBlockEntity) {
        ItemStack left = pBlockEntity.itemStackHandler.getStackInSlot(LEFT_INPUT);
        ItemStack right = pBlockEntity.itemStackHandler.getStackInSlot(RIGHT_INPUT);
        ItemStack alloyCompound = pBlockEntity.itemStackHandler.getStackInSlot(ALLOY_COMPOUND);

        AbstractAlloySmeltingRecipe recipe = pBlockEntity.getRecipe(left, right, alloyCompound);
        return recipe == null ? BURN_TIME_STANDARD : recipe.getCookingTime();
    }

    public static boolean isItemFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0;
    }

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

    // TODO: implement getSlotsForFace, canPlaceItemThroughFace, canTakeItemThroughFace

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction facing) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(facing == null) {
                return lazyItemHandler.cast();
            }

            if(directionWrappedHandlerMap.containsKey(facing)) {
                Direction localDir = this.getBlockState().getValue(AbstractAlloySmelterBlock.FACING);

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

    // TODO: implement getSlotsForFace, canExtractItem, insertItemInternal,
    // TODO: implement extractItemInternal

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == OUTPUT) {
            return false;
        }

        if (index == LEFT_INPUT || index == RIGHT_INPUT) {
            return true;
        }

        if (index == FUEL) {
            ItemStack itemStack = getItem(FUEL);
            return ForgeHooks.getBurnTime(stack, recipeType) > 0 || (
                    stack.getItem() == Items.BUCKET &&
                    itemStack.getItem() != Items.BUCKET);
        }

        if (index == ALLOY_COMPOUND) {
            return stack.getItem() == ModItems.LIGHT_CLAY_BALL.get()
                    || stack.getItem() == Items.CLAY_BALL;
        }

        return false;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {
        if (pRecipe != null) {
            ResourceLocation resourceLocation = pRecipe.getId();
            recipesUsed.addTo(resourceLocation, 1);
        }
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
                createExperience(pLevel, pPopVec, entry.getIntValue(), ((AbstractAlloySmeltingRecipe)recipe).getExperience());
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
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemStack = itemStackHandler.getStackInSlot(pSlot);
        boolean flag = !pStack.isEmpty() && pStack.sameItem(itemStack) && ItemStack.tagMatches(pStack, itemStack);

        itemStackHandler.setStackInSlot(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        if ((pSlot == LEFT_INPUT || pSlot == RIGHT_INPUT) && !flag) {
            cookingTotalTime = getTotalCookingTime(level, this);
            cookingProgress = 0;
            setChanged();
        }
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

    protected boolean canSmelt(@Nullable AbstractAlloySmeltingRecipe pRecipe, ItemStackHandler pInventory, int pStackSize) {
        if (pRecipe == null) {
            return false;
        }

        ItemStack left = pInventory.getStackInSlot(LEFT_INPUT);
        ItemStack right = pInventory.getStackInSlot(RIGHT_INPUT);

        boolean hasItemInLeftSlot = !left.isEmpty();
        boolean hasItemInRightSlot = !right.isEmpty();

        boolean hasCorrectIngredients = pRecipe.ingredientsContain(new SimpleContainer(left, right));

        boolean hasAlloyCompound = pInventory.getStackInSlot(ALLOY_COMPOUND).is(ModTags.Items.ALLOY_COMPOUND);
        boolean hasCorrectAmountOfAlloyCompound = pInventory.getStackInSlot(ALLOY_COMPOUND).getCount() >= pRecipe.getAlloyCompoundAmount();

        boolean canInsertResultInOutputSlot =
                pInventory.getStackInSlot(OUTPUT).isEmpty() ||
                (pInventory.getStackInSlot(OUTPUT).is(pRecipe.getResultItem().getItem()) &&
                pInventory.getStackInSlot(OUTPUT).getCount() + pRecipe.getResultItem().getCount() <= pRecipe.getResultItem().getMaxStackSize());

        boolean canSmelt = hasItemInLeftSlot && hasItemInRightSlot &&
                hasCorrectIngredients &&
                hasAlloyCompound && hasCorrectAmountOfAlloyCompound &&
                canInsertResultInOutputSlot;

        return canSmelt;
    }

    private boolean smelt(@Nullable Recipe<?> pRecipe, ItemStackHandler pStacks, int pStackSize) {
        if (pRecipe == null || !(pRecipe instanceof AbstractAlloySmeltingRecipe)) {
            return false;
        }

        AbstractAlloySmeltingRecipe alloyRecipe = (AbstractAlloySmeltingRecipe)pRecipe;
        if (!canSmelt(alloyRecipe, pStacks, pStackSize)) {
            return false;
        }

        ItemStack left = pStacks.getStackInSlot(LEFT_INPUT);
        ItemStack right = pStacks.getStackInSlot(RIGHT_INPUT);
        ItemStack alloyCompound = pStacks.getStackInSlot(ALLOY_COMPOUND);
        ItemStack output = pStacks.getStackInSlot(OUTPUT);

        ItemStack recipeResult = alloyRecipe.assemble(new SimpleContainer(left, right, alloyCompound));
        System.out.println(recipeResult);

        if (output.isEmpty()) {
            pStacks.setStackInSlot(OUTPUT, recipeResult);
        } else if (output.is(recipeResult.getItem())) {
            output.grow(recipeResult.getCount());
        } else {
            return false;
        }

        left.shrink(alloyRecipe.getUsedAmountOf(left.getItem()));
        right.shrink(alloyRecipe.getUsedAmountOf(right.getItem()));
        alloyCompound.shrink(alloyRecipe.getAlloyCompoundAmount());

        return true;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AlloySmelterTileBase pEntity) {
        boolean wasLit = pEntity.isLit();
        boolean isDirty = false;

        if (pEntity.isLit()) {
            --pEntity.litTime;
        }

        ItemStack left = pEntity.itemStackHandler.getStackInSlot(LEFT_INPUT);
        ItemStack right = pEntity.itemStackHandler.getStackInSlot(RIGHT_INPUT);
        ItemStack alloyCompound = pEntity.itemStackHandler.getStackInSlot(ALLOY_COMPOUND);

        ItemStack fuelSlot = pEntity.getItem(FUEL);
        boolean hasInput = !(pEntity.getItem(LEFT_INPUT).isEmpty() || pEntity.getItem(RIGHT_INPUT).isEmpty());
        boolean hasFuel = !fuelSlot.isEmpty();

        if (pEntity.isLit() || hasFuel && hasInput) {
            AbstractAlloySmeltingRecipe recipe = pLevel.getRecipeManager().getRecipeFor(
                    pEntity.recipeType,
                    new SimpleContainer(left, right, alloyCompound),
                    pLevel
            ).orElse(null);

            int stackSize = pEntity.getMaxStackSize();
            boolean canSmelt = pEntity.canSmelt(recipe, pEntity.itemStackHandler, stackSize);

            if (!pEntity.isLit() && canSmelt) {
                pEntity.litTime = pEntity.getBurnDuration(fuelSlot);
                pEntity.litDuration = pEntity.litTime;

                if (pEntity.isLit()) {
                    isDirty = true;

                    if (fuelSlot.hasCraftingRemainingItem()) {
                        pEntity.itemStackHandler.setStackInSlot(FUEL, fuelSlot.getCraftingRemainingItem());
                    } else if (hasFuel) {
                        Item item = fuelSlot.getItem();
                        fuelSlot.shrink(1);

                        if (fuelSlot.isEmpty()) {
                            pEntity.itemStackHandler.setStackInSlot(FUEL, fuelSlot.getCraftingRemainingItem());
                        }
                    }
                }
            }

            canSmelt = pEntity.canSmelt(recipe, pEntity.itemStackHandler, stackSize);
            if (pEntity.isLit() && canSmelt) {
                ++pEntity.cookingProgress;
                System.out.println(pEntity.cookingProgress);

                if (pEntity.cookingProgress >= pEntity.cookingTotalTime) {
                    pEntity.cookingProgress = 0;
                    pEntity.cookingTotalTime = getTotalCookingTime(pLevel, pEntity);

                    if (pEntity.smelt(recipe, pEntity.itemStackHandler, stackSize)) {
                        pEntity.setRecipeUsed(recipe);
                    }

                    isDirty = true;
                }
            } else {
                pEntity.cookingProgress = 0;
            }
        } else if (!pEntity.isLit() && pEntity.cookingProgress > 0) {
            pEntity.cookingProgress = Mth.clamp(pEntity.cookingProgress - 2, 0, pEntity.cookingTotalTime);
        }

        if (wasLit != pEntity.isLit()) {
            isDirty = true;
            pState = pState.setValue(AbstractAlloySmelterBlock.LIT, Boolean.valueOf(pEntity.isLit()));
            pLevel.setBlock(pPos, pState, 3);
        }

        if (isDirty) {
            setChanged(pLevel, pPos, pState);
        }
    }
}
