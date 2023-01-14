package io.github.gtbauke.unnamedtechmod;

import io.github.gtbauke.unnamedtechmod.init.ModConfiguredFeatures;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.init.ModPlacedFeatures;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UnnamedTechMod.MOD_ID)
public class UnnamedTechMod {
    public static final String MOD_ID = "unnamedtechmod";

    public UnnamedTechMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);

        ModConfiguredFeatures.CONFIGURED_FEATURE.register(bus);
        ModPlacedFeatures.PLACED_FEATURES.register(bus);
    }
}
