package io.github.gtbauke.unnamedtechmod.screen.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractPressScreen<T extends AbstractPressMenu> extends AbstractExtendedContainerScreen<T> {

    public AbstractPressScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation pTexture) {
        super(pMenu, pPlayerInventory, pTitle, pTexture);
        imageHeight = 175;
        inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        if (menu.isPressing()) {
            int l = this.menu.getProgress();
            this.blit(pPoseStack, x + 80, y + 38, 176, 14, l + 1, 11);
        }

        if (this.menu.isLit()) {
            int k = this.menu.getLitProgress();
            this.blit(pPoseStack, x + 18, y + 45 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int temp = this.menu.getTemperature();
        int SCALE_MIN_TEMP = 0;
        int SCALE_MAX_TEMP = 800;
        int tempScaled = ((temp - SCALE_MIN_TEMP) * 34) / (SCALE_MAX_TEMP - SCALE_MIN_TEMP);
        this.blit(pPoseStack, x + 17, y + 41 - tempScaled, 176, 25 + 33 - tempScaled, 16, tempScaled + 1);

        int recipeTemp = this.menu.getRecipeTemperature();
        if (recipeTemp == 0) return;

        int recipeTempScaled = ((recipeTemp - SCALE_MIN_TEMP) * 34) / (SCALE_MAX_TEMP - SCALE_MIN_TEMP);
        this.blit(pPoseStack, x + 17, y + 41 - recipeTempScaled, 176, 25 + 34, 3, 1);
    }
}
