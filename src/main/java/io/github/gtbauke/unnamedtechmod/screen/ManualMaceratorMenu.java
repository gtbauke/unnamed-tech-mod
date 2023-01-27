package io.github.gtbauke.unnamedtechmod.screen;

import io.github.gtbauke.unnamedtechmod.recipe.ManualMaceratorRecipe;
import io.github.gtbauke.unnamedtechmod.screen.base.AbstractMaceratorMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ManualMaceratorMenu extends AbstractMaceratorMenu {
    public ManualMaceratorMenu(int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity, ContainerData pData) {
        super(ModMenuTypes.MANUAL_MACERATOR_MENU.get(), ManualMaceratorRecipe.Type.INSTANCE, pContainerId, pPlayerInventory, blockEntity, pData);
    }

    public ManualMaceratorMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pPlayerInventory, pPlayerInventory.player.level.getBlockEntity(pBuffer.readBlockPos()), new SimpleContainerData(4));
    }
}
