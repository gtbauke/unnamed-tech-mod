package io.github.gtbauke.unnamedtechmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.screen.base.AbstractAlloySmelterScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BasicAlloySmelterScreen extends AbstractAlloySmelterScreen<BasicAlloySmelterMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(UnnamedTechMod.MOD_ID, "textures/gui/basic_alloy_smelter.png");

    public BasicAlloySmelterScreen(BasicAlloySmelterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
    }
}
