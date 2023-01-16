package io.github.gtbauke.unnamedtechmod.block.entity.base;

import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class AlloySmelterTileBase extends TileEntityInventory implements
        RecipeHolder,
        StackedContentsCompatible {
    public static final int LEFT_INPUT = 0;
    public static final int RIGHT_INPUT = 1;
    public static final int ALLOY_COMPOUND = 2;
    public static final int FUEL = 3;
    public static final int OUTPUT = 4;

    public int furnaceBurnTime;
    public int cookTime;
    public int totalCookTime;
    public int recipesUsed;
    public int timer;

    public final Object2IntOpenHashMap<ResourceLocation> recipes = new Object2IntOpenHashMap<>();
    public RecipeType<?> recipeType;

    public AlloySmelterTileBase(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, int inventorySize) {
        super(tileEntityType, pos, state, inventorySize);
        recipeType = BasicAlloySmelterRecipe.Type.INSTANCE;
    }

    public boolean hasRecipe(ItemStack left, ItemStack right) {
        return getRecipe(left, right).isPresent();
    }

    protected Optional<BasicAlloySmelterRecipe> getRecipe(ItemStack itemLeft, ItemStack itemRight) {
        if (itemLeft.getItem() instanceof AirItem && itemRight.getItem() instanceof  AirItem) {
            return Optional.empty();
        }

        RecipeManager recipeManager = level.getRecipeManager();
        return Optional.ofNullable(recipeManager.getRecipeFor(
                (RecipeType<BasicAlloySmelterRecipe>)recipeType,
                new SimpleContainer(itemLeft, itemRight),
                level
        )).orElse(null);
    }

    public int getCookTime() {
        if (getItem(LEFT_INPUT).getItem() == Items.AIR
        && getItem(RIGHT_INPUT).getItem() == Items.AIR) {
            return totalCookTime;
        }

        return Math.max(1, getSpeed());
    }

    public ForgeConfigSpec.IntValue getCookTimeConfig() {
        return null;
    }

    protected int getSpeed() {
        int regular = getCookTimeConfig().get();

        RecipeManager recipeManager = level.getRecipeManager();
        ItemStack leftItem = getItem(LEFT_INPUT);
        ItemStack rightItem = getItem(RIGHT_INPUT);

        int recipeTime = recipeManager.getRecipeFor(
                (RecipeType<BasicAlloySmelterRecipe>)recipeType,
                new SimpleContainer(leftItem, rightItem),
                level
        ).map(BasicAlloySmelterRecipe::getCookingTime).orElse(0);

        double div = 200.0 / recipeTime;
        double i = regular / div;

        return (int)Math.max(1, i);
    }

    public void dropContents() {
        for (int i = 0; i <= 4; i++) {
            ItemStack stack = getItem(i);

            Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), stack);
        }
    }

    public boolean isBurning() {
        return furnaceBurnTime > 0;
    }

    @Override
    public void load(CompoundTag pTag) {
        furnaceBurnTime = pTag.getInt("BurnTime");
        cookTime = pTag.getInt("CookTime");
        totalCookTime = pTag.getInt("CookTimeTotal");

        timer = 0;
        recipesUsed = getBurnTime(getItem(FUEL));
        CompoundTag nbt = pTag.getCompound("RecipesUsed");

        for (String s : nbt.getAllKeys()) {
            recipes.put(new ResourceLocation(s), nbt.getInt(s));
        }

        super.load(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putInt("BurnTime", furnaceBurnTime);
        pTag.putInt("CookTime", cookTime);
        pTag.putInt("CookTimeTotal", totalCookTime);

        CompoundTag nbt = new CompoundTag();
        recipes.forEach((recipeId, craftedAmount) -> {
            nbt.putInt(recipeId.toString(), craftedAmount);
        });

        pTag.put("RecipesUsed", nbt);
    }

    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        Item item = stack.getItem();
        int ret = stack.getBurnTime(RecipeType.SMELTING);

        return ForgeEventFactory.getItemBurnTime(stack, ret == -1
                ? AbstractFurnaceBlockEntity.getFuel().getOrDefault(item, 0)
                : ret, RecipeType.SMELTING
        );
    }

    public static boolean isItemFuel(ItemStack stack) {
        return getBurnTime(stack) > 0;
    }

    LazyOptional<? extends IItemHandler>[] invHandlers =
            SidedInvWrapper.create(this, Direction.DOWN, Direction.UP,
                    Direction.NORTH, Direction.SOUTH,
                    Direction.WEST, Direction.EAST);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction facing) {
        if (!isRemoved() && facing != null && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.DOWN)
                return invHandlers[0].cast();
            else if (facing == Direction.UP)
                return invHandlers[1].cast();
            else if (facing == Direction.NORTH)
                return invHandlers[2].cast();
            else if (facing == Direction.SOUTH)
                return invHandlers[3].cast();
            else if (facing == Direction.WEST)
                return invHandlers[4].cast();
            else
                return invHandlers[5].cast();
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

        if (index == LEFT_INPUT) {
            if (stack.isEmpty()) {
                return false;
            }

            return hasRecipe(stack, getItem(RIGHT_INPUT));
        }

        if (index == RIGHT_INPUT) {
            if (stack.isEmpty()) {
                return false;
            }

            return hasRecipe(getItem(LEFT_INPUT), stack);
        }

        if (index == FUEL) {
            ItemStack itemStack = getItem(FUEL);
            return getBurnTime(stack) > 0 || (
                    stack.getItem() == Items.BUCKET &&
                    itemStack.getItem() != Items.BUCKET);
        }

        if (index == ALLOY_COMPOUND) {
            return stack.getItem() == ModItems.LIGHT_CLAY_BALL.get()
                    || stack.getItem() == Items.CLAY_BALL;
        }

        return false;
    }

    // TODO: furnace xp

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void fillStackedContents(StackedContents pHelper) {
        for (ItemStack itemstack : inventory) {
            pHelper.accountStack(itemstack);
        }
    }

    protected boolean canSmelt(@Nullable Recipe<?> recipe) {
        if (!getItem(LEFT_INPUT).isEmpty() && !getItem(RIGHT_INPUT).isEmpty() && recipe != null) {
            ItemStack recipeOutput = recipe.getResultItem();

            if (!recipeOutput.isEmpty()) {
                ItemStack output = getItem(OUTPUT);

                if (output.isEmpty())
                    return true;

                if (!output.sameItem(recipeOutput))
                    return false;

                return output.getCount() + recipeOutput.getCount() <= output.getMaxStackSize();
            }
        }

        return false;
    }

    private boolean isInputEmpty(int pSlot) {
        return getItem(pSlot).isEmpty();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, AlloySmelterTileBase entity) {
        boolean wasBurning = entity.isBurning();

        if (!entity.level.isClientSide) {
            entity.timer++;

            if (entity.totalCookTime != entity.getCookTime()) {
                entity.totalCookTime = entity.getCookTime();
            }

            if (entity.isBurning()) {
                --entity.furnaceBurnTime;
            }

            ItemStack itemStack = entity.getItem(FUEL);

            if (entity.isBurning() || !itemStack.isEmpty() && !entity.isInputEmpty(LEFT_INPUT) && !entity.isInputEmpty(RIGHT_INPUT)) {
                Optional<BasicAlloySmelterRecipe> recipe = Optional.empty();

                if (!entity.isInputEmpty(LEFT_INPUT) && !entity.isInputEmpty(RIGHT_INPUT)) {
                    ItemStack left = entity.getItem(LEFT_INPUT);
                    ItemStack right = entity.getItem(RIGHT_INPUT);

                    recipe = entity.getRecipe(left, right);
                }

                boolean valid = entity.canSmelt(recipe.orElse(null));
                if (entity.isBurning() || !valid) {
                    entity.furnaceBurnTime = getBurnTime(itemStack) * entity.getCookTime() / 200;
                    entity.recipesUsed = entity.furnaceBurnTime;
                }

                if (entity.isBurning() && valid) {
                    ++entity.cookTime;

                    if (entity.cookTime >= entity.totalCookTime) {
                        entity.cookTime = 0;
                        entity.totalCookTime = entity.getCookTime();

                        entity.smelt(recipe.orElse(null));
                    }
                } else {
                    entity.cookTime = 0;
                }
            } else if (!entity.isBurning() && entity.cookTime > 0) {
                entity.cookTime = clamp(entity.cookTime - 2, 0, entity.totalCookTime);
            }

            if (wasBurning != entity.isBurning()) {
                level.setBlock(blockPos, level.getBlockState(entity.worldPosition).setValue(BlockStateProperties.LIT, entity.isBurning()), 3);
            }
        }
    }

    private void smelt(@Nullable Recipe<?> recipe) {
        timer = 0;

        if (recipe != null && canSmelt(recipe)) {
            ItemStack left = getItem(LEFT_INPUT);
            ItemStack right = getItem(RIGHT_INPUT);

            ItemStack recipeOutput = recipe.getResultItem();
            ItemStack output = getItem(OUTPUT);

            if (output.isEmpty()) {
                setItem(OUTPUT, recipeOutput.copy());
            } else if (output.getItem() == recipeOutput.getItem()) {
                output.grow(recipeOutput.getCount());
            }

            // check cp
            if (!level.isClientSide) {
                setRecipeUsed(recipe);
            }

            // TODO: get amount that they decrement in recipe
            left.shrink(1);
            right.shrink(1);
        }
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }

        return value > max ? max : value;
    }
}
