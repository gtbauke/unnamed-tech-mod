package io.github.gtbauke.unnamedtechmod.screen.base;

import io.github.gtbauke.unnamedtechmod.block.entity.base.AlloySmelterTileBase;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractAlloySmeltingRecipe;
import io.github.gtbauke.unnamedtechmod.utils.AlloySmelting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public abstract class AbstractAlloySmelterMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final AlloySmelterTileBase blockEntity;
    protected final Level level;

    private final RecipeType<? extends AbstractAlloySmeltingRecipe> recipeType;

    protected AbstractAlloySmelterMenu(MenuType<?> pMenuType, RecipeType<? extends AbstractAlloySmeltingRecipe> pRecipeType, int pContainerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(pMenuType, pRecipeType, pContainerId, inventory,
                inventory.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    protected AbstractAlloySmelterMenu(MenuType<?> pMenuType, RecipeType<? extends AbstractAlloySmeltingRecipe> pRecipeType, int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity, ContainerData pData) {
        super(pMenuType, pContainerId);

        this.recipeType = pRecipeType;
        this.blockEntity = (AlloySmelterTileBase) blockEntity;
        checkContainerSize(pPlayerInventory, AlloySmelterTileBase.INVENTORY_SIZE);
        checkContainerDataCount(pData, 4);

        this.data = pData;
        this.level = pPlayerInventory.player.level;

        addPlayerInventory(pPlayerInventory);
        addPlayerHotbar(pPlayerInventory);

        // TODO: create custom slot classes for better validation logic
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, AlloySmelting.LEFT_INPUT, 34, 17));
            addSlot(new SlotItemHandler(itemHandler, AlloySmelting.RIGHT_INPUT, 56, 17));
            addSlot(new SlotItemHandler(itemHandler, AlloySmelting.ALLOY_COMPOUND, 34, 53));
            addSlot(new SlotItemHandler(itemHandler, AlloySmelting.FUEL, 56, 53));
            addSlot(new SlotItemHandler(itemHandler, AlloySmelting.OUTPUT, 116, 35));
        });

        addDataSlots(pData);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.BASIC_ALLOY_SMELTER.get());
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = AlloySmelterTileBase.INVENTORY_SIZE;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);

        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;  //EMPTY_ITEM

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public int getBurnProgress() {
        int progress = this.data.get(AlloySmelterTileBase.DATA_COOKING_PROGRESS);
        int totalProgress = this.data.get(AlloySmelterTileBase.DATA_COOKING_TOTAL_TIME);

        return totalProgress != 0 ? ((progress * 24) / totalProgress) : 0;
    }

    public int getLitProgress() {
        int litDuration = this.data.get(AlloySmelterTileBase.DATA_LIT_DURATION);
        if (litDuration == 0) {
            litDuration = AlloySmelterTileBase.BURN_TIME_STANDARD;
        }

        return this.data.get(AlloySmelterTileBase.DATA_LIT_TIME) * 13 / litDuration;
    }

    public boolean isLit() {
        return data.get(AlloySmelterTileBase.DATA_LIT_TIME) > 0;
    }

    public boolean shouldMoveToInventory(int pSlotIndex) {
        return pSlotIndex != AlloySmelting.FUEL;
    }

    public boolean isCrafting() {
        return data.get(AlloySmelterTileBase.DATA_COOKING_PROGRESS) > 0;
    }
}
