package io.github.gtbauke.unnamedtechmod.screen;

import io.github.gtbauke.unnamedtechmod.block.entity.base.PressTileBase;
import io.github.gtbauke.unnamedtechmod.recipe.BasicPressRecipe;
import io.github.gtbauke.unnamedtechmod.screen.base.AbstractPressMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BasicPressMenu extends AbstractPressMenu {
    public BasicPressMenu(int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity, ContainerData pData) {
        super(ModMenuTypes.BASIC_PRESS.get(), BasicPressRecipe.Type.INSTANCE, pContainerId, pPlayerInventory, blockEntity, pData);
    }

    public BasicPressMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pPlayerInventory, pPlayerInventory.player.level.getBlockEntity(pBuffer.readBlockPos()), new SimpleContainerData(PressTileBase.DATA_AMOUNT));
    }
}
