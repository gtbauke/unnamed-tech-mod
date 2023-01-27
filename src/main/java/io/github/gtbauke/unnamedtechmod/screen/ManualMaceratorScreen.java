package io.github.gtbauke.unnamedtechmod.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.screen.base.AbstractMaceratorScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ManualMaceratorScreen extends AbstractMaceratorScreen<ManualMaceratorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(UnnamedTechMod.MOD_ID, "textures/gui/basic_alloy_smelter.png");

    public ManualMaceratorScreen(ManualMaceratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
    }
}
