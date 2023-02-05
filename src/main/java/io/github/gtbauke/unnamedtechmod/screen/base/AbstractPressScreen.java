package io.github.gtbauke.unnamedtechmod.screen.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractPressScreen<T extends AbstractPressMenu> extends AbstractExtendedContainerScreen<T> {
    public AbstractPressScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation pTexture) {
        super(pMenu, pPlayerInventory, pTitle, pTexture);
        imageHeight = 175;
    }

    @Override
    protected void init() {
        super.init();
        inventoryLabelY += 9;
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        if (menu.isPressing()) {
            int l = this.menu.getProgress();
            this.blit(pPoseStack, x + 80, y + 38, 176, 14, l + 1, 16);
        }

        if (this.menu.isLit()) {
            int k = this.menu.getLitProgress();
            this.blit(pPoseStack, x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }
    }
}
