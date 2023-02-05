package io.github.gtbauke.unnamedtechmod.screen;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.screen.base.AbstractPressScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BasicPressScreen extends AbstractPressScreen<BasicPressMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(UnnamedTechMod.MOD_ID, "textures/gui/basic_press.png");

    public BasicPressScreen(BasicPressMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
    }
}
