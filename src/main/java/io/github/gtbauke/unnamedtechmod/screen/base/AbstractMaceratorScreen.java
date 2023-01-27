package io.github.gtbauke.unnamedtechmod.screen.base;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractMaceratorScreen<T extends AbstractMaceratorMenu> extends AbstractContainerScreen<T> {
    public AbstractMaceratorScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
}
