package io.github.gtbauke.unnamedtechmod;

import io.github.gtbauke.unnamedtechmod.block.entity.ModBlockEntities;
import io.github.gtbauke.unnamedtechmod.config.ModClientConfig;
import io.github.gtbauke.unnamedtechmod.config.ModCommonConfig;
import io.github.gtbauke.unnamedtechmod.init.ModConfiguredFeatures;
import io.github.gtbauke.unnamedtechmod.init.ModBlocks;
import io.github.gtbauke.unnamedtechmod.init.ModItems;
import io.github.gtbauke.unnamedtechmod.init.ModPlacedFeatures;
import io.github.gtbauke.unnamedtechmod.recipe.ModRecipes;
import io.github.gtbauke.unnamedtechmod.screen.BasicAlloySmelterScreen;
import io.github.gtbauke.unnamedtechmod.screen.BasicPressScreen;
import io.github.gtbauke.unnamedtechmod.screen.ManualMaceratorScreen;
import io.github.gtbauke.unnamedtechmod.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(UnnamedTechMod.MOD_ID)
public class UnnamedTechMod {
    public static final String MOD_ID = "unnamedtechmod";
    public static final CreativeModeTab MOD_TAB = new CreativeModeTab("utm_main_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.EXAMPLE_ITEM.get());
        }
    };

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public UnnamedTechMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(bus);
        ModBlocks.register(bus);
        ModBlockEntities.register(bus);
        ModMenuTypes.register(bus);
        ModRecipes.register(bus);
        // ModFluids.register(bus);
        // ModFluidTypes.register(bus);

        ModConfiguredFeatures.CONFIGURED_FEATURE.register(bus);
        ModPlacedFeatures.PLACED_FEATURES.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(
                    ModMenuTypes.BASIC_ALLOY_SMELTER_MENU.get(),
                    BasicAlloySmelterScreen::new
            );

            MenuScreens.register(
                    ModMenuTypes.MANUAL_MACERATOR_MENU.get(),
                    ManualMaceratorScreen::new
            );

            MenuScreens.register(
                    ModMenuTypes.BASIC_PRESS.get(),
                    BasicPressScreen::new
            );
        }
    }
}
