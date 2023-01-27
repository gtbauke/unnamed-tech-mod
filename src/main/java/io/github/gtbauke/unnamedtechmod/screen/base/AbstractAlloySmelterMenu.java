package io.github.gtbauke.unnamedtechmod.screen.base;

import io.github.gtbauke.unnamedtechmod.block.entity.base.AlloySmelterTileBase;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractAlloySmeltingRecipe;
import io.github.gtbauke.unnamedtechmod.utils.AlloySmelting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public abstract class AbstractAlloySmelterMenu extends AbstractExtendedContainerMenu{
    private final ContainerData data;
    protected final Level level;
    private final AlloySmelterTileBase blockEntity;

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

        TE_INVENTORY_SLOT_COUNT = AlloySmelterTileBase.INVENTORY_SIZE;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.BASIC_ALLOY_SMELTER.get());
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
