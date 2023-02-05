package io.github.gtbauke.unnamedtechmod.screen.base;

import io.github.gtbauke.unnamedtechmod.block.entity.base.PressTileBase;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractPressRecipe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public abstract class AbstractPressMenu extends AbstractExtendedContainerMenu {
    private final ContainerData data;
    private final PressTileBase blockEntity;
    protected final Level level;

    private final RecipeType<? extends AbstractPressRecipe> recipeType;

    protected AbstractPressMenu(MenuType<?> pMenuType, RecipeType<? extends AbstractPressRecipe> pRecipeType, int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity, ContainerData pData) {
        super(pMenuType, pContainerId);

        this.recipeType = pRecipeType;
        this.blockEntity = (PressTileBase) blockEntity;
        checkContainerSize(pPlayerInventory, PressTileBase.INVENTORY_SIZE);
        checkContainerDataCount(pData, 5);

        this.data = pData;
        this.level = pPlayerInventory.player.level;

        addPlayerInventory(pPlayerInventory);
        addPlayerHotbar(pPlayerInventory);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, PressTileBase.INPUT, 56, 53));
            addSlot(new SlotItemHandler(itemHandler, PressTileBase.FUEL, 56, 17));
            addSlot(new SlotItemHandler(itemHandler, PressTileBase.OUTPUT, 116, 35));
        });
        addDataSlots(pData);

        TE_INVENTORY_SLOT_COUNT = PressTileBase.INVENTORY_SIZE;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.BASIC_PRESS.get());
    }

    public boolean isPressing() {
        return data.get(PressTileBase.DATA_PRESSING_PROGRESS) > 0;
    }

    public int getProgress() {
        int progress = this.data.get(PressTileBase.DATA_PRESSING_PROGRESS);
        int totalProgress = this.data.get(PressTileBase.DATA_PRESSING_TOTAL_TIME);

        progress = totalProgress / 2;

        return totalProgress != 0 ? ((progress * 27) / totalProgress) : 0;
    }

    public boolean isLit() {
        return data.get(PressTileBase.DATA_LIT_TIME) > 0;
    }

    public int getLitProgress() {
        int litDuration = this.data.get(PressTileBase.DATA_PRESSING_DURATION);
        if (litDuration == 0) {
            litDuration = PressTileBase.BURN_TIME_STANDARD;
        }

        //return this.data.get(PressTileBase.DATA_LIT_TIME) * 13 / litDuration;
        return (litDuration / 2) * 13 / litDuration;
    }
}
