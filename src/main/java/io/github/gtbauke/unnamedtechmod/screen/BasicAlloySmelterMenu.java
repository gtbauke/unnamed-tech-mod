package io.github.gtbauke.unnamedtechmod.screen;

import io.github.gtbauke.unnamedtechmod.recipe.BasicAlloySmelterRecipe;
import io.github.gtbauke.unnamedtechmod.screen.base.AbstractAlloySmelterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BasicAlloySmelterMenu extends AbstractAlloySmelterMenu {

    public BasicAlloySmelterMenu(int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity, ContainerData pData) {
        super(ModMenuTypes.BASIC_ALLOY_SMELTER_MENU.get(), BasicAlloySmelterRecipe.Type.INSTANCE, pContainerId, pPlayerInventory, blockEntity, pData);
    }

    public BasicAlloySmelterMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pPlayerInventory, pPlayerInventory.player.level.getBlockEntity(pBuffer.readBlockPos()), new SimpleContainerData(4));
    }
}
