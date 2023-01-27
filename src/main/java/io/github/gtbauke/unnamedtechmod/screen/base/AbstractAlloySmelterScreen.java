package io.github.gtbauke.unnamedtechmod.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public abstract class AbstractAlloySmelterScreen<T extends AbstractAlloySmelterMenu> extends AbstractExtendedContainerScreen<T> {
    private final ResourceLocation texture;

    public AbstractAlloySmelterScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation pTexture) {
        super(pMenu, pPlayerInventory, pTitle);
        this.texture = pTexture;
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.texture);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        if (this.menu.isLit()) {
            int k = this.menu.getLitProgress();
            this.blit(pPoseStack, x + 46, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        if (menu.isCrafting()) {
            int l = this.menu.getBurnProgress();
            this.blit(pPoseStack, x + 79, y + 34, 176, 14, l + 1, 16);
        }
    }
}
