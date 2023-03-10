package io.github.gtbauke.unnamedtechmod.screen.base;

import io.github.gtbauke.unnamedtechmod.block.entity.base.AlloySmelterTileBase;
import io.github.gtbauke.unnamedtechmod.block.entity.base.MaceratorTileBase;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.recipe.AbstractMaceratorRecipe;
import io.github.gtbauke.unnamedtechmod.utils.AlloySmelting;
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

public abstract class AbstractMaceratorMenu extends AbstractExtendedContainerMenu {

    private final ContainerData data;
    private final MaceratorTileBase blockEntity;
    protected final Level level;

    private final RecipeType<? extends AbstractMaceratorRecipe> recipeType;
    protected AbstractMaceratorMenu(MenuType<?> pMenuType, RecipeType<? extends AbstractMaceratorRecipe> pRecipeType, int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity, ContainerData pData) {
        super(pMenuType, pContainerId);

        this.recipeType = pRecipeType;
        this.blockEntity = (MaceratorTileBase) blockEntity;
        checkContainerSize(pPlayerInventory, MaceratorTileBase.INVENTORY_SIZE);
        checkContainerDataCount(pData, 4);

        this.data = pData;
        this.level = pPlayerInventory.player.level;

        addPlayerInventory(pPlayerInventory);
        addPlayerHotbar(pPlayerInventory);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, MaceratorTileBase.INPUT, 41, 36));
            addSlot(new SlotItemHandler(itemHandler, MaceratorTileBase.OUTPUT, 116, 35));
        });

        addDataSlots(pData);

        TE_INVENTORY_SLOT_COUNT = MaceratorTileBase.INVENTORY_SIZE;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.MANUAL_MACERATOR.get());
    }

    public boolean isCrushing() {
        return data.get(MaceratorTileBase.DATA_CRUSHING_PROGRESS) > 0;
    }

    public int getProgress() {
        int progress = this.data.get(MaceratorTileBase.DATA_CRUSHING_PROGRESS);
        int totalProgress = this.data.get(MaceratorTileBase.DATA_CRUSHING_TOTAL_TIME);

        return totalProgress != 0 ? ((progress * 27) / totalProgress) : 0;
    }
}
