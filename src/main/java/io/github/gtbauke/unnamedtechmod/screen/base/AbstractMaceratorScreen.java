package io.github.gtbauke.unnamedtechmod.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractMaceratorScreen<T extends AbstractMaceratorMenu> extends AbstractExtendedContainerScreen<T> {
    public AbstractMaceratorScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation pTexture) {
        super(pMenu, pPlayerInventory, pTitle, pTexture);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        if (menu.isCrushing()) {
            int l = this.menu.getProgress();
            this.blit(pPoseStack, x + 71, y + 35, 176, 0, l + 1, 16);
        }
    }
}
