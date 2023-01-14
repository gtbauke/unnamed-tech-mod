package io.github.gtbauke.unnamedtechmod;

import io.github.gtbauke.unnamedtechmod.config.ModClientConfig;
import io.github.gtbauke.unnamedtechmod.config.ModCommonConfig;
import io.github.gtbauke.unnamedtechmod.init.ModConfiguredFeatures;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.init.ModPlacedFeatures;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UnnamedTechMod.MOD_ID)
public class UnnamedTechMod {
    public static final String MOD_ID = "unnamedtechmod";

    public UnnamedTechMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(bus);
        ModBlocks.register(bus);

        ModConfiguredFeatures.CONFIGURED_FEATURE.register(bus);
        ModPlacedFeatures.PLACED_FEATURES.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC);
    }
}
